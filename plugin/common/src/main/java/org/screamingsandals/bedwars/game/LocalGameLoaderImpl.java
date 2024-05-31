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

import com.onarandombox.MultiverseCore.api.Core;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.game.LocalGame;
import org.screamingsandals.bedwars.api.game.LocalGameLoader;
import org.screamingsandals.bedwars.api.game.target.Target;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.target.NoTargetImpl;
import org.screamingsandals.bedwars.game.target.ExpirableTargetBlockImpl;
import org.screamingsandals.bedwars.game.target.TargetBlockImpl;
import org.screamingsandals.bedwars.game.target.ExpirableTargetImpl;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.bedwars.utils.ConfigurateUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.variants.VariantManagerImpl;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.entity.LivingEntity;
import org.screamingsandals.lib.plugin.Plugins;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.world.Worlds;
import org.screamingsandals.lib.world.chunk.Chunk;
import org.screamingsandals.lib.world.gamerule.GameRuleType;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class LocalGameLoaderImpl implements LocalGameLoader {

    public static LocalGameLoaderImpl getInstance() {
        return ServiceManager.get(LocalGameLoaderImpl.class);
    }

    @Override
    public GameImpl loadGame(File file, boolean firstAttempt) {
        try {
            final ConfigurationNode configMap = ConfigurateUtils.loadFileAsNode(file);
            if (configMap == null) {
                return null;
            }

            var uid = configMap.node("uuid");
            UUID uuid;
            if (uid.empty()) {
                var indexOf = file.getName().indexOf(".");
                var uuidStr = indexOf == -1 ? file.getName() : file.getName().substring(0, indexOf);
                try {
                    uuid = UUID.fromString(uuidStr);
                } catch (Throwable t) {
                    do {
                        uuid = UUID.randomUUID();
                    } while (GameManagerImpl.getInstance().getGame(uuid).isPresent());
                }
            } else {
                uuid = uid.get(UUID.class);
            }

            if (GameManagerImpl.getInstance().getGame(uuid).isPresent()) {
                Server.getConsoleSender().sendMessage(
                        MiscUtils.BW_PREFIX.withAppendix(
                                Component.text("Arena " + uuid + " has the same unique id as another arena that's already loaded. Skipping!", Color.RED)
                        )
                );
                return null;
            }

            final var game = new GameImpl(uuid);
            game.setFile(file);
            game.setName(configMap.node("name").getString());
            game.setFee(configMap.node("fee").getDouble(0D));
            game.setPauseCountdown(configMap.node("pauseCountdown").getInt());
            game.setGameTime(configMap.node("gameTime").getInt());

            var worldName = Objects.requireNonNull(configMap.node("world").getString());
            game.setWorld(Worlds.getWorld(worldName));

            var multiverse = Plugins.getPlugin("Multiverse-Core");
            if (game.getWorld() == null) {
                if (multiverse != null) {
                    Server.getConsoleSender().sendMessage(
                            MiscUtils.BW_PREFIX.withAppendix(
                                    Component.text("World " + worldName + " was not found, but we found Multiverse-Core, so we will try to load this world.", Color.RED)
                            )
                    );

                    if (((Core) multiverse).getMVWorldManager().loadWorld(worldName)) {
                        Server.getConsoleSender().sendMessage(
                                MiscUtils.BW_PREFIX.withAppendix(
                                        Component.text("World " + worldName + " was successfully loaded with Multiverse-Core, continue in arena loading.", Color.GREEN)
                                )
                        );

                        game.setWorld(Objects.requireNonNull(Worlds.getWorld(worldName)));
                    } else {
                        Server.getConsoleSender().sendMessage(
                                MiscUtils.BW_PREFIX.withAppendix(
                                        Component.text("Arena " + game.getName() + " can't be loaded, because world " + worldName + " is missing!", Color.RED)
                                )
                        );
                        return null;
                    }
                } else if (firstAttempt) {
                    Server.getConsoleSender().sendMessage(
                            MiscUtils.BW_PREFIX.withAppendix(
                                    Component.text("Arena " + game.getName() + " can't be loaded, because world " + worldName + " is missing! We will try it again after all plugins have loaded!", Color.YELLOW)
                            )
                    );
                    Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> loadGame(file, false), 10L, TaskerTime.TICKS);
                    return null;
                } else {
                    Server.getConsoleSender().sendMessage(
                            MiscUtils.BW_PREFIX.withAppendix(
                                    Component.text("Arena " + game.getName() + " can't be loaded, because world " + worldName + " is missing!", Color.RED)
                            )
                    );
                    return null;
                }
            }

            if (Server.isVersion(1, 15)) {
                game.getWorld().setGameRuleValue(GameRuleType.of("doImmediateRespawn"), true);
            }

            game.setPos1(MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(configMap.node("pos1").getString())));
            game.setPos2(MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(configMap.node("pos2").getString())));


            if (MainConfig.getInstance().node("prevent-spawning-mobs").getBoolean(true)) {
                for (LivingEntity e : game.getWorld().getEntitiesByClass(LivingEntity.class)) {
                    if (!e.getEntityType().is("minecraft:player") && !e.getEntityType().is("minecraft:armor_stand")) {
                        if (ArenaUtils.isInArea(e.getLocation(), game.getPos1(), game.getPos2())) {
                            final Chunk chunk = e.getLocation().getWorld().getChunkAt(e.getLocation());
                            if (chunk != null && !chunk.isLoaded()) {
                                chunk.load();
                            }
                            e.remove();
                        }
                    }
                }
            }

            game.setSpecSpawn(MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(configMap.node("specSpawn").getString())));
            var spawnWorld = configMap.node("lobbySpawnWorld").getString();
            var lobbySpawnWorld = Worlds.getWorld(Objects.requireNonNull(spawnWorld));
            if (lobbySpawnWorld == null) {
                if (multiverse != null) {
                    Server.getConsoleSender().sendMessage(
                            MiscUtils.BW_PREFIX.withAppendix(
                                    Component.text("World " + spawnWorld + " was not found, but we found Multiverse-Core, so we will try to load this world.", Color.RED)
                            )
                    );

                    if (((Core) multiverse).getMVWorldManager().loadWorld(spawnWorld)) {
                        Server.getConsoleSender().sendMessage(
                                MiscUtils.BW_PREFIX.withAppendix(
                                        Component.text("World " + spawnWorld + " was successfully loaded with Multiverse-Core, continue in arena loading.", Color.GREEN)
                                )
                        );

                        lobbySpawnWorld = Objects.requireNonNull(Worlds.getWorld(Objects.requireNonNull(spawnWorld)));
                    } else {
                        Server.getConsoleSender().sendMessage(
                                MiscUtils.BW_PREFIX.withAppendix(
                                        Component.text("Arena " + game.getName() + " can't be loaded, because world " + spawnWorld + " is missing!", Color.RED)
                                )
                        );
                        return null;
                    }
                } else if (firstAttempt) {
                    Server.getConsoleSender().sendMessage(
                            MiscUtils.BW_PREFIX.withAppendix(
                                    Component.text("Arena " + game.getName() + " can't be loaded, because world " + spawnWorld + " is missing! We will try it again after all plugins have loaded!", Color.YELLOW)
                            )
                    );
                    Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> loadGame(file, false), 10L, TaskerTime.TICKS);
                    return null;
                } else {
                    Server.getConsoleSender().sendMessage(
                            MiscUtils.BW_PREFIX.withAppendix(
                                    Component.text("Arena " + game.getName() + " can't be loaded, because world " + spawnWorld + " is missing!", Color.RED)
                            )
                    );
                    return null;
                }
            }

            var lobbyPos1 = configMap.node("lobbyPos1").getString();
            var lobbyPos2 = configMap.node("lobbyPos2").getString();
            if (lobbyPos1 != null && lobbyPos2 != null) {
                game.setLobbyPos1(MiscUtils.readLocationFromString(lobbySpawnWorld, lobbyPos1));
                game.setLobbyPos2(MiscUtils.readLocationFromString(lobbySpawnWorld, lobbyPos2));
            }

            var variant = configMap.node("variant");
            if (!variant.empty()) {
                var gameVariant = VariantManagerImpl.getInstance().getVariant(variant.getString("")).orElse(null);
                if (gameVariant != null) {
                    game.setGameVariant(gameVariant);
                }
            }
            if (game.getGameVariant() == null) {
                game.setGameVariant(VariantManagerImpl.getInstance().getDefaultVariant());
            }

            game.setLobbySpawn(MiscUtils.readLocationFromString(lobbySpawnWorld, Objects.requireNonNull(configMap.node("lobbySpawn").getString())));
            game.setMinPlayers(configMap.node("minPlayers").getInt(2));
            for (var entry : configMap.node("teams").childrenMap().entrySet()) {
                var teamN = entry.getKey();
                var team = entry.getValue();
                var t = new TeamImpl();
                t.setColor(TeamColorImpl.valueOf(MiscUtils.convertColorToNewFormat(team.node("color").getString(), team.node("isNewColor").getBoolean())));
                t.setName(teamN.toString());
                var targetNode = team.node("target");
                if (!targetNode.empty() && targetNode.isMap()) {
                    var type = targetNode.node("type").getString("");
                    Target target;
                    switch (type) {
                        case "block":
                            target = TargetBlockImpl.Loader.INSTANCE.load(game, targetNode).orElseThrow();
                            break;
                        case "countdown":
                            target = ExpirableTargetImpl.Loader.INSTANCE.load(game, targetNode).orElseThrow();
                            break;
                        case "block-countdown":
                            target = ExpirableTargetBlockImpl.Loader.INSTANCE.load(game, targetNode).orElseThrow();
                            break;
                        case "none":
                            target = NoTargetImpl.Loader.INSTANCE.load(game, targetNode).orElseThrow();
                            break;
                        default:
                            target = null;
                    }
                    t.setTarget(target);
                } else {
                    var bed = team.node("bed");
                    if (!bed.empty()) {
                        t.setTarget(new TargetBlockImpl(MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(bed.getString()))));
                    }
                }
                t.setMaxPlayers(team.node("maxPlayers").getInt());
                var spawns = team.node("spawns");
                if (!spawns.virtual() && spawns.isList()) {
                    try {
                        Objects.requireNonNull(spawns.getList(String.class)).stream()
                                .map(s -> MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(s)))
                                .forEach(t.getTeamSpawns()::add);
                    } catch (SerializationException e) {
                        e.printStackTrace();
                        // maybe we still have the old single spawn? probably not, but let's try it anyway
                        t.getTeamSpawns().add(MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(team.node("spawn").getString())));
                    }
                } else {
                    t.getTeamSpawns().add(MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(team.node("spawn").getString())));
                }
                t.setGame(game);

                game.getTeams().add(t);
            }
            for (var spawner : configMap.node("spawners").childrenList()) {
                game.getSpawners().add(ItemSpawnerImpl.Loader.INSTANCE.load(game, spawner).orElseThrow());
            }
            for (var store : configMap.node("stores").childrenList()) {
                game.getGameStoreList().add(GameStoreImpl.Loader.INSTANCE.load(game, store).orElseThrow());
            }

            var oldCustomPrefix = configMap.node("customPrefix");
            var newCustomPrefix = configMap.node("constant", "prefix");
            if (!oldCustomPrefix.empty() && newCustomPrefix.empty()) {
                var str = oldCustomPrefix.getString();
                if (str != null) {
                    newCustomPrefix.set(MiscUtils.toMiniMessage(str));
                    oldCustomPrefix.set(null);
                }
            }

            // migration of arenaTime to configuration container
            {
                var oldArenaTime = configMap.node("arenaTime");
                var newArenaTime = configMap.node("constant", "arena-time");
                if (!oldArenaTime.empty() && newArenaTime.empty()) {
                    newArenaTime.from(oldArenaTime);
                    oldArenaTime.set(null);
                }
            }

            // migration of lobbyBossBarColor to configuration container
            {
                var oldLobbyBossBarColor = configMap.node("lobbyBossBarColor");
                var newLobbyBossBarColor = configMap.node("constant", "bossbar", "lobby", "color");
                if (!oldLobbyBossBarColor.empty() && newLobbyBossBarColor.empty()) {
                    newLobbyBossBarColor.set(GameImpl.loadBossBarColor(oldLobbyBossBarColor.getString("default").toUpperCase()));
                    oldLobbyBossBarColor.set(null);
                }
            }

            // migration of gameBossBarColor to configuration container
            {
                var oldGameBossBarColor = configMap.node("gameBossBarColor");
                var newGameBossBarColor = configMap.node("constant", "bossbar", "game", "color");
                if (!newGameBossBarColor.empty() && oldGameBossBarColor.empty()) {
                    newGameBossBarColor.set(GameImpl.loadBossBarColor(oldGameBossBarColor.getString("default").toUpperCase()));
                    oldGameBossBarColor.set(null);
                }
            }

            game.getConfigurationContainer().applyNode(configMap.node("constant"));

            game.setArenaWeather(GameImpl.loadWeather(configMap.node("arenaWeather").getString("default").toUpperCase()));

            game.setPostGameWaiting(configMap.node("postGameWaiting").getInt(3));

            // migration of displayName (legacy) to game-display-name (MiniMessage)
            {
                var oldDisplayName = configMap.node("displayName");
                var newDisplayName = configMap.node("game-display-name");
                if (!oldDisplayName.empty() && newDisplayName.empty()) {
                    newDisplayName.set(MiscUtils.toMiniMessage(oldDisplayName.getString("")));
                    oldDisplayName.set(null);
                }
            }

            game.setDisplayName(configMap.node("game-display-name").getString());

            game.start();
            Server.getConsoleSender().sendMessage(
                    MiscUtils.BW_PREFIX.withAppendix(
                            Component.text("Arena ", Color.GREEN),
                            Component.text(game.getUuid() + "/" + game.getName() + " (" + file.getName() + ")", Color.WHITE),
                            Component.text(" loaded!", Color.GREEN)
                    )
            );
            if (uid.empty()) {
                try {
                    // because we didn't have uuid in the arena config file, we need to save the arena again
                    game.saveToConfig();
                } catch (Throwable ignored) {
                }
            }

            return game;
        } catch (Throwable throwable) {
            Debug.warn("Something went wrong while loading arena file " + file.getName() + ". Please report this to our Discord or GitHub!", true);
            throwable.printStackTrace();
            return null;
        }

    }

    @SneakyThrows
    @Override
     public void saveGame(@NotNull LocalGame apiGame) {
        final GameImpl game = (GameImpl) apiGame;
        var dir = BedWarsPlugin.getInstance().getPluginDescription().dataFolder().resolve("arenas").toFile();
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }

        var file = game.getFile();
        if (file == null) {
            do {
                file = new File(dir, UUID.randomUUID() + ".json");
            } while (file.exists());
        }
        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        game.setFile(file);

        final ConfigurationLoader<? extends ConfigurationNode> loader;
        if (file.getName().toLowerCase().endsWith(".yml") || file.getName().toLowerCase().endsWith(".yaml")) {
            loader = YamlConfigurationLoader.builder()
                    .file(file)
                    .build();
        } else {
            loader = GsonConfigurationLoader.builder()
                    .file(file)
                    .build();
        }

        var configMap = loader.createNode();
        configMap.node("uuid").set(game.getUuid());
        configMap.node("name").set(game.getName());
        configMap.node("pauseCountdown").set(game.getPauseCountdown());
        configMap.node("gameTime").set(game.getGameTime());
        configMap.node("world").set(game.getWorld().getName());
        configMap.node("pos1").set(MiscUtils.writeLocationToString(game.getPos1()));
        configMap.node("pos2").set(MiscUtils.writeLocationToString(game.getPos2()));
        configMap.node("specSpawn").set(MiscUtils.writeLocationToString(game.getSpecSpawn()));
        configMap.node("lobbySpawn").set(MiscUtils.writeLocationToString(game.getLobbySpawn()));
        if (game.getLobbyPos1() != null) {
            configMap.node("lobbyPos1", MiscUtils.writeLocationToString(game.getLobbyPos1()));
        }
        if (game.getLobbyPos2() != null) {
            configMap.node("lobbyPos2", MiscUtils.writeLocationToString(game.getLobbyPos2()));
        }
        configMap.node("lobbySpawnWorld").set(game.getLobbySpawn().getWorld().getName());
        configMap.node("minPlayers").set(game.getMinPlayers());
        configMap.node("postGameWaiting").set(game.getPostGameWaiting());
        configMap.node("game-display-name").set(game.getDisplayName());
        final var teams = game.getTeams();
        if (!teams.isEmpty()) {
            for (var t : teams) {
                var teamNode = configMap.node("teams", t.getName());
                teamNode.node("isNewColor").set(true);
                teamNode.node("color").set(t.getColor().name());
                teamNode.node("maxPlayers").set(t.getMaxPlayers());
                if (t.getTarget() instanceof SerializableGameComponent) {
                    ((SerializableGameComponent) t.getTarget()).saveTo(teamNode.node("target"));
                }
                var spawns = teamNode.node("spawns");
                for (var spawn : t.getTeamSpawns()) {
                    spawns.appendListNode().set(MiscUtils.writeLocationToString(spawn));
                }
            }
        }
        var spawners = game.getSpawners();
        for (var spawner : spawners) {
            spawner.saveTo(configMap.node("spawners").appendListNode());
        }
        var gameStore = game.getGameStoreList();
        for (var store : gameStore) {
            store.saveTo(configMap.node("stores").appendListNode());
        }

        configMap.node("constant").from(game.getConfigurationContainer().getSaved());

        var arenaWeather = game.getArenaWeather();
        configMap.node("arenaWeather").set(arenaWeather == null ? "default" : arenaWeather.location().asString());

        var gameVariant = game.getGameVariant();
        if (gameVariant != null) {
            configMap.node("variant").set(gameVariant.getName());
        }

        configMap.node("fee").set(game.getFee());

        try {
            loader.save(configMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
