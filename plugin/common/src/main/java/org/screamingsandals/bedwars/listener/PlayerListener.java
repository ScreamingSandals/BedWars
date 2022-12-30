/*
 * Copyright (C) 2022 ScreamingSandals
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

package org.screamingsandals.bedwars.listener;

import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.PlatformService;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.events.TargetInvalidationReason;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.commands.admin.JoinTeamCommand;
import org.screamingsandals.bedwars.config.GameConfigurationContainerImpl;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.special.listener.ProtectionWallListener;
import org.screamingsandals.bedwars.special.listener.RescuePlatformListener;
import org.screamingsandals.bedwars.utils.EconomyUtils;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.events.PlayerDeathMessageSendEventImpl;
import org.screamingsandals.bedwars.events.PlayerKilledEventImpl;
import org.screamingsandals.bedwars.events.PlayerRespawnedEventImpl;
import org.screamingsandals.bedwars.events.TeamChestOpenEventImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.target.TargetBlockImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.*;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.SpecialSoundKey;
import org.screamingsandals.lib.attribute.AttributeHolder;
import org.screamingsandals.lib.attribute.AttributeTypeHolder;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.entity.EntityLiving;
import org.screamingsandals.lib.entity.EntityProjectile;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.event.EventPriority;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.*;
import org.screamingsandals.lib.event.player.*;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.placeholders.PlaceholderManager;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.sound.SoundSource;
import org.screamingsandals.lib.spectator.sound.SoundStart;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class PlayerListener {
    private final List<PlayerWrapper> explosionAffectedPlayers = new ArrayList<>();

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onPlayerDeath(SPlayerDeathEvent event) {
        final var victim = event.player();

        if (PlayerManagerImpl.getInstance().isPlayerInGame(victim)) {
            Debug.info(victim.getName() + " died in a BedWars game, processing his death...");
            final var gVictim = victim.as(BedWarsPlayer.class);
            final var game = gVictim.getGame();
            final var victimTeam = game.getPlayerTeam(gVictim);
            final var victimColor = victimTeam.getColor().getTextColor();
            final var drops = List.copyOf(event.drops());
            int respawnTime = game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.RESPAWN_COOLDOWN_TIME, 5);

            if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.KEEP_ARMOR_ON_DEATH, false)) {
                final var armorContents = victim.getPlayerInventory().getArmorContents();
                gVictim.setArmorContents(armorContents);
                Debug.info(victim.getName() + "'s armor contents: " +
                        Arrays.stream(armorContents)
                                .filter(Objects::nonNull)
                                .map(stack -> stack.getMaterial().platformName())
                                .collect(Collectors.toList()));

            }

            event.keepInventory(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.KEEP_INVENTORY_ON_DEATH, false));
            event.droppedExp(0);

            if (game.getStatus() == GameStatus.RUNNING) {
                Debug.info(victim.getName() + " died while game was running");
                if (!game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.PLAYER_DROPS, false)) {
                    event.drops().clear();
                }

                if (MainConfig.getInstance().node("chat", "send-death-messages-just-in-game").getBoolean()) {
                    var deathMessage = event.deathMessage();
                    Message deathMessageMsg = null;
                    final var killer = event.killer();
                    if (MainConfig.getInstance().node("chat", "send-custom-death-messages").getBoolean()) {
                        if (killer != null && PlayerManagerImpl.getInstance().isPlayerInGame(killer)) {
                            Debug.info(victim.getName() + " died because entity " + killer.getName() + " killed him");
                            final var gKiller = killer.as(BedWarsPlayer.class);
                            final var killerTeam = game.getPlayerTeam(gKiller);
                            final var killerColor = killerTeam.getColor().getTextColor();

                            deathMessageMsg = Message.of(LangKeys.IN_GAME_PLAYER_KILLED)
                                    .prefixOrDefault(game.getCustomPrefixComponent())
                                    .placeholder("victim", victim.getDisplayName().withColor(victimColor))
                                    .placeholder("killer", killer.getDisplayName().withColor(killerColor))
                                    .placeholder("victimteam", Component.text(victimTeam.getName(), victimColor))
                                    .placeholder("killerteam", Component.text(killerTeam.getName(), killerColor));
                        } else {
                            deathMessageMsg = Message.of(LangKeys.IN_GAME_PLAYER_SELF_KILLED)
                                    .prefixOrDefault(game.getCustomPrefixComponent())
                                    .placeholder("victim", victim.getDisplayName().withColor(victimColor))
                                    .placeholder("victimteam", Component.text(victimTeam.getName(), victimColor));
                        }

                    }
                    if (deathMessage != null) {
                        var bpdmsEvent = new PlayerDeathMessageSendEventImpl(game, gVictim, deathMessageMsg != null ? deathMessageMsg : Message.ofPlainText(deathMessage));
                        EventManager.fire(bpdmsEvent);
                        if (!bpdmsEvent.cancelled()) {
                            event.deathMessage(null);
                            bpdmsEvent.getMessage().send(game.getConnectedPlayers());
                        }
                    }
                }

                var team = game.getPlayerTeam(gVictim);
                SpawnEffects.spawnEffect(game, gVictim, "game-effects.kill");
                boolean isBed = team.getTarget().isValid();
                if (isBed && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TARGET_BLOCK_RESPAWN_ANCHOR_ENABLE_DECREASE, false) && team.getTarget() instanceof TargetBlockImpl && ((TargetBlockImpl) team.getTarget()).getTargetBlock().getBlock().getType().isSameType("respawn_anchor")) {
                    var targetBlockLoc = ((TargetBlockImpl) team.getTarget()).getTargetBlock();
                    var anchor = targetBlockLoc.getBlock().getType();
                    int charges = anchor.get("charges").map(Integer::parseInt).orElse(0);
                    if (charges > 0) {
                        var c = charges - 1;
                        targetBlockLoc.getBlock().setType(anchor.with("charges", String.valueOf(c)));
                        if (c == 0) {
                            targetBlockLoc.getWorld().playSound(SoundStart.sound(
                                    SpecialSoundKey.key(MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "deplete").getString("block.respawn_anchor.deplete")),
                                    SoundSource.BLOCK,
                                    1,
                                    1
                            ), targetBlockLoc.getX(), targetBlockLoc.getY(), targetBlockLoc.getZ());
                        } else {
                            targetBlockLoc.getWorld().playSound(SoundStart.sound(
                                    SpecialSoundKey.key(MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "deplete").getString("block.glass.break")),
                                    SoundSource.BLOCK,
                                    1,
                                    1
                            ), targetBlockLoc.getX(), targetBlockLoc.getY(), targetBlockLoc.getZ());
                        }
                    } else {
                        isBed = false;
                    }
                }
                if (!isBed) {
                    Debug.info(victim.getName() + " died without bed, he's going to spectate the game");
                    gVictim.setSpectator(true);
                    team.getPlayers().remove(gVictim);
                    if (PlayerStatisticManager.isEnabled()) {
                        var statistic = PlayerStatisticManager.getInstance().getStatistic(victim);
                        statistic.addLoses(1);
                        statistic.addScore(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.STATISTICS_SCORES_LOSE, 0));
                    }
                }

                boolean onlyOnBedDestroy = game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.STATISTICS_BED_DESTROYED_KILLS, false);

                var killer = event.killer();
                if (killer != null && PlayerManagerImpl.getInstance().isPlayerInGame(killer)) {
                    var gKiller = killer.as(BedWarsPlayer.class);
                    if (gKiller.getGame() == game) {
                        if (!onlyOnBedDestroy || !isBed) {
                            game.dispatchRewardCommands("player-kill", killer,
                                    game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.STATISTICS_SCORES_KILL, 10));
                        }
                        if (!isBed) {
                            game.dispatchRewardCommands("player-final-kill", killer,
                                    game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.STATISTICS_SCORES_FINAL_KILL, 10));
                        }
                        if (team.isDead()) {
                            SpawnEffects.spawnEffect(game, gVictim, "game-effects.teamkill");

                            killer.playSound(SoundStart.sound(
                                    SpecialSoundKey.key(MainConfig.getInstance().node("sounds", "team_kill", "sound").getString("entity.player.levelup")),
                                    SoundSource.AMBIENT,
                                    (float) MainConfig.getInstance().node("sounds", "team_kill", "volume").getDouble(),
                                    (float) MainConfig.getInstance().node("sounds", "team_kill", "pitch").getDouble()
                            ));
                        } else {
                            killer.playSound(SoundStart.sound(
                                    SpecialSoundKey.key(MainConfig.getInstance().node("sounds", "player_kill", "sound").getString("entity.generic.big_fall")),
                                    SoundSource.AMBIENT,
                                    (float) MainConfig.getInstance().node("sounds", "player_kill", "volume").getDouble(),
                                    (float) MainConfig.getInstance().node("sounds", "player_kill", "pitch").getDouble()
                            ));
                            if (!isBed) {
                                EconomyUtils.deposit(killer, game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.ECONOMY_REWARD_FINAL_KILL, 0.0));
                            } else {
                                EconomyUtils.deposit(killer, game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.ECONOMY_REWARD_KILL, 0.0));
                            }
                        }
                    }
                }

                var killedEvent = new PlayerKilledEventImpl(game, killer != null && PlayerManagerImpl.getInstance().isPlayerInGame(killer) ? killer.as(BedWarsPlayer.class) : null, gVictim, drops);
                EventManager.fire(killedEvent);

                if (PlayerStatisticManager.isEnabled()) {
                    var diePlayer = PlayerStatisticManager.getInstance().getStatistic(victim);
                    PlayerStatisticImpl killerPlayer;

                    if (!onlyOnBedDestroy || !isBed) {
                        diePlayer.addDeaths(1);
                        diePlayer.addScore(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.STATISTICS_SCORES_DIE, 0));
                    }

                    if (killer != null) {
                        if (!onlyOnBedDestroy || !isBed) {
                            killerPlayer = PlayerStatisticManager.getInstance().getStatistic(killer);
                            if (killerPlayer != null) {
                                killerPlayer.addKills(1);
                                killerPlayer.addScore(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.STATISTICS_SCORES_KILL, 10));

                                if (!isBed) {
                                    killerPlayer.addScore(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.STATISTICS_SCORES_FINAL_KILL, 10));
                                }
                            }
                        }
                    }
                }
            }
            if (!Server.isVersion(1, 15) && (!PlatformService.getInstance().getFakeDeath().isAvailable() || !game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.ALLOW_FAKE_DEATH, false))) {
                Debug.info(victim.getName() + " is going to be respawned via spigot api");
                PlatformService.getInstance().respawnPlayer(victim, 3L);
            }
            if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.RESPAWN_COOLDOWN_ENABLED, false)
                    && victimTeam.isAlive()
                    && !gVictim.isSpectator()) {
                game.makeSpectator(gVictim, false);
                Debug.info(victim.getName() + " is in respawn cooldown");

                final var livingTime = new AtomicInteger(respawnTime);
                final var task = new AtomicReference<TaskerTask>();
                task.set(
                        Tasker.build(() -> {
                                    if (livingTime.get() > 0) {
                                        Message
                                                .of(LangKeys.IN_GAME_RESPAWN_COOLDOWN_TITLE)
                                                .placeholder("time", livingTime.get())
                                                .times(TitleUtils.defaultTimes())
                                                .title(gVictim);
                                        gVictim.playSound(SoundStart.sound(
                                                SpecialSoundKey.key(MainConfig.getInstance().node("sounds", "respawn_cooldown_wait", "sound").getString("block.stone_button.click_on")),
                                                SoundSource.AMBIENT,
                                                (float) MainConfig.getInstance().node("sounds", "respawn_cooldown_wait", "volume").getDouble(),
                                                (float) MainConfig.getInstance().node("sounds", "respawn_cooldown_wait", "pitch").getDouble()
                                        ));
                                    }

                                    livingTime.decrementAndGet();
                                    if (livingTime.get() == 0) {
                                        game.makePlayerFromSpectator(gVictim);
                                        gVictim.playSound(
                                                SoundStart.sound(
                                                        SpecialSoundKey.key(MainConfig.getInstance().node("sounds", "respawn_cooldown_done", "sound").getString("ui.button.click")),
                                                        SoundSource.AMBIENT,
                                                        (float) MainConfig.getInstance().node("sounds", "respawn_cooldown_done", "volume").getDouble(1),
                                                        (float) MainConfig.getInstance().node("sounds", "respawn_cooldown_done", "pitch").getDouble(1)
                                                )
                                        );
                                        task.get().cancel();
                                    }
                                })
                                .delay(20, TaskerTime.TICKS)
                                .repeat(20, TaskerTime.TICKS)
                                .start()
                );
            }
        }
    }

    @OnEvent
    public void onPlayerQuit(SPlayerLeaveEvent event) {
        if (PlayerManagerImpl.getInstance().isPlayerRegistered(event.player())) {
            var gPlayer = event.player().as(BedWarsPlayer.class);
            if (gPlayer.isInGame()) {
                gPlayer.forceSynchronousTeleportation = true;
                gPlayer.changeGame(null);
            }
            PlayerManagerImpl.getInstance().dropPlayer(gPlayer);
        }

        if (MainConfig.getInstance().node("disable-server-message", "player-join").getBoolean()) {
            event.leaveMessage(null);
        }
    }

    @OnEvent
    public void onPlayerJoin(SPlayerJoinEvent event) {
        var player = event.player();

        if (GameImpl.isBungeeEnabled() && MainConfig.getInstance().node("bungee", "auto-game-connect").getBoolean()) {
            Debug.info(event.player().getName() + " joined the server and auto-game-connect is enabled. Registering task...");
            Tasker.build(() -> {
                        try {
                            Debug.info("Selecting game for " + event.player().getName());
                            var gameManager = GameManagerImpl.getInstance();
                            var game = gameManager.getFirstWaitingGame().or(gameManager::getFirstRunningGame);
                            if (game.isEmpty()) { // still nothing?
                                if (!player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                                    Debug.info(event.player().getName() + " is not connecting to any game! Kicking...");
                                    BungeeUtils.movePlayerToBungeeServer(player, false);
                                }
                                return;
                            }
                            Debug.info(event.player().getName() + " is connecting to " + game.get().getName());

                            game.get().joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player));
                        } catch (NullPointerException ignored) {
                            if (!player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                                Debug.info(event.player().getName() + " is not connecting to any game! Kicking...");
                                BungeeUtils.movePlayerToBungeeServer(player, false);
                            }
                        }
                    })
                    .delay(1, TaskerTime.TICKS)
                    .start();
        }

        if (MainConfig.getInstance().node("disable-server-message", "player-join").getBoolean()) {
            event.joinMessage(null);
        }

        if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-foreign-players").getBoolean()) {
            Server.getConnectedPlayers().stream().filter(PlayerManagerImpl.getInstance()::isPlayerInGame).forEach(p -> PlayerManagerImpl.getInstance().getPlayer(p).orElseThrow().hidePlayer(player));
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onPlayerRespawn(SPlayerRespawnEvent event) {
        if (PlayerManagerImpl.getInstance().isPlayerInGame(event.player())) {
            Debug.info(event.player().getName() + " is respawning in BedWars game");
            var gPlayer = event.player().as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            var team = game.getPlayerTeam(gPlayer);

            if (game.getStatus() == GameStatus.WAITING) {
                Debug.info(event.player().getName() + " is in lobby");
                event.location(gPlayer.getGame().getLobbySpawn());
                return;
            }
            Debug.info(event.player().getName() + " is in game");
            // clear inventory to fix issue 148
            if (!game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.KEEP_INVENTORY_ON_DEATH, false)) {
                event.player().getPlayerInventory().clear();
            }
            if (gPlayer.isSpectator()) {
                Debug.info(event.player().getName() + " is going to be spectator");
                if (team == null) {
                    event.location(MiscUtils.findEmptyLocation(gPlayer.getGame().makeSpectator(gPlayer, true)));
                } else {
                    event.location(MiscUtils.findEmptyLocation(gPlayer.getGame().makeSpectator(gPlayer, false)));
                }
            } else {
                Debug.info(event.player().getName() + " is going to play the game");
                event.location(gPlayer.getGame().getPlayerTeam(gPlayer).getRandomSpawn());

                var respawnEvent = new PlayerRespawnedEventImpl(game, gPlayer);
                EventManager.fire(respawnEvent);

                if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.RESPAWN_PROTECTION_ENABLED, true)) {
                    game.addProtectedPlayer(gPlayer).runProtection();
                }

                SpawnEffects.spawnEffect(gPlayer.getGame(), gPlayer, "game-effects.respawn");
                if (gPlayer.getGame().getConfigurationContainer().getOrDefault(GameConfigurationContainer.PLAYER_RESPAWN_ITEMS_ENABLED, false)) {
                    var playerRespawnItems = gPlayer.getGame().getConfigurationContainer().getOrDefault(GameConfigurationContainerImpl.PLAYER_RESPAWN_ITEMS_ITEMS, List.of());
                    if (!playerRespawnItems.isEmpty()) {
                        MiscUtils.giveItemsToPlayer(playerRespawnItems, gPlayer, team.getColor());
                    } else {
                        Debug.warn("You have wrongly configured player-respawn-items.items!", true);
                    }
                }

                if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.KEEP_ARMOR_ON_DEATH, false)) {
                    final var armorContents = gPlayer.getArmorContents();
                    if (armorContents != null) {
                        gPlayer.getPlayerInventory().setArmorContents(armorContents);
                    }
                }

                MiscUtils.giveItemsToPlayer(gPlayer.getPermanentItemsPurchased(), gPlayer, team.getColor());
            }
        }
    }

    @OnEvent
    public void onPlayerWorldChange(SPlayerWorldChangeEvent event) {
        if (PlayerManagerImpl.getInstance().isPlayerInGame(event.player())) {
            var gPlayer = event.player().as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (!game.getWorld().equals(event.player().getLocation().getWorld())
                    && !game.getLobbySpawn().getWorld().equals(event.player().getLocation().getWorld())) {
                gPlayer.changeGame(null);
                Debug.info(event.player().getName() + " changed world while in BedWars arena. Kicking...");
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onBlockPlace(SPlayerBlockPlaceEvent event) {
        if (event.cancelled()) {
            if (MainConfig.getInstance().node("event-hacks", "place").getBoolean() && PlayerManagerImpl.getInstance().isPlayerInGame(event.player())) {
                event.cancelled(false);
            } else {
                return;
            }
        }

        if (PlayerManagerImpl.getInstance().isPlayerInGame(event.player())) {
            var gPlayer = event.player().as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (game.getStatus() == GameStatus.WAITING) {
                event.cancelled(true);
                Debug.info(event.player().getName() + " attempted to place a block, canceled");
                return;
            }
            if (!game.blockPlace(gPlayer, event.block(),
                    event.replacedBlockState(), event.itemInHand())) {
                event.cancelled(true);
                Debug.info(event.player().getName() + " attempted to place a block, canceled");
            } else {
                Debug.info(event.player().getName() + " attempted to place a block, allowed");
            }
        } else if (MainConfig.getInstance().node("preventArenaFromGriefing").getBoolean()) {
            for (var game : GameManagerImpl.getInstance().getGames()) {
                if (game.getStatus() != GameStatus.DISABLED && ArenaUtils.isInArea(event.block().getLocation(), game.getPos1(), game.getPos2())) {
                    event.cancelled(true);
                    Debug.info(event.player().getName() + " attempted to place a block in protected area while not playing BedWars game, canceled");
                    return;
                }
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onBlockBreak(SPlayerBlockBreakEvent event) {
        if (event.cancelled()) {
            if (MainConfig.getInstance().node("event-hacks", "destroy").getBoolean() && PlayerManagerImpl.getInstance().isPlayerInGame(event.player())) {
                event.cancelled(false);
            } else {
                return;
            }
        }

        if (PlayerManagerImpl.getInstance().isPlayerInGame(event.player())) {
            final var gamePlayer = event.player().as(BedWarsPlayer.class);
            final var game = gamePlayer.getGame();
            final var block = event.block();

            if (game.getStatus() == GameStatus.WAITING) {
                Debug.info(event.player().getName() + " attempted to break a block, canceled");
                event.cancelled(true);
                return;
            }
            if (event.block().getType().platformName().toLowerCase().contains("bed") && !MainConfig.getInstance().node("bed-can-be-breaked-by", "player").getBoolean()) {
                Debug.info(event.player().getName() + " attempted to break a bed manually");
                event.cancelled(true);
                return;
            }

            if (!game.blockBreak(gamePlayer, event.block(), event)) {
                event.cancelled(true);
                Debug.info(event.player().getName() + " attempted to break a block, cancelled");
            } else {
                Debug.info(event.player().getName() + " attempted to break a block, allowed");
            }

            //Fix for obsidian dropping
            if (game.getStatus() == GameStatus.RUNNING && gamePlayer.isInGame()) {
                if (block.getType().isSameType("ender_chest")) {
                    event.dropItems(false);
                }
            }
        } else if (MainConfig.getInstance().node("preventArenaFromGriefing").getBoolean()) {
            for (var game : GameManagerImpl.getInstance().getGames()) {
                if (game.getStatus() != GameStatus.DISABLED && ArenaUtils.isInArea(event.block().getLocation(), game.getPos1(), game.getPos2())) {
                    event.cancelled(true);
                    Debug.info(event.player().getName() + " attempted to break a block in protected area while not in BedWars game, canceled");
                    return;
                }
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onCommandExecuted(SPlayerCommandPreprocessEvent event) {
        if (event.cancelled()) {
            return;
        }

        final var player = event.player();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            final var message = event.command();
            final var gamePlayer = event.player().as(BedWarsPlayer.class);
            if (BedWarsPlugin.isCommandLeaveShortcut(message)) {
                event.cancelled(true);
                gamePlayer.changeGame(null);
            } else if (!BedWarsPlugin.isCommandAllowedInGame(message.split(" ")[0])) {
                //Allow players with permissions to use all commands
                if (player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                    Debug.info(event.player().getName() + " attempted to execute a command, allowed");
                    return;
                }

                Debug.info(event.player().getName() + " attempted to execute a command, canceled");
                event.cancelled(true);
                player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_COMMAND_IS_NOT_ALLOWED).prefixOrDefault(gamePlayer.getGame().getCustomPrefixComponent()));
                return;
            }
            Debug.info(event.player().getName() + " attempted to execute a command, allowed");
        }
    }

    @OnEvent
    public void onInventoryClick(SPlayerInventoryClickEvent event) {
        if (event.inventory() == null) {
            return;
        }

        if (event.inventory().getType().is("player")) {
            var p = event.player();
            if (PlayerManagerImpl.getInstance().isPlayerInGame(p)) {
                var gPlayer = p.as(BedWarsPlayer.class);
                var game = gPlayer.getGame();
                if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator()) {
                    event.cancelled(true);
                    Debug.info(p.getName() + " used item in lobby or as spectator");
                    if (event.getClickType().isLeftClick() || event.getClickType().isRightClick()) {
                        var item = event.currentItem();
                        if (item != null) {
                            p.closeInventory();
                            if (item.getMaterial().is(MainConfig.getInstance().node("items", "jointeam").getString("COMPASS"))) {
                                if (game.getStatus() == GameStatus.WAITING) {
                                    var inv = game.getTeamSelectorInventory();
                                    if (inv == null) {
                                        return;
                                    }
                                    inv.openForPlayer(gPlayer);
                                } else if (gPlayer.isSpectator()) {
                                    // TODO
                                }
                            } else if (item.getMaterial().is(MainConfig.getInstance().node("items", "startgame").getString("DIAMOND"))) {
                                if (game.getStatus() == GameStatus.WAITING && p.hasPermission(BedWarsPermission.START_ITEM_PERMISSION.asPermission())) {
                                    if (game.checkMinPlayers()) {
                                        game.gameStartItem = true;
                                    } else {
                                        p.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_VIP_NOT_ENOUGH_PLAYERS).prefixOrDefault(game.getCustomPrefixComponent()));
                                    }
                                }
                            } else if (item.getMaterial().is(MainConfig.getInstance().node("items", "leavegame").getString("SLIME_BALL"))) {
                                game.leaveFromGame(gPlayer);
                            }
                        }
                    }
                }
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGH)
    public void onHunger(SFoodLevelChangeEvent event) {
        if (!(event.entity().getEntityType().is("player")) || event.cancelled()) {
            return;
        }

        var player = (PlayerWrapper) event.entity();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator()) {
                event.cancelled(true);
                Debug.info(player.getName() + " tried to eat while eating is not allowed");
            }

            if (game.getStatus() == GameStatus.RUNNING && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.DISABLE_HUNGER, false)) {
                event.cancelled(true);
                Debug.info(player.getName() + " tried to eat while eating is not allowed");
            }
        }
    }

    @OnEvent
    public void onCraft(SPlayerCraftItemEvent event) {
        if (event.cancelled()) {
            return;
        }

        var player = event.player();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            if (gPlayer.getGame().getStatus() != GameStatus.RUNNING) {
                event.cancelled(true);
                Debug.info(player.getName() + " tried to craft while crafting is not allowed");
            } else if (!gPlayer.getGame().getConfigurationContainer().getOrDefault(GameConfigurationContainer.ALLOW_CRAFTING, false)) {
                event.cancelled(true);
                Debug.info(player.getName() + " tried to craft while crafting is not allowed");
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onDamage(SEntityDamageEvent event) {
        if (event.cancelled()) {
            if (MainConfig.getInstance().node("event-hacks", "damage").getBoolean()
                    && event.entity().getEntityType().is("player")
                    && PlayerManagerImpl.getInstance().isPlayerInGame(event.entity().getUniqueId())) {
                event.cancelled(false);
            } else {
                return;
            }
        }

        final var entity = event.entity();

        if (!entity.getEntityType().is("player")) {
            if (!event.damageCause().is("void")) {
                var game = EntitiesManagerImpl.getInstance().getGameOfEntity(entity);
                if (game.isPresent()) {
                    if (game.get().isEntityShop(entity) && game.get().getConfigurationContainer().getOrDefault(GameConfigurationContainer.PREVENT_KILLING_VILLAGERS, false)) {
                        Debug.info("Game entity was damaged, cancelling");
                        event.cancelled(true);
                    }
                }
            }

            if (event instanceof SEntityDamageByEntityEvent) {
                if (event.entity().getEntityType().is("armor_stand")) {
                    var damager = ((SEntityDamageByEntityEvent) event).damager();
                    if (damager.getEntityType().is("player")) {
                        var player = (PlayerWrapper) event.entity();
                        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                            var gPlayer = player.as(BedWarsPlayer.class);
                            if (gPlayer.getGame().getStatus() == GameStatus.WAITING || gPlayer.isSpectator()) {
                                Debug.info(player.getName() + " damaged armor stand in lobby, cancelling");
                                event.cancelled(true);
                            }
                        }
                    }
                }
            }
            return;
        }

        var player = (PlayerWrapper) event.entity();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            Debug.info(player.getName() + " was damaged in game");
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (gPlayer.isSpectator()) {
                if (event.damageCause().is("void")) {
                    gPlayer.setFallDistance(0);
                    gPlayer.teleport(game.getSpecSpawn());
                }
                event.cancelled(true);
            } else if (game.getStatus() == GameStatus.WAITING) {
                if (event.damageCause().is("void")) {
                    gPlayer.setFallDistance(0);
                    gPlayer.teleport(game.getLobbySpawn());
                }
                event.cancelled(true);
            } else if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (gPlayer.isSpectator()) {
                    event.cancelled(true);
                }
                if (game.isProtectionActive(gPlayer) && !event.damageCause().is("void")) {
                    event.cancelled(true);
                    return;
                }

                if (event.damageCause().is("void") && gPlayer.getHealth() > 0.5) {
                    gPlayer.setHealth(0.5);
                } else if (event.damageCause().is("fall")) {
                    if (explosionAffectedPlayers.contains(player)) {
                        event.damage(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TNT_JUMP_FALL_DAMAGE, 0.75));
                        explosionAffectedPlayers.remove(player);
                    }
                } else if (event instanceof SEntityDamageByEntityEvent) {
                    var edbee = (SEntityDamageByEntityEvent) event;

                    if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TNT_JUMP_ENABLED, false) && edbee.damager().getEntityType().is("tnt")) {
                        final var tnt = edbee.damager();
                        final var playerSource = PlatformService.getInstance().getSourceOfTnt(tnt);
                        if (playerSource != null) {
                            if (playerSource.equals(player)) {
                                event.damage(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TNT_JUMP_SOURCE_DAMAGE, 0.5));
                                var tntVector = tnt.getLocation().asVector();
                                var vector = player
                                        .getLocation()
                                        .clone()
                                        .add(0, game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TNT_JUMP_ACCELERATION_Y, 0), 0)
                                        .asVector()
                                        .add(-tntVector.getX(), -tntVector.getY(), -tntVector.getZ()).normalize();

                                vector.setY(vector.getY() / game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TNT_JUMP_REDUCE_Y, 0.0));
                                vector.multiply(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TNT_JUMP_LAUNCH_MULTIPLIER, 0));
                                player.setVelocity(vector);
                                explosionAffectedPlayers.add(player);
                            }
                            if (!game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TNT_JUMP_TEAM_DAMAGE, true)) {
                                if (game.getPlayerTeam(gPlayer).equals(game.getPlayerTeam(PlayerManagerImpl.getInstance().getPlayer(playerSource.getUniqueId()).orElseThrow()))) {
                                    event.cancelled(true);
                                }
                            }
                        }

                    } else if (edbee.damager().getEntityType().is("player")) {
                        var damager = (PlayerWrapper) ((SEntityDamageByEntityEvent) event).damager();
                        if (PlayerManagerImpl.getInstance().isPlayerInGame(damager)) {
                            var gDamager = damager.as(BedWarsPlayer.class);
                            if (gDamager.isSpectator() || (gDamager.getGame().getPlayerTeam(gDamager) == game.getPlayerTeam(gPlayer) && !game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.FRIENDLYFIRE, false))) {
                                event.cancelled(true);
                            }
                        }
                    } else if (edbee.damager().getEntityType().is("firework") && game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                        event.cancelled(true);
                    } else if (edbee.damager() instanceof EntityProjectile) {
                        var projectile = (EntityProjectile) edbee.damager();
                        if (projectile.getEntityType().is("minecraft:fireball", "minecraft:small_fireball", "minecraft:dragon_fireball") && game.getStatus() == GameStatus.RUNNING) {
                            final double damage = MainConfig.getInstance().node("specials", "throwable-fireball", "damage").getDouble(); // TODO: special items may have custom configuration
                            event.damage(damage);
                        } else if (projectile.getShooter() instanceof PlayerWrapper) {
                            var damager = projectile.getShooter().as(PlayerWrapper.class);
                            if (PlayerManagerImpl.getInstance().isPlayerInGame(damager)) {
                                var gDamager = damager.as(BedWarsPlayer.class);
                                if (gDamager.isSpectator() || gDamager.getGame().getPlayerTeam(gDamager) == game.getPlayerTeam(gPlayer) && !game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.FRIENDLYFIRE, false)) {
                                    event.cancelled(true);
                                }
                            }
                        }
                    }
                }

                // TODO: check this, there was final damage before
                if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.ALLOW_FAKE_DEATH, false) && !event.cancelled() && (player.getHealth() - event.damage() <= 0)) {
                    var fakeDeath = PlatformService.getInstance().getFakeDeath();
                    if (fakeDeath.isAvailable()) {
                        event.cancelled(true);
                        Debug.info(player.getName() + " is going to be respawned via FakeDeath");
                        fakeDeath.die(gPlayer);
                    }
                }
            }
        }
    }

    @OnEvent
    public void onLaunchProjectile(SProjectileLaunchEvent event) {
        if (event.cancelled()) {
            return;
        }

        var projectile = event.entity();
        if (projectile.getShooter() instanceof PlayerWrapper) {
            var damager = projectile.getShooter().as(PlayerWrapper.class);
            if (PlayerManagerImpl.getInstance().isPlayerInGame(damager)) {
                if (damager.as(BedWarsPlayer.class).isSpectator()) {
                    event.cancelled(true);
                    Debug.info(damager.getName() + " tried to launch projectile as spectator");
                }
            }
        }
    }

    @OnEvent
    public void onDrop(SPlayerDropItemEvent event) {
        if (event.cancelled()) {
            return;
        }
        var player = event.player();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            if (gPlayer.getGame().getStatus() != GameStatus.RUNNING || gPlayer.isSpectator()) {
                event.cancelled(true);
                Debug.info(player.getName() + " tried to drop an item as spectator or in lobby");
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onFly(SPlayerToggleFlightEvent event) {
        if (event.cancelled()) {
            return;
        }

        var player = event.player();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var bwPlayer = player.as(BedWarsPlayer.class);
            if (!bwPlayer.isSpectator() && !player.hasPermission(BedWarsPermission.BYPASS_FLIGHT_PERMISSION.asPermission())
                    && bwPlayer.getGame().getConfigurationContainer().getOrDefault(GameConfigurationContainer.DISABLE_FLIGHT, false)) {
                event.cancelled(true);
                Debug.info(player.getName() + " tried to fly, canceled");
            }
        }
    }

    @SuppressWarnings("deprecation")
    @OnEvent
    public void onPlayerInteract(SPlayerInteractEvent event) {
        var player = event.player();
        if ((event.cancelled() && event.action() != SPlayerInteractEvent.Action.RIGHT_CLICK_AIR) || !PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }
        var gPlayer = player.as(BedWarsPlayer.class);
        var game = Objects.requireNonNull(gPlayer.getGame());

        final var clickedBlock = event.clickedBlock();
        if (event.action() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK
                && clickedBlock != null && clickedBlock.getType().is("minecraft:chest")
                && game.getStatus() == GameStatus.WAITING) {
            event.cancelled(true);
            return;
        }

        if (event.action() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator()) {
                Debug.info(player.getName() + " used item in lobby or as spectator");
                event.cancelled(true);
                if (event.material().is(MainConfig.getInstance().node("items", "jointeam").getString("COMPASS"))) {
                    if (game.getStatus() == GameStatus.WAITING) {
                        var inv = game.getTeamSelectorInventory();
                        if (inv == null) {
                            return;
                        }
                        inv.openForPlayer(gPlayer);
                    } else if (gPlayer.isSpectator()) {
                        // TODO
                    }
                } else if (event.material().is(MainConfig.getInstance().node("items", "startgame").getString("DIAMOND"))) {
                    if (game.getStatus() == GameStatus.WAITING && (player.hasPermission(BedWarsPermission.START_ITEM_PERMISSION.asPermission()))) {
                        if (game.checkMinPlayers()) {
                            game.gameStartItem = true;
                        } else {
                            player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_VIP_NOT_ENOUGH_PLAYERS).prefixOrDefault(game.getCustomPrefixComponent()));
                        }
                    }
                } else if (event.material().is(MainConfig.getInstance().node("items", "leavegame").getString("SLIME_BALL"))) {
                    game.leaveFromGame(gPlayer);
                }
            } else if (game.getStatus() == GameStatus.RUNNING) {
                if (clickedBlock != null) {
                    if (clickedBlock.getType().isSameType("ender_chest")) {
                        var team = game.getTeamOfChest(clickedBlock.getLocation());
                        event.cancelled(true);

                        if (team == null) {
                            player.openInventory(game.getFakeEnderChest(gPlayer));
                            Debug.info(player.getName() + " opened personal ender chest");
                            return;
                        }

                        if (!team.getPlayers().contains(gPlayer)) {
                            player.sendMessage(Message.of(LangKeys.SPECIALS_TEAM_CHEST_NOT_YOURS).prefixOrDefault(game.getCustomPrefixComponent()));
                            Debug.info(player.getName() + " tried to open foreign team chest");
                            return;
                        }

                        var teamChestOpenEvent = new TeamChestOpenEventImpl(game, gPlayer, team);
                        EventManager.fire(teamChestOpenEvent);

                        if (teamChestOpenEvent.cancelled()) {
                            return;
                        }

                        player.openInventory(team.getTeamChestInventory());
                        Debug.info(player.getName() + " opened team chest");
                    } else if (clickedBlock.getBlockState().orElseThrow().holdsInventory()) {
                        var inventory = clickedBlock.getBlockState().orElseThrow().getInventory().orElseThrow();
                        game.addChestForFutureClear(clickedBlock.getLocation(), inventory);
                        Debug.info(player.getName() + " used chest in BedWars game");

                    } else if (clickedBlock.getType().platformName().toLowerCase().contains("cake")) {
                        if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TARGET_BLOCK_CAKE_DESTROY_BY_EATING, false)) {
                            var pt = game.getPlayerTeam(gPlayer);
                            if (pt.getTarget() instanceof TargetBlockImpl && ((TargetBlockImpl) pt.getTarget()).getTargetBlock().equals(clickedBlock.getLocation())) {
                                event.cancelled(true);
                            } else {
                                if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.DISABLE_CAKE_EATING, true)) {
                                    event.cancelled(true);
                                }
                                Debug.info(player.getName() + " is eating cake");
                                for (var team : game.getActiveTeams()) {
                                    if (team.getTarget() instanceof TargetBlockImpl && ((TargetBlockImpl) team.getTarget()).getTargetBlock().equals(clickedBlock.getLocation())) {
                                        event.cancelled(true);
                                        var cake = clickedBlock.getType();
                                        if (cake.get("bites").map("0"::equals).orElse(true)) {
                                            game.getRegion().putOriginalBlock(clickedBlock.getLocation(), clickedBlock.getBlockState().orElseThrow());
                                        }
                                        var bites = cake.get("bites").map(Integer::parseInt).orElse(0) + 1;
                                        cake = cake.with("bites", String.valueOf(bites));

                                        if (bites >= 6) {
                                            game.internalProcessInvalidation(team, team.getTarget(), event.player(), TargetInvalidationReason.TARGET_BLOCK_EATEN, false, false);
                                        } else {
                                            clickedBlock.setType(cake);
                                        }
                                        break;
                                    }
                                }
                            }
                        } else if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.DISABLE_CAKE_EATING, true)) {
                            event.cancelled(true);
                        }
                    } else if (clickedBlock.getType().is("dragon_egg") && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.DISABLE_DRAGON_EGG_TELEPORT, true)) {
                        event.cancelled(true); // Fix - #432
                    }
                }
            }

            if (clickedBlock != null) {
                if (game.getRegion().isBedBlock(clickedBlock.getBlockState().orElseThrow()) || clickedBlock.getType().isSameType("respawn_anchor")) {
                    // prevent Essentials to set home in arena
                    event.cancelled(true);

                    if (event.action() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator()) {
                        var stack = event.item();
                        if (stack != null && stack.getAmount() > 0) {
                            boolean anchorFilled = false;
                            var pt = game.getPlayerTeam(gPlayer);
                            if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.TARGET_BLOCK_RESPAWN_ANCHOR_ENABLE_DECREASE, false)
                                    && clickedBlock.getType().isSameType("respawn_anchor")
                                    && pt.getTarget() instanceof TargetBlockImpl && ((TargetBlockImpl) pt.getTarget()).getTargetBlock().equals(clickedBlock.getLocation())
                                    && event.item() != null && event.item().getMaterial().is("glowstone")) {
                                Debug.info(player.getName() + " filled respawn anchor");
                                var anchor = clickedBlock.getType();
                                int charges = anchor.get("charges").map(Integer::parseInt).orElse(0);
                                charges++;
                                if (charges <= 4) {
                                    anchorFilled = true;
                                    clickedBlock.setType(anchor.with("charges", String.valueOf(charges)));
                                    stack.changeAmount(stack.getAmount() - 1);

                                    clickedBlock.getLocation().getWorld().playSound(SoundStart.sound(
                                            SpecialSoundKey.key(MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "charge").getString("block.respawn_anchor.charge")),
                                            SoundSource.BLOCK,
                                            1,
                                            1
                                    ), clickedBlock.getLocation().getX(), clickedBlock.getLocation().getY(), clickedBlock.getLocation().getZ());
                                }
                            }

                            if (
                                    !anchorFilled
                                    && stack.getMaterial().block().isPresent()

                                    /* These special items don't work with the feature below */
                                    && ItemUtils.getIfStartsWith(stack, ProtectionWallListener.PROTECTION_WALL_PREFIX) == null
                                    && ItemUtils.getIfStartsWith(stack, RescuePlatformListener.RESCUE_PLATFORM_PREFIX) == null
                            ) {
                                var face = event.blockFace();
                                var block = clickedBlock.getLocation().clone().add(face.getDirection()).getBlock();
                                if (block.getType().isAir()) {
                                    var originalState = block.getBlockState().orElseThrow();
                                    block.setType(stack.getMaterial().block().orElseThrow());
                                    var bEvent = PlatformService.getInstance().fireFakeBlockPlaceEvent(
                                            block, originalState, clickedBlock, stack, player, true
                                    );

                                    if (bEvent.cancelled()) {
                                        originalState.updateBlock(true, false);
                                    } else {
                                        if (!player.getGameMode().is("creative")) {
                                            stack.changeAmount(stack.getAmount() - 1);
                                        }
                                        if (!player.isSneaking()) {
                                            // TODO get right block place sound
                                            block.getLocation().getWorld().playSound(SoundStart.sound(
                                                    SpecialSoundKey.key("minecraft:block.stone.place"),
                                                    SoundSource.BLOCK, 1, 1
                                            ), block.getLocation().getX(), block.getLocation().getY(), block.getLocation().getZ());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (event.action() == SPlayerInteractEvent.Action.LEFT_CLICK_BLOCK &&
                game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator()
                && clickedBlock != null && clickedBlock.getType().isSameType("dragon_egg")
                && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.DISABLE_DRAGON_EGG_TELEPORT, true)) {
            event.cancelled(true);
            Debug.info(player.getName() + " interacts with dragon egg");
            var blockBreakEvent = PlatformService.getInstance().fireFakeBlockBreakEvent(clickedBlock, player);
            if (blockBreakEvent.cancelled()) {
                return;
            }
            if (blockBreakEvent.dropItems()) {
                clickedBlock.breakNaturally();
            } else {
                clickedBlock.setType(BlockTypeHolder.air());
            }
        }
    }

    @OnEvent
    public void onEntityInteract(SPlayerInteractEntityEvent event) {
        if (event.cancelled()) {
            return;
        }

        var player = event.player();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if ((game.getStatus() == GameStatus.WAITING && !(event instanceof SPlayerInteractAtEntityEvent)) || gPlayer.isSpectator()) {
                event.cancelled(true);
                Debug.info(player.getName() + " interacts with entity in lobby or as spectator");
            }
        }
    }

    @OnEvent
    public void onSleep(SPlayerBedEnterEvent event) {
        if (event.cancelled()) {
            return;
        }

        if (PlayerManagerImpl.getInstance().isPlayerInGame(event.player())) {
            event.cancelled(true);
        } else {
            for (var game : GameManagerImpl.getInstance().getGames()) {
                if (ArenaUtils.isInArea(event.bed().getLocation(), game.getPos1(), game.getPos2())) {
                    event.cancelled(true);
                    Debug.info(event.player().getName() + " tried to sleep");
                    return;
                }
            }
        }
    }

    @OnEvent
    public void onInventoryOpen(SPlayerInventoryOpenEvent event) {
        if (event.cancelled()) {
            return;
        }

        var player = event.player();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gProfile = player.as(BedWarsPlayer.class);
            if (gProfile.getGame().getStatus() == GameStatus.RUNNING) {
                if (gProfile.isSpectator()) {
                    event.cancelled(!event.topInventory().getType().is("player"));
                    Debug.info(player.getName() + " tried to open prohibited inventory");
                    return;
                }
                if (event.topInventory().getType().is("enchanting", "crafting", "anvil", "brewing", "furnace", "workbench")) {
                    if (!gProfile.getGame().getConfigurationContainer().getOrDefault(GameConfigurationContainer.ALLOW_CRAFTING, false)) {
                        event.cancelled(true);
                        Debug.info(player.getName() + " tried to open prohibited inventory");
                    }
                }
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGH)
    public void onInteractAtEntity(SPlayerInteractAtEntityEvent event) {
        if (event.cancelled()) {
            return;
        }

        var player = event.player();
        var entity = event.clickedEntity();

        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (!(entity instanceof EntityLiving)) {
                return;
            }

            if (game.getStatus() != GameStatus.WAITING) {
                return;
            }
            var displayName = entity.getCustomName() != null ? entity.getCustomName().toPlainText() : null;

            for (var team : game.getTeams()) {
                if (team.getName().equals(displayName)) {
                    event.cancelled(true);
                    Debug.info(player.getName() + " selected his team with armor stand");
                    game.selectTeam(gPlayer, displayName);
                    return;
                }
            }
        } else if (player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
            var value = JoinTeamCommand.TEAMS_IN_HAND.get(player.getUuid());
            if (value == null) {
                return;
            }

            event.cancelled(true);

            if (!(entity instanceof EntityLiving)) {
                player.sendMessage(Message.of(LangKeys.ADMIN_TEAM_JOIN_ENTITY_ENTITY_NOT_COMPATIBLE).defaultPrefix());
                return;
            }

            var living = (EntityLiving) entity;
            living.setRemoveWhenFarAway(false);
            living.setCanPickupItems(false);
            living.setCustomName(Component.text(value.getName(), value.getColor().getTextColor()));
            living.setCustomNameVisible(MainConfig.getInstance().node("jointeam-entity-show-name").getBoolean(true));

            if (living.getEntityType().is("armor_stand")) {
                ArmorStandUtils.equip(living, value);
            }

            JoinTeamCommand.TEAMS_IN_HAND.remove(player.getUuid());
            player.sendMessage(Message.of(LangKeys.ADMIN_TEAM_JOIN_ENTITY_ENTITY_ADDED).defaultPrefix());
        }
    }

    @OnEvent(priority = EventPriority.HIGHEST)
    public void onChat(SPlayerChatEvent event) {
        if (event.cancelled() || !MainConfig.getInstance().node("chat", "override").getBoolean()) {
            return;
        }

        var player = event.sender();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            var team = game.getPlayerTeam(gPlayer);
            var message = event.message();
            var spectator = gPlayer.isSpectator();

            var playerName = player.getName();
            var displayName = player.getDisplayName();
            var playerListName = player.getPlayerListName();

            String format = MainConfig.getInstance().node("chat", "format").getString("<%teamcolor%%name%§r> ");
            if (team != null) {
                format = format.replace("%teamcolor%", MiscUtils.toLegacyColorCode(team.getColor().getTextColor()));
                format = format.replace("%team%", team.getName());
                format = format.replace("%coloredteam%", Component.text(team.getName()).withColor(team.getColor().getTextColor()).toLegacy());
            } else if (spectator) {
                format = format.replace("%teamcolor%", MiscUtils.toLegacyColorCode(Color.GRAY));
                format = format.replace("%team%", "SPECTATOR");
                format = format.replace("%coloredteam%",  MiscUtils.toLegacyColorCode(Color.GRAY) + "SPECTATOR");
            } else {
                format = format.replace("%teamcolor%", MiscUtils.toLegacyColorCode(Color.GRAY));
                format = format.replace("%team%", "");
                format = format.replace("%coloredteam%", MiscUtils.toLegacyColorCode(Color.GRAY));
            }
            format = format.replace("%name%", playerName);
            format = format.replace("%displayName%", displayName.toLegacy());
            format = format.replace("%playerListName%", playerListName == null ? "" : playerListName.toLegacy());

            var vaultPrefix = PlaceholderManager.resolveString(player, "%vault_prefix%");
            var vaultSuffix = PlaceholderManager.resolveString(player, "%vault_suffix%");
            format = format.replace("%prefix%", vaultPrefix.equals("%vault_prefix%") ? "" : vaultPrefix); // deprecated, should be migrated
            format = format.replace("%suffix%", vaultSuffix.equals("%vault_prefix%") ? "" : vaultSuffix); // deprecated, should be migrated
            format = format.replace("%vault_prefix%", vaultPrefix.equals("%vault_prefix%") ? "" : vaultPrefix);
            format = format.replace("%vault_suffix%", vaultSuffix.equals("%vault_prefix%") ? "" : vaultSuffix);

            format = MiscUtils.translateAlternateColorCodes('&', format);

            boolean teamChat = MainConfig.getInstance().node("chat", "default-team-chat-while-running").getBoolean(true)
                    && game.getStatus() == GameStatus.RUNNING && (team != null || spectator);

            String allChat = MainConfig.getInstance().node("chat", "all-chat-prefix").getString("@a");
            String tChat = MainConfig.getInstance().node("chat", "team-chat-prefix").getString("@t");

            if (message.startsWith(allChat) && (!spectator || !MainConfig.getInstance().node("chat", "disable-all-chat-for-spectators").getBoolean())) {
                teamChat = false;
                message = message.substring(allChat.length()).trim();
            } else if (message.startsWith(tChat) && (team != null || spectator)) {
                teamChat = true;
                message = message.substring(tChat.length()).trim();
            }

            if (teamChat) {
                if (spectator) {
                    format = MainConfig.getInstance().node("chat", "death-chat").getString("[DEATH] ") + format;
                } else {
                    format = MainConfig.getInstance().node("chat", "team-chat").getString("[TEAM] ") + format;
                }
            } else {
                format = MainConfig.getInstance().node("chat", "all-chat").getString("[ALL] ") + format;
            }

            event.format(format + message.replaceAll("%", "%%")); // Fix using % in chat
            var recipients = event.recipients().iterator();
            while (recipients.hasNext()) {
                var recipient = recipients.next();
                var recipientGame = PlayerManagerImpl.getInstance().getGameOfPlayer(recipient);
                if (recipientGame.isEmpty() || recipientGame.get() != game) {
                    if ((game.getStatus() == GameStatus.WAITING && MainConfig.getInstance().node("chat", "separate-chat", "lobby").getBoolean())
                            || (game.getStatus() != GameStatus.WAITING && MainConfig.getInstance().node("chat", "separate-chat", "game").getBoolean())) {
                        recipients.remove();
                    }
                } else if (game.getPlayerTeam(recipient.as(BedWarsPlayer.class)) != team && teamChat) {
                    recipients.remove();
                }
            }

            for (var p : event.recipients()) {
                p.sendMessage(event.format());
            }
            event.cancelled(true);
        } else {
            if (MainConfig.getInstance().node("chat", "separate-chat", "lobby").getBoolean() || MainConfig.getInstance().node("chat", "separate-chat", "game").getBoolean()) {
                var recipients = event.recipients().iterator();
                while (recipients.hasNext()) {
                    var recipient = recipients.next();
                    var recipientGame = PlayerManagerImpl.getInstance().getGameOfPlayer(recipient);
                    if (recipientGame.isPresent()) {
                        if ((recipientGame.get().getStatus() == GameStatus.WAITING && MainConfig.getInstance().node("chat", "separate-chat", "lobby").getBoolean())
                                || (recipientGame.get().getStatus() != GameStatus.WAITING && MainConfig.getInstance().node("chat", "separate-chat", "game").getBoolean())) {
                            recipients.remove();
                        }
                    }
                }
            }
        }
    }

    @OnEvent
    public void onMove(SPlayerMoveEvent event) {
        if (event.cancelled()) {
            return;
        }

        var player = event.player();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA, false) && game.getStatus() == GameStatus.RUNNING
                    && !gPlayer.isSpectator()) {
                if (!ArenaUtils.isInArea(event.newLocation(), game.getPos1(), game.getPos2())) {
                    var armor = player.getAttribute(AttributeTypeHolder.of("minecraft:generic.armor"));
                    var armorToughness = AttributeTypeHolder.ofOptional("minecraft:generic.armor_toughness").flatMap(player::getAttribute);
                    if (armor.isEmpty()) {
                        player.damage(5);
                    } else {
                        // this is not 100% accurate formula - armorToughness check contains weaponDamage which is hardcoded to 5 (4*5=20) but we don't know the weapon damage yet
                        var multiplier = (1.0 - Math.min(20.0, Math.max(armor.get().getValue() / 5.0, armor.get().getValue() - 20.0 / (armorToughness.map(AttributeHolder::getValue).orElse(0.0) + 8))) / 25.0);
                        if (multiplier < 1) {
                            multiplier = 2 - multiplier;
                        }
                        double weaponDamage = multiplier * 5.0;
                        player.damage(weaponDamage);
                    }
                    Debug.info(player.getName() + " is doing prohibited move, damaging");
                }
            } else if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.PREVENT_SPECTATOR_FROM_FLYING_AWAY, false) && gPlayer.isSpectator() && (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING)) {
                if (!ArenaUtils.isInArea(event.newLocation(), game.getPos1(), game.getPos2())) {
                    event.cancelled(true);
                    Debug.info(player.getName() + " is doing prohibited move, cancelling");
                }
            }
        }
    }

    @OnEvent
    public void onPlaceLiquid(SPlayerBucketEvent event) {
        if (event.cancelled() || event.action() != SPlayerBucketEvent.Action.EMPTY) {
            return;
        }

        var player = event.player();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            var loc = event.blockClicked().getLocation();

            loc.add(event.blockFace().getDirection().normalize());

            var block = loc.getBlock();
            if (game.getStatus() == GameStatus.RUNNING) {
                if (block.getType().isAir() || game.getRegion().isLocationModifiedDuringGame(block.getLocation())) {
                    game.getRegion().addBuiltDuringGame(block.getLocation());
                    Debug.info(player.getName() + " placed liquid");
                } else {
                    event.cancelled(true);
                    Debug.info(player.getName() + " placed liquid, cancelling");
                }
            } else if (game.getStatus() != GameStatus.DISABLED) {
                event.cancelled(true);
                Debug.info(player.getName() + " placed liquid, cancelling");
            }
        } else if (MainConfig.getInstance().node("preventArenaFromGriefing").getBoolean()) {
            for (var game : GameManagerImpl.getInstance().getGames()) {
                if (game.getStatus() != GameStatus.DISABLED && ArenaUtils.isInArea(event.blockClicked().getLocation(), game.getPos1(), game.getPos2())) {
                    event.cancelled(true);
                    Debug.info(player.getName() + " is doing prohibited actions in protected area while not playing BedWars");
                    return;
                }
            }
        }
    }

    @OnEvent
    public void onVehicleCreated(SVehicleCreateEvent event) {
        if (event.cancelled()) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (ArenaUtils.isInArea(event.entity().getLocation(), game.getPos1(), game.getPos2())) {
                    EntitiesManagerImpl.getInstance().addEntityToGame(event.entity(), game);
                    break;
                }
            }
        }
    }

    @OnEvent
    public void onItemPickup(SPlayerPickupItemEvent event) {
        if (event.cancelled()) {
            return;
        }

        var player = event.player();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator()) {
                event.cancelled(true);
                Debug.info(player.getName() + " tried to pick up the item in lobby or as spectator");
            } else {
                for (var spawner : game.getSpawners()) {
                    spawner.remove(event.item());
                }
            }
        }
    }

    @OnEvent
    public void onPlayerSwapHandItems(SPlayerSwapHandItemsEvent event) {
        var player = event.player();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            if (gPlayer.getGame().getStatus() == GameStatus.WAITING) {
                event.cancelled(true);
                Debug.info(event.player().getName() + " tried to swap his hands in lobby, cancelling");
            }
        }
    }

    @OnEvent
    public void onItemMerge(SItemMergeEvent event) {
        if (event.cancelled()) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING
                    && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SPAWNER_DISABLE_MERGE, false)) {

                if (ArenaUtils.isInArea(event.entity().getLocation(), game.getPos1(), game.getPos2())
                        || ArenaUtils.isInArea(event.target().getLocation(), game.getPos1(), game.getPos2())) {
                    event.cancelled(true);
                    return;
                }
            }
        }
    }
}
