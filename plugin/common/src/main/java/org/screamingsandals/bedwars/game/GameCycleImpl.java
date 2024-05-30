/*
 * Copyright (C) 2024 ScreamingSandals
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
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.events.TargetInvalidationReason;
import org.screamingsandals.bedwars.api.game.GameCycle;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.upgrades.UpgradeRegistry;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.boss.BossBarImpl;
import org.screamingsandals.bedwars.commands.StatsCommand;
import org.screamingsandals.bedwars.config.GameConfigurationContainerImpl;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.RecordSave;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.events.*;
import org.screamingsandals.bedwars.game.target.AExpirableTarget;
import org.screamingsandals.bedwars.game.target.ExpirableTargetBlockImpl;
import org.screamingsandals.bedwars.game.target.TargetBlockDestroyedInfo;
import org.screamingsandals.bedwars.game.target.TargetBlockImpl;
import org.screamingsandals.bedwars.holograms.StatisticsHolograms;
import org.screamingsandals.bedwars.inventories.TeamSelectorInventory;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.sidebar.GameSidebar;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.EconomyUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.utils.SpawnEffects;
import org.screamingsandals.bedwars.utils.TitleUtils;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.entity.LivingEntity;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.healthindicator.HealthIndicator;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.npc.NPC;
import org.screamingsandals.lib.player.gamemode.GameMode;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.bossbar.BossBarColor;
import org.screamingsandals.lib.spectator.bossbar.BossBarDivision;
import org.screamingsandals.lib.spectator.sound.SoundSource;
import org.screamingsandals.lib.spectator.sound.SoundStart;
import org.screamingsandals.lib.spectator.title.Title;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.Task;
import org.screamingsandals.lib.utils.ResourceLocation;
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.world.gamerule.GameRuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Add configurable game phases and support for phase insertions.
@RequiredArgsConstructor
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
        var tick = preparePhase3();

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

        // Phase 7: Check if game end celebrating started and remove title on bossbar
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
                Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> {
                    if (PlayerStatisticManager.isEnabled()) {
                        var statistic = PlayerStatisticManager.getInstance()
                                .getStatistic(player);
                        game.dispatchRewardCommands("player-end-game", player, statistic.getScore());
                    } else {
                        game.dispatchRewardCommands("player-end-game", player, 0);
                    }
                }, 40, TaskerTime.TICKS);
            }
        }

        if (game.getStatus() == GameStatus.REBUILDING) { // If status is still rebuilding
            game.rebuild();
        }

        if (GameImpl.isBungeeEnabled()) {
            GameManagerImpl.getInstance().reselectGame();

            game.setPreServerRestart(true);

            if (!game.getConnectedPlayers().isEmpty()) {
                game.kickAllPlayers();
            }

            Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> {
                if (MainConfig.getInstance().node("bungee", "serverRestart").getBoolean()) {
                    EventManager.fire(new ServerRestartEventImpl());

                    Server.getConsoleSender().tryToDispatchCommand("restart");
                } else if (MainConfig.getInstance().node("bungee", "serverStop").getBoolean()) {
                    Server.shutdown();
                } else {
                    game.setPreServerRestart(false);
                }
            }, 30, TaskerTime.TICKS);
        }
    }

    private void tickRunningGame(GameChangedStatusEventImpl statusE, GameTickEventImpl tick) {
        var runningTeams = game.getTeamsAlive();
        var expiredTargetBlocks = updateExpirableBlocks(runningTeams);
        var configurationContainer = game.getConfigurationContainer();
        var players = game.getPlayers();
        var teamsInGame = game.getTeamsInGame();

        if (!expiredTargetBlocks.isEmpty()) {
            if (teamsInGame.stream().anyMatch(team -> team.getTarget().isValid())) {
                for (var t : expiredTargetBlocks) {
                    Message
                            .of(t.isItBedBlock() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_BED_CERTAIN_POPULAR_SERVER : (t.isItAnchor() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANCHOR_CERTAIN_POPULAR_SERVER : (t.isItCake() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_CAKE_CERTAIN_POPULAR_SERVER : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANY_CERTAIN_POPULAR_SERVER)))
                            .join(LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_SUBTITLE_VICTIM)
                            .times(TitleUtils.defaultTimes())
                            .title(t.getTeam().getPlayers());
                }
            } else {
                var allBeds = expiredTargetBlocks.stream().allMatch(TargetBlockDestroyedInfo::isItBedBlock);
                var allAnchors = expiredTargetBlocks.stream().allMatch(TargetBlockDestroyedInfo::isItAnchor);
                var allCakes = expiredTargetBlocks.stream().allMatch(TargetBlockDestroyedInfo::isItCake);
                for (var t : expiredTargetBlocks) {
                    Message
                            .of(t.isItBedBlock() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_BED_CERTAIN_POPULAR_SERVER : (t.isItAnchor() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANCHOR_CERTAIN_POPULAR_SERVER : (t.isItCake() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_CAKE_CERTAIN_POPULAR_SERVER : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANY_CERTAIN_POPULAR_SERVER)))
                            .join(allBeds ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ALL_BEDS : (allAnchors ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ALL_ANCHORS : (allCakes ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ALL_CAKES : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ALL_TARGET_BLOCKS)))
                            .times(TitleUtils.defaultTimes())
                            .title(t.getTeam().getPlayers());
                }
            }
        }

        if (runningTeams.size() <= 1) {
            if (runningTeams.size() == 1) {
                var remainingGameTime = game.getGameTime() - game.getCountdown();
                if (remainingGameTime < configurationContainer.getOrDefault(GameConfigurationContainer.PLAYERS_CAN_WIN_GAME_ONLY_AFTER_SECONDS, 0)) {
                    Message.of(LangKeys.IN_GAME_END_YOU_LOST)
                            .join(LangKeys.IN_GAME_END_GAME_ENDED_TOO_EARLY)
                            .title(players);
                } else {
                    TeamImpl winner = null;
                    for (var t : teamsInGame) {
                        if (t.isAlive()) {
                            winner = t;
                            String time = game.getFormattedTimeLeft(remainingGameTime);
                            var message = Message
                                    .of(LangKeys.IN_GAME_END_TEAM_WIN)
                                    .prefixOrDefault(game.getCustomPrefixComponent())
                                    .placeholder("team", Component.text(t.getName(), t.getColor().getTextColor()))
                                    .placeholder("time", time);
                            boolean madeRecord = game.processRecord(t, remainingGameTime);
                            for (BedWarsPlayer player : players) {
                                player.sendMessage(message);
                                if (game.getPlayerTeam(player) == t) {
                                    Message.of(LangKeys.IN_GAME_END_YOU_WON)
                                            .join(LangKeys.IN_GAME_END_TEAM_WIN)
                                            .placeholder("team", Component.text(t.getName(), t.getColor().getTextColor()))
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
                                        if (PlayerStatisticManager.isEnabled()) {
                                            var statistic = PlayerStatisticManager.getInstance().getStatistic(player);
                                            game.dispatchRewardCommands("player-win-run-immediately", player, statistic.getScore());
                                        } else {
                                            game.dispatchRewardCommands("player-win-run-immediately", player, 0);
                                        }
                                        Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> {
                                            if (PlayerStatisticManager.isEnabled()) {
                                                var statistic = PlayerStatisticManager.getInstance().getStatistic(player);
                                                game.dispatchRewardCommands("player-win", player, statistic.getScore());
                                            } else {
                                                game.dispatchRewardCommands("player-win", player, 0);
                                            }
                                        }, (2 + game.getPostGameWaiting()) * 20L, TaskerTime.TICKS);
                                    }
                                } else {
                                    Message.of(LangKeys.IN_GAME_END_YOU_LOST)
                                            .join(LangKeys.IN_GAME_END_TEAM_WIN)
                                            .placeholder("team", Component.text(t.getName(), t.getColor().getTextColor()))
                                            .placeholder("time", time)
                                            .times(TitleUtils.defaultTimes())
                                            .title(player);

                                    if (StatisticsHolograms.isEnabled()) {
                                        StatisticsHolograms.getInstance().updateHolograms(player);
                                    }
                                }
                            }
                            break;
                        }
                    }

                    var endingEvent = new GameEndingEventImpl(game, winner);
                    EventManager.fire(endingEvent);
                }
                EventManager.fire(statusE);
                Debug.info(game.getName() + ": game is ending");

                tick.setNextCountdown(game.getPostGameWaiting());
                tick.setNextStatus(GameStatus.GAME_END_CELEBRATING);
            } else {
                tick.setNextStatus(GameStatus.REBUILDING);
                tick.setNextCountdown(0);
            }
        }
    }

    private ArrayList<TargetBlockDestroyedInfo> updateExpirableBlocks(List<TeamImpl> runningTeams) {
        var expiredTargetBlocks = new ArrayList<TargetBlockDestroyedInfo>();
        for (var team : runningTeams) {
            if (team.getTarget() instanceof AExpirableTarget && team.getTarget().isValid()) {
                var expirableTarget = (AExpirableTarget) team.getTarget();
                expirableTarget.setRemainingTime(expirableTarget.getRemainingTime() - 1);
                if (!team.getTarget().isValid()) {
                    var info = new TargetBlockDestroyedInfo(game, team);
                    if (team.getTarget() instanceof ExpirableTargetBlockImpl
                            && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.USE_CERTAIN_POPULAR_SERVER_TITLES, false)) {
                        expiredTargetBlocks.add(info);
                    }

                    game.internalProcessInvalidation(team, team.getTarget(), null, TargetInvalidationReason.TIMEOUT, true, true);
                }
            }
        }

        return expiredTargetBlocks;
    }

    private void prepareGame(GameChangedStatusEventImpl statusE, GameTickEventImpl tick) {
        var configurationContainer = game.getConfigurationContainer();
        var players = game.getPlayers();

        Debug.info(game.getName() + ": preparing game");
        game.setPreparing(true);
        var startE = new GameStartEventImpl(game);
        EventManager.fire(startE);
        EventManager.fire(statusE);

        if (startE.isCancelled()) {
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

            Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, game::updateSigns, 3, TaskerTime.TICKS);

            if (MainConfig.getInstance().node("use-chunk-tickets-if-available").getBoolean()) {
                game.configureChunkTickets();
            }

            for (GameStoreImpl store : game.getGameStoreList()) {
                var villager = store.spawn();
                if (villager instanceof LivingEntity) {
                    EntitiesManagerImpl.getInstance().addEntityToGame((LivingEntity) villager, game);
                    ((LivingEntity) villager).setAI(false);
                    ((LivingEntity) villager).getLocation().getNearbyEntities(1).forEach(entity -> {
                        if (entity.getEntityType().equals(((LivingEntity) villager).getEntityType()) && entity.getLocation().getBlock().equals(((LivingEntity) villager).getLocation().getBlock()) && !villager.equals(entity)) {
                            entity.remove();
                        }
                    });
                } else if (villager instanceof NPC) {
                    game.getOtherVisuals().add((NPC) villager);
                    game.getPlayers().forEach(((NPC) villager)::addViewer);
                }
            }

            for (ItemSpawnerImpl spawner : game.getSpawners()) {
                spawner.start(game);

                UpgradeStorage storage = UpgradeRegistry.getUpgrade("spawner");
                if (storage != null) {
                    storage.addUpgrade(game, spawner);
                }
            }

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
                    var loc = game.makeSpectator(player, true);
                    player.playSound(
                            SoundStart.sound(
                                    ResourceLocation.of(MainConfig.getInstance().node("sounds", "game_start", "sound").getString("entity.player.levelup")),
                                    SoundSource.AMBIENT,
                                    (float) MainConfig.getInstance().node("sounds", "game_start", "volume").getDouble(1),
                                    (float) MainConfig.getInstance().node("sounds", "game_start", "pitch").getDouble(1)
                            ),
                            loc.getX(),
                            loc.getY(),
                            loc.getZ()
                    );
                } else {
                    player.teleport(team.getRandomSpawn(), () -> {
                        player.setGameMode(GameMode.of("survival"));
                        if (configurationContainer.getOrDefault(GameConfigurationContainer.GAME_START_ITEMS_ENABLED, false)) {
                            var givenGameStartItems = configurationContainer.getOrDefault(GameConfigurationContainerImpl.GAME_START_ITEMS_ITEMS, List.of());
                            if (!givenGameStartItems.isEmpty()) {
                                MiscUtils.giveItemsToPlayer(givenGameStartItems, player, team.getColor());
                            } else {
                                Debug.warn("You have wrongly configured game-start-items.items!", true);
                            }
                        }
                        SpawnEffects.spawnEffect(game, player, "game-effects.start");
                        player.playSound(
                                SoundStart.sound(
                                        ResourceLocation.of(MainConfig.getInstance().node("sounds", "game_start", "sound").getString("entity.player.levelup")),
                                        SoundSource.AMBIENT,
                                        (float) MainConfig.getInstance().node("sounds", "game_start", "volume").getDouble(1),
                                        (float) MainConfig.getInstance().node("sounds", "game_start", "pitch").getDouble(1)
                                )
                        );
                    });
                }
            }

            var teams = game.getTeams();
            var teamsInGame = game.getTeamsInGame();
            if (configurationContainer.getOrDefault(GameConfigurationContainer.REMOVE_UNUSED_TARGET_BLOCKS, false)) {
                for (TeamImpl team : teams) {
                    if (!teamsInGame.contains(team) && team.getTarget() instanceof TargetBlockImpl) {
                        Location loc = ((TargetBlockImpl) team.getTarget()).getTargetBlock();
                        BlockPlacement block = loc.getBlock();
                        var region = game.getRegion();
                        if (region.isBedBlock(block.blockSnapshot())) {
                            region.putOriginalBlock(block.location(), block.blockSnapshot());
                            var neighbor = region.getBedNeighbor(block);
                            region.putOriginalBlock(neighbor.location(), neighbor.blockSnapshot());
                            neighbor.alterBlockWithoutPhysics(Block.air());
                        } else {
                            region.putOriginalBlock(loc, block.blockSnapshot());
                        }
                        block.block(Block.air());
                    }
                }
            }

            for (var team : teamsInGame) {
                team.start();
            }

            if (Server.isVersion(1, 15) && (!configurationContainer.getOrDefault(GameConfigurationContainer.ALLOW_FAKE_DEATH, false))) {
                game.getWorld().setGameRuleValue(GameRuleType.of("doImmediateRespawn"), true);
            }
            game.setPreparing(false);

            var startedEvent = new GameStartedEventImpl(game);
            EventManager.fire(startedEvent);
            EventManager.fire(statusE);
            Debug.info(game.getName() + ": game prepared");

            if (configurationContainer.getOrDefault(GameConfigurationContainer.ENABLE_BELOW_NAME_HEALTH_INDICATOR, false)) {
                var healthIndicator = HealthIndicator.of()
                        .symbol(Component.text("\u2665", Color.RED))
                        .show()
                        .startUpdateTask(4, TaskerTime.TICKS);
                players.forEach(healthIndicator::addViewer);
                players.stream().filter(bedWarsPlayer -> !bedWarsPlayer.isSpectator()).forEach(healthIndicator::addTrackedPlayer);
                game.setHealthIndicator(healthIndicator);
            }
        }

        // show records
        RecordSave.getInstance().getRecord(game.getName()).ifPresentOrElse(record ->
                Message.of(LangKeys.IN_GAME_RECORD_CURRENT)
                        .prefixOrDefault(game.getCustomPrefixComponent())
                        .placeholder("time", game.getFormattedTimeLeft(record.getTime()))
                        .placeholderRaw("team-members", String.join(", ", record.getWinners()))
                        .send(players), () ->
                Message.of(LangKeys.IN_GAME_RECORD_NO)
                        .prefixOrDefault(game.getCustomPrefixComponent())
                        .send(players)
        );
    }

    // TODO: Come up with a better name, or extract this function a bit further.
    private GameTickEventImpl preparePhase3() {
        int nextCountdown = game.getCountdown();
        GameStatus nextStatus = game.getStatus();

        if (game.getStatus() == GameStatus.WAITING) {
            // Game start item that force starts a game, skipping countdowns
            if (game.isGameStartItem()) {
                game.ensurePlayersAreTeamed();

                // if more than one player is in the game, and all the players are in a team, we can go ahead and start the game.
                if (game.getPlayers().size() > 1) {
                    game.setCountdown(0);
                    game.setGameStartItem(false);
                }
            }

            if (game.isForceGameToStart()) {
                game.setForceGameToStart(false);

                nextCountdown = game.getGameTime();
                nextStatus = GameStatus.RUNNING;

                game.ensurePlayersAreTeamed();

                var teamsInGame = game.getTeamsInGame();
                // this logic forces all non-player teams to fake it's existence in case there's only a single player-team in game (Useful for debugging)
                if (teamsInGame.size() == 1) {
                    for (var team : game.getTeams()) {
                        if (!teamsInGame.contains(team)) {
                            team.setForced(true);
                            teamsInGame.add(team);
                            break;
                        }
                    }
                }
            } else if (game.isAllowedToStart()) {
                if (game.getCountdown() == 0) {
                    nextCountdown = game.getGameTime();
                    nextStatus = GameStatus.RUNNING;
                } else {
                    nextCountdown--;
                    // It's counting down, so let's show the player the countdown when necessary.
                    showPlayerCountdown();
                }
            } else {
                game.setCountdown(game.getPauseCountdown());
                nextCountdown = game.getPauseCountdown();
            }

            game.setBossbarProgress(game.getCountdown(), game.getPauseCountdown());
        } else if (game.getStatus() == GameStatus.RUNNING) {
            if (game.getCountdown() == 0) {
                nextCountdown = game.getPostGameWaiting();
                nextStatus = GameStatus.GAME_END_CELEBRATING;
            } else {
                nextCountdown--;
            }
            game.setBossbarProgress(game.getCountdown(), game.getGameTime());
        } else if (game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
            if (game.getCountdown() == 0) {
                nextStatus = GameStatus.REBUILDING;
            } else {
                nextCountdown--;
            }
            game.setBossbarProgress(game.getCountdown(), game.getPostGameWaiting());
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

    private void showPlayerCountdown() {
        var countdown = game.getCountdown();
        if (countdown <= 10 && countdown >= 1 && countdown != game.getPreviousCountdown()) {
            for (BedWarsPlayer player : game.getPlayers()) {
                player.showTitle(Title.title(Component.text(Integer.toString(countdown), Color.YELLOW), Component.empty(), TitleUtils.defaultTimes()));
                player.playSound(
                        SoundStart.sound(
                                ResourceLocation.of(MainConfig.getInstance().node("sounds", "countdown", "sound").getString("ui.button.click")),
                                SoundSource.AMBIENT,
                                (float) MainConfig.getInstance().node("sounds", "countdown", "volume").getDouble(1),
                                (float) MainConfig.getInstance().node("sounds", "countdown", "pitch").getDouble(1)
                        )
                );
            }
        }
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
        game.updateSigns();

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
