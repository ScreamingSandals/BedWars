/*
 * Copyright (C) 2025 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.game;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.events.TargetInvalidationReason;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameCycle;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.game.target.TargetBlock;
import org.screamingsandals.bedwars.boss.BossBarImpl;
import org.screamingsandals.bedwars.commands.StatsCommand;
import org.screamingsandals.bedwars.config.GameConfigurationContainerImpl;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.RecordSave;
import org.screamingsandals.bedwars.events.*;
import org.screamingsandals.bedwars.game.target.AExpirableTarget;
import org.screamingsandals.bedwars.game.target.ExpirableTargetBlockImpl;
import org.screamingsandals.bedwars.game.target.TargetBlockDestroyedInfo;
import org.screamingsandals.bedwars.holograms.StatisticsHolograms;
import org.screamingsandals.bedwars.inventories.TeamSelectorInventory;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.sidebar.GameSidebar;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.EconomyUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.utils.SignUtils;
import org.screamingsandals.bedwars.utils.SpawnEffects;
import org.screamingsandals.bedwars.utils.TitleUtils;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.item.meta.PotionEffectType;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.bossbar.BossBarColor;
import org.screamingsandals.lib.spectator.bossbar.BossBarDivision;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.Task;
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.world.gamerule.GameRuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
// TODO: Add configurable game phases and support for phase insertions.
public class GameCycleImpl implements GameCycle {
    private final GameImpl game;
    /**
     * Game task instance that handles the updates towards phases in the Game cycle.
     */
    private Task task;


    private void runCycle() {
        // Phase 1: Check if game is running
        if (game.getStatus() == GameStatus.DISABLED) { // Game is not running, why cycle is still running?
            endGameCycle();
            return;
        }

        var statusE = new GameChangedStatusEventImpl(game);

        // Phase 2: If this is first tick, prepare waiting lobby
        if (game.getCountdown() == -1 && game.getStatus() == GameStatus.WAITING) {
            prepareGameLobby();
        }

        // Phase 3: Prepare information about next tick for tick event and update
        // boss bar with scoreboard, then we call the tick event
        var tick = tickAndUpdateCountdown();

        // Phase 4: Update Previous information
        game.setPreviousCountdown(game.getCountdown());
        game.setPreviousStatus(game.getStatus());

        // Phase 5: Process tick
        // Phase 5.1: If status changed
        if (game.getStatus() != tick.getNextStatus()) {
            // Phase 5.1.1: Prepare game if next status is RUNNING
            if (tick.getNextStatus() == GameStatus.RUNNING) {
                prepareGame(statusE, tick);
            }
            // Phase 5.2: If status is same as before
        } else {
            // Phase 5.2.1: On game tick (if not interrupted by a change of status)
            if (game.getStatus() == GameStatus.RUNNING) {
                tickRunningGame(statusE, tick);
            }
        }

        // Phase 6: Update status and countdown for next tick
        game.setCountdown(tick.getNextCountdown());
        game.setStatus(tick.getNextStatus());

        // Phase 7: Check if game end celebrating started and remove title on boss-bar
        if (game.getStatus() == GameStatus.GAME_END_CELEBRATING && game.hasGameStatusChanged()) {
            var statusbar = game.getStatusBar();
            if (statusbar instanceof BossBarImpl) {
                var bossbar = (BossBarImpl) statusbar;
                bossbar.setMessage(Component.empty());
            }
        }

        // Phase 8: Check if status is rebuilding and rebuild game
        if (game.getStatus() == GameStatus.REBUILDING) {
            rebuildGameArena(statusE);
        }
    }

    private void rebuildGameArena(GameChangedStatusEventImpl statusE) {
        var event = new GameEndEventImpl(game);
        EventManager.fire(event);
        EventManager.fire(statusE);

        var message = Message
                .of(LangKeys.IN_GAME_END_GAME_END)
                .prefixOrDefault(game.getCustomPrefixComponent());

        for (BedWarsPlayer player : List.copyOf(game.getPlayers())) {
            player.sendMessage(message);
            player.changeGame(null);

            if (MainConfig.getInstance().node("rewards", "enabled").getBoolean()) {
                game.dispatchEndGameReward(player);
            }
        }

        if (game.getStatus() == GameStatus.REBUILDING) { // If status is still rebuilding
            game.rebuild();
        }

        if (GameImpl.isBungeeEnabled()) {
            game.handleBungeePostGame();
        }
    }

    private void tickRunningGame(GameChangedStatusEventImpl statusE, GameTickEventImpl tick) {
        var runningTeams = game.getTeamsAlive();
        updateExpirableTargetBlocks(runningTeams);
        processTraps(runningTeams);

        var players = game.getPlayers();
        var teamsInGame = game.getTeamsInGame();


        // game over, let's rebuild the arena.
        if (runningTeams.isEmpty()) {
            tick.setNextStatus(GameStatus.REBUILDING);
            tick.setNextCountdown(0);
            return;
        }

        // if there's only one team remaining, the game has ended, let's start game end preparations.
        if (runningTeams.size() == 1) {
            var remainingGameTime = game.getGameTime() - game.getCountdown();
            if (remainingGameTime < game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.PLAYERS_CAN_WIN_GAME_ONLY_AFTER_SECONDS, 0)) {
                Message.of(LangKeys.IN_GAME_END_YOU_LOST)
                        .join(LangKeys.IN_GAME_END_GAME_ENDED_TOO_EARLY)
                        .title(players);
            } else {
                TeamImpl winner = null;

                for (var team : teamsInGame) {
                    if (team.isAlive())
                        winner = team;
                }

                // safety checks, unnecessary really but better safe than sorry.
                if (winner == null) {
                    EventManager.fire(statusE);
                    tick.setNextCountdown(game.getPostGameWaiting());
                    tick.setNextStatus(GameStatus.GAME_END_CELEBRATING);
                    return;
                }

                String time = GameImpl.getFormattedTimeLeft(remainingGameTime);
                var message = Message
                        .of(LangKeys.IN_GAME_END_TEAM_WIN)
                        .prefixOrDefault(game.getCustomPrefixComponent())
                        .placeholder("team", Component.text(winner.getName(), winner.getColor().getTextColor()))
                        .placeholder("time", time);

                boolean madeRecord = game.processRecord(winner, remainingGameTime);

                for (BedWarsPlayer player : players) {
                    player.sendMessage(message);

                    if (game.getPlayerTeam(player) == winner) {
                        handlePlayerWin(player, winner, time, madeRecord);
                    } else {
                        Message.of(LangKeys.IN_GAME_END_YOU_LOST)
                                .join(LangKeys.IN_GAME_END_TEAM_WIN)
                                .placeholder("team", Component.text(winner.getName(), winner.getColor().getTextColor()))
                                .placeholder("time", time)
                                .times(TitleUtils.defaultTimes())
                                .title(player);

                        if (StatisticsHolograms.isEnabled()) {
                            StatisticsHolograms.getInstance().updateHolograms(player);
                        }
                    }
                }

                var endingEvent = new GameEndingEventImpl(game, winner);
                EventManager.fire(endingEvent);

                game.dispatchRewardCommands("team-win", null, 0, winner, null, null);
                for (var member : winner.getTeamMembers()) {
                    game.dispatchRewardCommands("player-team-win", null, 0, winner, winner.getPlayers().stream().anyMatch(p -> p.getUniqueId().equals(member.getUuid())), member);
                }
            }
            EventManager.fire(statusE);
            Debug.info(game.getName() + ": game is ending");

            tick.setNextCountdown(game.getPostGameWaiting());
            tick.setNextStatus(GameStatus.GAME_END_CELEBRATING);
        }
    }

    private void handlePlayerWin(BedWarsPlayer player, TeamImpl winner, String time, boolean madeRecord) {
        var configurationContainer = game.getConfigurationContainer();
        Message.of(LangKeys.IN_GAME_END_YOU_WON)
                .join(LangKeys.IN_GAME_END_TEAM_WIN)
                .placeholder("team", Component.text(winner.getName(), winner.getColor().getTextColor()))
                .placeholder("time", time)
                .times(TitleUtils.defaultTimes())
                .title(player);
        EconomyUtils.deposit(player, configurationContainer.getOrDefault(GameConfigurationContainer.ECONOMY_REWARD_WIN, 0.0));

        SpawnEffects.spawnEffect(game, player, "game-effects.end");

        if (PlayerStatisticManager.isEnabled()) {
            var statistic = PlayerStatisticManager.getInstance()
                    .getStatistic(player);
            statistic.addWins(1);
            statistic.addScore(configurationContainer.getOrDefault(GameConfigurationContainer.STATISTICS_SCORES_WIN, 50));

            if (madeRecord) {
                statistic.addScore(configurationContainer.getOrDefault(GameConfigurationContainer.STATISTICS_SCORES_RECORD, 100));
            }

            if (StatisticsHolograms.isEnabled()) {
                StatisticsHolograms.getInstance().updateHolograms(player);
            }

            if (MainConfig.getInstance().node("statistics", "show-on-game-end")
                    .getBoolean()) {
                StatsCommand.sendStats(player, PlayerStatisticManager.getInstance().getStatistic(player));
            }
        }

        if (MainConfig.getInstance().node("rewards", "enabled").getBoolean()) {
            game.dispatchPlayerWinReward(player, winner);
        }
    }

    private void updateExpirableTargetBlocks(List<TeamImpl> runningTeams) {
        var expiredTargetBlocks = new ArrayList<TargetBlockDestroyedInfo>();
        for (var team : runningTeams) {
            var target = team.getTarget();
            if (target instanceof AExpirableTarget && target.isValid()) {
                var expirableTarget = (AExpirableTarget) team.getTarget();
                expirableTarget.setRemainingTime(expirableTarget.getRemainingTime() - 1);

                if (!expirableTarget.isValid()) {
                    var info = new TargetBlockDestroyedInfo(game, team);
                    if (expirableTarget instanceof ExpirableTargetBlockImpl
                            && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.USE_CERTAIN_POPULAR_SERVER_TITLES, false)) {
                        expiredTargetBlocks.add(info);
                    }

                    game.internalProcessInvalidation(team, expirableTarget, null, TargetInvalidationReason.TIMEOUT, true, true);
                }
            }
        }

        if (expiredTargetBlocks.isEmpty()) {
            return;
        }

        // if no target blocks are valid, we send all target destroyed message to players.
        if (runningTeams.stream().noneMatch(team -> team.getTarget().isValid())) {
            var allBeds = expiredTargetBlocks.stream().allMatch(TargetBlockDestroyedInfo::isItBedBlock);
            var allAnchors = expiredTargetBlocks.stream().allMatch(TargetBlockDestroyedInfo::isItAnchor);
            var allCakes = expiredTargetBlocks.stream().allMatch(TargetBlockDestroyedInfo::isItCake);
            for (var t : expiredTargetBlocks) {
                Message
                        .of(t.isItBedBlock() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_BED_CERTAIN_POPULAR_SERVER
                                : (t.isItAnchor() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANCHOR_CERTAIN_POPULAR_SERVER
                                : (t.isItCake() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_CAKE_CERTAIN_POPULAR_SERVER
                                : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANY_CERTAIN_POPULAR_SERVER)))
                        .join(allBeds ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ALL_BEDS
                                : (allAnchors ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ALL_ANCHORS
                                : (allCakes ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ALL_CAKES
                                : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ALL_TARGET_BLOCKS)))
                        .times(TitleUtils.defaultTimes())
                        .title(t.getTeam().getPlayers());
            }
            // or else we show messages on the specific targets that were destroyed.
        } else {
            for (var t : expiredTargetBlocks) {
                Message
                        .of(t.isItBedBlock() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_BED_CERTAIN_POPULAR_SERVER
                                : (t.isItAnchor() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANCHOR_CERTAIN_POPULAR_SERVER
                                : (t.isItCake() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_CAKE_CERTAIN_POPULAR_SERVER
                                : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANY_CERTAIN_POPULAR_SERVER)))
                        .join(LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_SUBTITLE_VICTIM)
                        .times(TitleUtils.defaultTimes())
                        .title(t.getTeam().getPlayers());
            }
        }
    }

    private void processTraps(@NotNull List<@NotNull TeamImpl> teams) {
        for (var team : teams) {
            var target = team.getTarget();
            Location location;
            if (target instanceof TargetBlock) {
                location = ((TargetBlock) target).getTargetBlock().as(Location.class);
            } else {
                // this team does not have locatable target, we have to use one of the spawns
                location = team.getTeamSpawns().get(0);
            }

            for (var trap : List.copyOf(team.getTraps())) {
                var squared = trap.getDetectionRange() * trap.getDetectionRange();

                var stream = game.getConnectedPlayers().stream()
                        .filter(player -> !player.isSpectator());
                if (trap.isEnemies() && !trap.isTeam()) {
                    stream = stream.filter(player -> !team.isPlayerInTeam(player));
                } else if (!trap.isEnemies() && trap.isTeam()) {
                    stream = stream.filter(team::isPlayerInTeam);
                }
                stream.filter(player -> location.getDistanceSquared(player.getLocation()) <= squared)
                        .forEach(player -> {
                            var event = new TrapTriggeredEventImpl(game, player, team, trap.getEffects(), trap.isTeam(), trap.isEnemies(), trap.isSingularUse(), trap.getDetectionRange());
                            EventManager.fire(event);

                            if (event.cancelled()) {
                                return;
                            }

                            player.addPotionEffects(trap.getEffects());

                            if (trap.isSingularUse()) {
                                team.getTraps().remove(trap);
                            }

                            // TODO: invisible players? removing invisibility?

                            if (trap.getName() != null) {
                                if (trap.getMessage() != null) {
                                    player.sendMessage(
                                            Message.ofRichText(trap.getMessage())
                                                    .prefix(game.getCustomPrefixComponent())
                                                    .placeholder("team", Component.text(team.getName(), team.getColor().getTextColor()))
                                                    .placeholder("trap", trap.getName())
                                    );
                                }

                                if (trap.getTeamTitle() != null) {
                                    var title = Message
                                            .ofRichText(trap.getTeamTitle())
                                            .joinRichText(trap.getTeamSubtitle() != null ? trap.getTeamSubtitle() : "")
                                            .placeholder("trap", trap.getName())
                                            .times(TitleUtils.defaultTimes());

                                    team.getPlayers().forEach(pl -> pl.showTitle(title));
                                }

                                var triggerSound = trap.getTriggerSound();
                                if (triggerSound != null) {
                                    team.getPlayers().forEach(pl -> pl.playSound(triggerSound));
                                }
                            }
                        });
            }
        }
    }

    private void prepareGame(GameChangedStatusEventImpl statusE, GameTickEventImpl tick) {
        var configurationContainer = game.getConfigurationContainer();
        Debug.info(game.getName() + ": preparing game");
        game.setPreparing(true);

        var players = game.getPlayers();

        var gameStartEvent = new GameStartEventImpl(game);
        EventManager.fire(gameStartEvent);
        EventManager.fire(statusE);

        if (gameStartEvent.isCancelled()) {
            tick.setNextCountdown(game.getPauseCountdown());
            tick.setNextStatus(GameStatus.WAITING);
            game.setPreparing(false);
        } else {
            if (configurationContainer.getOrDefault(GameConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY, false)) {
                game.makePlayersJoinRandomTeams();
            }

            var statusbar = game.getStatusBar();
            statusbar.setProgress(0);
            statusbar.setVisible(configurationContainer.getOrDefault(GameConfigurationContainer.BOSSBAR_GAME_ENABLED, false));

            if (statusbar instanceof BossBarImpl) {
                var bossbar = (BossBarImpl) statusbar;
                bossbar.setMessage(Message.of(LangKeys.IN_GAME_BOSSBAR_RUNNING).asComponent());
                bossbar.setColor(configurationContainer.getOrDefault(GameConfigurationContainerImpl.BOSSBAR_GAME_COLOR, BossBarColor.PURPLE));
                bossbar.setStyle(configurationContainer.getOrDefault(GameConfigurationContainerImpl.BOSSBAR_GAME_DIVISION, BossBarDivision.NO_DIVISION));
            }

            var teamSelectorInventory = game.getTeamSelectorInventory();
            if (teamSelectorInventory != null)
                teamSelectorInventory.destroy();
            game.setTeamSelectorInventory(null);

            Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> SignUtils.updateSigns(game), 3, TaskerTime.TICKS);

            if (MainConfig.getInstance().node("use-chunk-tickets-if-available").getBoolean()) {
                game.configureChunkTickets();
            }

            game.spawnGameStores();
            game.startGameSpawners();

            var title = Message
                    .of(LangKeys.IN_GAME_GAME_START_TITLE)
                    .join(LangKeys.IN_GAME_GAME_START_SUBTITLE)
                    .placeholder("arena", game.getName())
                    .times(TitleUtils.defaultTimes());

            if (configurationContainer.getOrDefault(GameConfigurationContainer.SHOW_GAME_INFO_ON_START, false)) {
                Message
                        .of(LangKeys.IN_GAME_MESSAGES_GAME_START)
                        .placeholderRaw("resources", MiscUtils.center(game.getGameVariant().getItemSpawnerTypes().stream().map(ItemSpawnerTypeImpl::getName).collect(Collectors.joining(", ")), 49))
                        .send(players);
            }

            for (BedWarsPlayer player : players) {
                Debug.info(game.getName() + ": moving " + player.getName() + " into game");
                var team = game.getPlayerTeam(player);
                player.getPlayerInventory().clear();
                // Player still had armor on legacy versions
                player.getPlayerInventory().setHelmet(null);
                player.getPlayerInventory().setChestplate(null);
                player.getPlayerInventory().setLeggings(null);
                player.getPlayerInventory().setBoots(null);
                player.showTitle(title);

                if (team == null) {
                    game.spawnSpectatorOnGameStart(player);
                } else {
                    game.spawnTeamPlayerOnGameStart(player, team);
                }
            }

            if (configurationContainer.getOrDefault(GameConfigurationContainer.REMOVE_UNUSED_TARGET_BLOCKS, false)) {
                game.removeUnusedTargetBlocks();
            }

            for (var team : game.getTeamsInGame()) {
                team.start();
            }

            if (Server.isVersion(1, 15) && (!configurationContainer.getOrDefault(GameConfigurationContainer.ALLOW_FAKE_DEATH, false))) {
                game.getWorld().setGameRuleValue(GameRuleType.of("doImmediateRespawn"), true);
            }
            if (Server.isVersion(1, 21, 6) && MainConfig.getInstance().node("disable-locator-bars-in-arena-worlds").getBoolean(true)) {
                game.getWorld().setGameRuleValue(GameRuleType.of("locatorBar"), false);
            }

            game.setPreparing(false);

            var startedEvent = new GameStartedEventImpl(game);
            EventManager.fire(startedEvent);
            EventManager.fire(statusE);
            Debug.info(game.getName() + ": game prepared");

            if (configurationContainer.getOrDefault(GameConfigurationContainer.ENABLE_BELOW_NAME_HEALTH_INDICATOR, false)) {
                game.startHealthIndicator();
            }

            for (var player : players) {
                game.dispatchRewardCommands("player-game-start", player, 0, game.getPlayerTeam(player), null, null);
            }
            game.dispatchRewardCommands("game-start", null, 0);
        }

        // show records
        RecordSave.getInstance().getRecord(game.getName()).ifPresentOrElse(record ->
                Message.of(LangKeys.IN_GAME_RECORD_CURRENT)
                        .prefixOrDefault(game.getCustomPrefixComponent())
                        .placeholder("time", GameImpl.getFormattedTimeLeft(record.getTime()))
                        .placeholderRaw("team-members", String.join(", ", record.getWinners()))
                        .send(players), () ->
                Message.of(LangKeys.IN_GAME_RECORD_NO)
                        .prefixOrDefault(game.getCustomPrefixComponent())
                        .send(players)
        );
    }


    private GameTickEventImpl tickAndUpdateCountdown() {
        int nextCountdown = game.getCountdown();
        GameStatus nextStatus = game.getStatus();

        switch (game.getStatus()) {
            case WAITING:
                // Game start item that force starts a game, skipping countdowns
                if (game.isGameStartItem()) {
                    game.useGameStartItem();
                }

                if (game.isForceGameToStart()) {
                    game.forceGameStart();

                    nextCountdown = game.getGameTime();
                    nextStatus = GameStatus.RUNNING;
                } else if (game.isAllowedToStart()) {
                    if (game.getCountdown() == 0) {
                        nextCountdown = game.getGameTime();
                        nextStatus = GameStatus.RUNNING;
                    } else {
                        nextCountdown--;

                        var countdown = game.getCountdown();
                        // It's counting down, so let's show the player the countdown when it hits 10 seconds or below.
                        if (countdown <= 10 && countdown >= 1 && countdown != game.getPreviousCountdown()) {
                            game.showPlayerCountdown();
                        }
                    }
                } else {
                    game.setCountdown(game.getPauseCountdown());
                    nextCountdown = game.getPauseCountdown();
                }

                game.setBossbarProgress(game.getCountdown(), game.getPauseCountdown());
                break;
            case RUNNING:
                if (game.getCountdown() == 0) {
                    nextCountdown = game.getPostGameWaiting();
                    nextStatus = GameStatus.GAME_END_CELEBRATING;
                } else {
                    nextCountdown--;
                }
                game.setBossbarProgress(game.getCountdown(), game.getGameTime());
                break;
            case GAME_END_CELEBRATING:
                if (game.getCountdown() == 0) {
                    nextStatus = GameStatus.REBUILDING;
                } else {
                    nextCountdown--;
                }
                game.setBossbarProgress(game.getCountdown(), game.getPostGameWaiting());
                break;
        }

        // Phase 4: Call Tick Event
        var tick = new GameTickEventImpl(
                game,
                game.getPreviousCountdown(),
                game.getPreviousStatus(),
                game.getCountdown(),
                game.getStatus(),
                nextCountdown, nextStatus, nextCountdown, nextStatus
        );
        EventManager.fire(tick);
        Debug.info(game.getName() + ": tick passed: " + tick.getPreviousCountdown() + "," + tick.getCountdown() + "," + tick.getNextCountdown() + " (" + tick.getPreviousStatus() + "," + tick.getStatus() + "," + tick.getNextStatus() + ")");

        return tick;
    }

    private void prepareGameLobby() {
        Debug.info(game.getName() + ": preparing lobby");

        game.setPreviousCountdown(game.getPauseCountdown());
        game.setCountdown(game.getPauseCountdown());

        var title = Message.of(LangKeys.IN_GAME_BOSSBAR_WAITING).asComponent();
        var statusbar = game.getStatusBar();
        var configurationContainer = game.getConfigurationContainer();
        statusbar.setProgress(0);
        statusbar.setVisible(configurationContainer.getOrDefault(GameConfigurationContainer.BOSSBAR_LOBBY_ENABLED, false));

        for (BedWarsPlayer p : game.getPlayers()) {
            statusbar.addPlayer(p);
        }

        if (statusbar instanceof BossBarImpl) {
            var bossbar = (BossBarImpl) statusbar;
            bossbar.setMessage(title);
            bossbar.setColor(configurationContainer.getOrDefault(GameConfigurationContainerImpl.BOSSBAR_LOBBY_COLOR, BossBarColor.PURPLE));
            bossbar.setStyle(configurationContainer.getOrDefault(GameConfigurationContainerImpl.BOSSBAR_LOBBY_DIVISION, BossBarDivision.NO_DIVISION));
        }

        var teamSelectorInventory = game.getTeamSelectorInventory();
        if (teamSelectorInventory == null) {
            teamSelectorInventory = new TeamSelectorInventory(game);
            game.setTeamSelectorInventory(teamSelectorInventory);
        }

        var experimentalBoard = game.getExperimentalBoard();
        if (experimentalBoard == null) {
            experimentalBoard = new GameSidebar(game);
            game.setExperimentalBoard(experimentalBoard);
        }
        SignUtils.updateSigns(game);

        game.setPreviousStatus(GameStatus.WAITING);
        Debug.info(game.getName() + ": lobby prepared");
    }

    public void startGameCycle() {
        endGameCycle();
        task = Tasker.runRepeatedly(DefaultThreads.GLOBAL_THREAD, this::runCycle, 1, TaskerTime.SECONDS);
    }

    public void endGameCycle() {
        if (task != null) {
            if (task.isScheduledOrRunning()) {
                task.cancel();
            }
            task = null;
        }
    }
}
