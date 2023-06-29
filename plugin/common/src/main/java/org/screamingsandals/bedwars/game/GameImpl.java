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

package org.screamingsandals.bedwars.game;

import com.onarandombox.MultiverseCore.api.Core;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.PlatformService;
import org.screamingsandals.bedwars.api.ArenaTime;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.boss.StatusBar;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.events.TargetInvalidationReason;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.game.target.Target;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.api.upgrades.UpgradeRegistry;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.api.utils.DelayFactory;
import org.screamingsandals.bedwars.boss.BossBarImpl;
import org.screamingsandals.bedwars.boss.XPBarImpl;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.commands.StatsCommand;
import org.screamingsandals.bedwars.config.GameConfigurationContainerImpl;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.RecordSave;
import org.screamingsandals.bedwars.utils.EconomyUtils;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.events.*;
import org.screamingsandals.bedwars.game.target.*;
import org.screamingsandals.bedwars.holograms.StatisticsHolograms;
import org.screamingsandals.bedwars.inventories.TeamSelectorInventory;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.region.BWRegion;
import org.screamingsandals.bedwars.region.FlatteningRegion;
import org.screamingsandals.bedwars.sidebar.GameSidebar;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.tab.TabManager;
import org.screamingsandals.bedwars.utils.*;
import org.screamingsandals.bedwars.variants.VariantImpl;
import org.screamingsandals.bedwars.variants.VariantManagerImpl;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.SpecialSoundKey;
import org.screamingsandals.lib.block.BlockHolder;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.block.state.BlockStateHolder;
import org.screamingsandals.lib.block.state.SignHolder;
import org.screamingsandals.lib.container.Container;
import org.screamingsandals.lib.container.type.InventoryTypeHolder;
import org.screamingsandals.lib.economy.EconomyManager;
import org.screamingsandals.lib.entity.EntityBasic;
import org.screamingsandals.lib.entity.EntityItem;
import org.screamingsandals.lib.entity.EntityLiving;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.event.player.SPlayerBlockBreakEvent;
import org.screamingsandals.lib.healthindicator.HealthIndicator;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.npc.NPC;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.player.SenderWrapper;
import org.screamingsandals.lib.player.gamemode.GameModeHolder;
import org.screamingsandals.lib.plugin.PluginManager;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.bossbar.BossBarColor;
import org.screamingsandals.lib.spectator.bossbar.BossBarDivision;
import org.screamingsandals.lib.spectator.sound.SoundSource;
import org.screamingsandals.lib.spectator.sound.SoundStart;
import org.screamingsandals.lib.spectator.title.Title;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;
import org.screamingsandals.lib.visuals.Visual;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.LocationMapper;
import org.screamingsandals.lib.world.WorldHolder;
import org.screamingsandals.lib.world.WorldMapper;
import org.screamingsandals.lib.world.chunk.ChunkHolder;
import org.screamingsandals.lib.world.gamerule.GameRuleHolder;
import org.screamingsandals.lib.world.weather.WeatherHolder;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameImpl implements Game {
    public boolean gameStartItem;
    public boolean forceGameToStart;
    @Getter
    private final UUID uuid;
    private String name;
    private LocationHolder pos1;
    private LocationHolder pos2;
    private LocationHolder lobbySpawn;
    private LocationHolder lobbyPos1;
    private LocationHolder lobbyPos2;
    private LocationHolder specSpawn;
    private final List<TeamImpl> teams = new ArrayList<>();
    private final List<ItemSpawnerImpl> spawners = new ArrayList<>();
    private final Map<BedWarsPlayer, RespawnProtection> respawnProtectionMap = new HashMap<>();
    @Getter
    @Setter
    private double fee;
    private int pauseCountdown;
    private int gameTime;
    private int minPlayers;
    private final List<BedWarsPlayer> players = new ArrayList<>();
    private WorldHolder world;
    private final List<GameStoreImpl> gameStore = new ArrayList<>();
    private WeatherHolder arenaWeather = null;
    private boolean preServerRestart = false;
    @Getter
    private File file;
    @Getter
    @Setter
    @Nullable
    private String displayName;
    @Getter
    @UnknownNullability("Shouldn't be null unless the GameImpl object has been constructed using other methods than loadGame or createGame")
    private VariantImpl gameVariant;

    // STATUS
    private GameStatus previousStatus = GameStatus.DISABLED;
    private GameStatus status = GameStatus.DISABLED;
    private GameStatus afterRebuild = GameStatus.WAITING;
    private int countdown = -1, previousCountdown = -1;
    private int calculatedMaxPlayers;
    private TaskerTask task;
    private final List<TeamImpl> teamsInGame = new ArrayList<>();
    private final BWRegion region = Server.isVersion(1, 13) ? new FlatteningRegion() : PlatformService.getInstance().getLegacyRegion();
    private TeamSelectorInventory teamSelectorInventory;
    private StatusBar statusbar;
    private final Map<LocationHolder, Item[]> usedChests = new HashMap<>();
    private final List<SpecialItem> activeSpecialItems = new ArrayList<>();
    private final List<DelayFactory> activeDelays = new ArrayList<>();
    private final Map<BedWarsPlayer, Container> fakeEnderChests = new HashMap<>();
    private int postGameWaiting = 3;
    private GameSidebar experimentalBoard = null;
    private HealthIndicator healthIndicator = null;
    @Getter
    private final List<Visual<?>> otherVisuals = new ArrayList<>();

    @Getter
    private final GameConfigurationContainerImpl configurationContainer = new GameConfigurationContainerImpl();

    private boolean preparing = false;

    public static GameImpl loadGame(File file) {
        return loadGame(file, true);
    }

    public static GameImpl loadGame(File file, boolean firstAttempt) {
        try {
            if (!file.exists()) {
                return null;
            }

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

            final ConfigurationNode configMap;
            try {
                configMap = loader.load();
            } catch (ConfigurateException e) {
                e.printStackTrace();
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
                PlayerMapper.getConsoleSender().sendMessage(
                        MiscUtils.BW_PREFIX.withAppendix(
                                Component.text("Arena " + uuid + " has the same unique id as another arena that's already loaded. Skipping!", Color.RED)
                        )
                );
                return null;
            }

            final var game = new GameImpl(uuid);
            game.file = file;
            game.name = configMap.node("name").getString();
            game.fee = configMap.node("fee").getDouble(0D);
            game.pauseCountdown = configMap.node("pauseCountdown").getInt();
            game.gameTime = configMap.node("gameTime").getInt();

            var worldName = Objects.requireNonNull(configMap.node("world").getString());
            game.world = WorldMapper.getWorld(worldName).orElse(null);

            var multiverseKey = PluginManager.createKey("Multiverse-Core").orElseThrow();
            var multiverse = PluginManager.getPlatformClass(multiverseKey);
            if (game.world == null) {
                if (PluginManager.isEnabled(multiverseKey)) {
                    PlayerMapper.getConsoleSender().sendMessage(
                            MiscUtils.BW_PREFIX.withAppendix(
                                    Component.text("World " + worldName + " was not found, but we found Multiverse-Core, so we will try to load this world.", Color.RED)
                            )
                    );

                    if (multiverse.isPresent() && ((Core) multiverse.orElseThrow()).getMVWorldManager().loadWorld(worldName)) {
                        PlayerMapper.getConsoleSender().sendMessage(
                                MiscUtils.BW_PREFIX.withAppendix(
                                        Component.text("World " + worldName + " was successfully loaded with Multiverse-Core, continue in arena loading.", Color.GREEN)
                                )
                        );

                        game.world = WorldMapper.getWorld(worldName).orElseThrow();
                    } else {
                        PlayerMapper.getConsoleSender().sendMessage(
                                MiscUtils.BW_PREFIX.withAppendix(
                                        Component.text("Arena " + game.name + " can't be loaded, because world " + worldName + " is missing!", Color.RED)
                                )
                        );
                        return null;
                    }
                } else if (firstAttempt) {
                    PlayerMapper.getConsoleSender().sendMessage(
                            MiscUtils.BW_PREFIX.withAppendix(
                                    Component.text("Arena " + game.name + " can't be loaded, because world " + worldName + " is missing! We will try it again after all plugins have loaded!", Color.YELLOW)
                            )
                    );
                    Tasker.build(() -> loadGame(file, false)).delay(10L, TaskerTime.TICKS).start();
                    return null;
                } else {
                    PlayerMapper.getConsoleSender().sendMessage(
                            MiscUtils.BW_PREFIX.withAppendix(
                                    Component.text("Arena " + game.name + " can't be loaded, because world " + worldName + " is missing!", Color.RED)
                            )
                    );
                    return null;
                }
            }

            if (Server.isVersion(1, 15)) {
                game.world.setGameRuleValue(GameRuleHolder.of("doImmediateRespawn"), true);
            }

            game.pos1 = MiscUtils.readLocationFromString(game.world, Objects.requireNonNull(configMap.node("pos1").getString()));
            game.pos2 = MiscUtils.readLocationFromString(game.world, Objects.requireNonNull(configMap.node("pos2").getString()));


            if (MainConfig.getInstance().node("prevent-spawning-mobs").getBoolean(true)) {
                for (EntityLiving e : game.world.getEntitiesByClass(EntityLiving.class)) {
                    if (!e.getEntityType().is("minecraft:player") && !e.getEntityType().is("minecraft:armor_stand")) {
                        if (ArenaUtils.isInArea(e.getLocation(), game.pos1, game.pos2)) {
                            final Optional<ChunkHolder> chunk = e.getLocation().getWorld().getChunkAt(e.getLocation());
                            if (chunk.isPresent() && !chunk.get().isLoaded()) {
                                chunk.get().load();
                            }
                            e.remove();
                        }
                    }
                }
            }

            game.specSpawn = MiscUtils.readLocationFromString(game.world, Objects.requireNonNull(configMap.node("specSpawn").getString()));
            var spawnWorld = configMap.node("lobbySpawnWorld").getString();
            var lobbySpawnWorld = WorldMapper.getWorld(Objects.requireNonNull(spawnWorld)).orElse(null);
            if (lobbySpawnWorld == null) {
                if (PluginManager.isEnabled(multiverseKey)) {
                    PlayerMapper.getConsoleSender().sendMessage(
                            MiscUtils.BW_PREFIX.withAppendix(
                                    Component.text("World " + spawnWorld + " was not found, but we found Multiverse-Core, so we will try to load this world.", Color.RED)
                            )
                    );

                    if (multiverse.isPresent() && ((Core) multiverse.orElseThrow()).getMVWorldManager().loadWorld(spawnWorld)) {
                        PlayerMapper.getConsoleSender().sendMessage(
                                MiscUtils.BW_PREFIX.withAppendix(
                                        Component.text("World " + spawnWorld + " was successfully loaded with Multiverse-Core, continue in arena loading.", Color.GREEN)
                                )
                        );

                        lobbySpawnWorld = WorldMapper.getWorld(Objects.requireNonNull(spawnWorld)).orElseThrow();
                    } else {
                        PlayerMapper.getConsoleSender().sendMessage(
                                MiscUtils.BW_PREFIX.withAppendix(
                                        Component.text("Arena " + game.name + " can't be loaded, because world " + spawnWorld + " is missing!", Color.RED)
                                )
                        );
                        return null;
                    }
                } else if (firstAttempt) {
                    PlayerMapper.getConsoleSender().sendMessage(
                            MiscUtils.BW_PREFIX.withAppendix(
                                    Component.text("Arena " + game.name + " can't be loaded, because world " + spawnWorld + " is missing! We will try it again after all plugins have loaded!", Color.YELLOW)
                            )
                    );
                    Tasker.build(() -> loadGame(file, false)).delay(10L, TaskerTime.TICKS).start();
                    return null;
                } else {
                    PlayerMapper.getConsoleSender().sendMessage(
                            MiscUtils.BW_PREFIX.withAppendix(
                                    Component.text("Arena " + game.name + " can't be loaded, because world " + spawnWorld + " is missing!", Color.RED)
                            )
                    );
                    return null;
                }
            }

            var lobbyPos1 = configMap.node("lobbyPos1").getString();
            var lobbyPos2 = configMap.node("lobbyPos2").getString();
            if (lobbyPos1 != null && lobbyPos2 != null) {
                game.lobbyPos1 = MiscUtils.readLocationFromString(lobbySpawnWorld, lobbyPos1);
                game.lobbyPos2 = MiscUtils.readLocationFromString(lobbySpawnWorld, lobbyPos2);
            }

            var variant = configMap.node("variant");
            if (!variant.empty()) {
                var gameVariant = VariantManagerImpl.getInstance().getVariant(variant.getString("")).orElse(null);
                if (gameVariant != null) {
                    game.setGameVariant(gameVariant);
                }
            }
            if (game.gameVariant == null) {
                game.setGameVariant(VariantManagerImpl.getInstance().getDefaultVariant());
            }

            game.lobbySpawn = MiscUtils.readLocationFromString(lobbySpawnWorld, Objects.requireNonNull(configMap.node("lobbySpawn").getString()));
            game.minPlayers = configMap.node("minPlayers").getInt(2);
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
                            target = TargetCountdownImpl.Loader.INSTANCE.load(game, targetNode).orElseThrow();
                            break;
                        case "block-countdown":
                            target = TargetBlockCountdownImpl.Loader.INSTANCE.load(game, targetNode).orElseThrow();
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
                        t.setTarget(new TargetBlockImpl(MiscUtils.readLocationFromString(game.world, Objects.requireNonNull(bed.getString()))));
                    }
                }
                t.setMaxPlayers(team.node("maxPlayers").getInt());
                var spawns = team.node("spawns");
                if (!spawns.virtual() && spawns.isList()) {
                    try {
                        Objects.requireNonNull(spawns.getList(String.class)).stream()
                                .map(s -> MiscUtils.readLocationFromString(game.world, Objects.requireNonNull(s)))
                                .forEach(t.getTeamSpawns()::add);
                    } catch (SerializationException e) {
                        e.printStackTrace();
                        // maybe we still have the old single spawn? probably not, but let's try it anyway
                        t.getTeamSpawns().add(MiscUtils.readLocationFromString(game.world, Objects.requireNonNull(team.node("spawn").getString())));
                    }
                } else {
                    t.getTeamSpawns().add(MiscUtils.readLocationFromString(game.world, Objects.requireNonNull(team.node("spawn").getString())));
                }
                t.setGame(game);

                game.teams.add(t);
            }
            for (var spawner : configMap.node("spawners").childrenList()) {
                game.spawners.add(ItemSpawnerImpl.Loader.INSTANCE.load(game, spawner).orElseThrow());
            }
            for (var store : configMap.node("stores").childrenList()) {
                game.gameStore.add(GameStoreImpl.Loader.INSTANCE.load(game, store).orElseThrow());
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
                    newLobbyBossBarColor.set(loadBossBarColor(oldLobbyBossBarColor.getString("default").toUpperCase()));
                    oldLobbyBossBarColor.set(null);
                }
            }

            // migration of gameBossBarColor to configuration container
            {
                var oldGameBossBarColor = configMap.node("gameBossBarColor");
                var newGameBossBarColor = configMap.node("constant", "bossbar", "game", "color");
                if (!newGameBossBarColor.empty() && oldGameBossBarColor.empty()) {
                    newGameBossBarColor.set(loadBossBarColor(oldGameBossBarColor.getString("default").toUpperCase()));
                    oldGameBossBarColor.set(null);
                }
            }

            game.configurationContainer.applyNode(configMap.node("constant"));

            game.arenaWeather = loadWeather(configMap.node("arenaWeather").getString("default").toUpperCase());

            game.postGameWaiting = configMap.node("postGameWaiting").getInt(3);

            // migration of displayName (legacy) to game-display-name (MiniMessage)
            {
                var oldDisplayName = configMap.node("displayName");
                var newDisplayName = configMap.node("game-display-name");
                if (!oldDisplayName.empty() && newDisplayName.empty()) {
                    newDisplayName.set(MiscUtils.toMiniMessage(oldDisplayName.getString("")));
                    oldDisplayName.set(null);
                }
            }

            game.displayName = configMap.node("game-display-name").getString();

            game.start();
            PlayerMapper.getConsoleSender().sendMessage(
                    MiscUtils.BW_PREFIX.withAppendix(
                            Component.text("Arena ", Color.GREEN),
                            Component.text(game.uuid + "/" + game.name + " (" + file.getName() + ")", Color.WHITE),
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

    public void removeEntity(EntityBasic e) {
        if (ArenaUtils.isInArea(e.getLocation(), pos1, pos2)) {
            final var chunk = e.getLocation().getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            e.remove();
        }
    }


    public static WeatherHolder loadWeather(String weather) {
        try {
            if ("default".equalsIgnoreCase(weather)) {
                return null;
            }
            return WeatherHolder.ofOptional(weather).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static BossBarColor loadBossBarColor(String color) {
        try {
            return BossBarColor.valueOf(color);
        } catch (Exception e) {
            return null;
        }
    }

    public static GameImpl createGame(String name) {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (GameManagerImpl.getInstance().getGame(uuid).isPresent());
        var game = new GameImpl(uuid);
        game.name = name;
        game.pauseCountdown = 60;
        game.gameTime = 3600;
        game.minPlayers = 2;
        game.setGameVariant(VariantManagerImpl.getInstance().getDefaultVariant());

        return game;
    }
    public static boolean isBungeeEnabled() {
        return MainConfig.getInstance().node("bungee", "enabled").getBoolean();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WorldHolder getWorld() {
        return world;
    }

    public void setWorld(WorldHolder world) {
        if (this.world == null) {
            this.world = world;
        }
    }

    public LocationHolder getPos1() {
        return pos1;
    }

    public void setPos1(LocationHolder pos1) {
        this.pos1 = pos1;
    }

    public LocationHolder getPos2() {
        return pos2;
    }

    public void setPos2(LocationHolder pos2) {
        this.pos2 = pos2;
    }

    public LocationHolder getLobbySpawn() {
        return lobbySpawn;
    }

    public void setLobbySpawn(LocationHolder lobbySpawn) {
        this.lobbySpawn = lobbySpawn;
    }

    public int getPauseCountdown() {
        return pauseCountdown;
    }

    public void setPauseCountdown(int pauseCountdown) {
        this.pauseCountdown = pauseCountdown;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public boolean checkMinPlayers() {
        return players.size() >= getMinPlayers();
    }

    public int countPlayers() {
        return this.players.size();
    }

    public int countSpectators() {
        return (int) this.players.stream().filter(t -> t.isSpectator() && getPlayerTeam(t) == null).count();
    }

    public int countSpectating() {
        return (int) this.players.stream().filter(BedWarsPlayer::isSpectator).count();
    }

    public int countRespawnable() {
        return (int) this.players.stream().filter(t -> getPlayerTeam(t) != null).count();
    }

    public int countAlive() {
        return (int) this.players.stream().filter(t -> !t.isSpectator()).count();
    }

    @Override
    public List<GameStoreImpl> getGameStores() {
        return List.copyOf(gameStore);
    }

    public List<GameStoreImpl> getGameStoreList() {
        return gameStore;
    }

    public LocationHolder getSpecSpawn() {
        return specSpawn;
    }

    public void setSpecSpawn(LocationHolder specSpawn) {
        this.specSpawn = specSpawn;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    @Override
    public TeamImpl getTeamFromName(String name) {
        return teams.stream().filter(team1 -> team1.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<TeamImpl> getTeams() {
        return teams;
    }

    public List<ItemSpawnerImpl> getSpawners() {
        return spawners;
    }

    public TeamSelectorInventory getTeamSelectorInventory() {
        return teamSelectorInventory;
    }

    public boolean isBlockAddedDuringGame(LocationHolder loc) {
        return status == GameStatus.RUNNING && region.isLocationModifiedDuringGame(loc);
    }

    @Deprecated
    public boolean isBlockAddedDuringGame(Object loc) {
        return status == GameStatus.RUNNING && region.isLocationModifiedDuringGame(loc);
    }

    public boolean blockPlace(BedWarsPlayer player, BlockHolder block, BlockStateHolder replaced, Item itemInHand) {
        if (status != GameStatus.RUNNING) {
            return false; // ?
        }
        if (player.isSpectator()) {
            return false;
        }
        if (BedWarsPlugin.isFarmBlock(block.getType())) {
            return true;
        }
        if (!ArenaUtils.isInArea(block.getLocation(), pos1, pos2)) {
            return false;
        }

        var event = new PlayerBuildBlockEventImpl(this, player, getPlayerTeam(player), block, replaced, itemInHand);
        EventManager.fire(event);

        if (event.isCancelled()) {
            return false;
        }

        if (!replaced.getType().isAir()) {
            if (region.isLocationModifiedDuringGame(replaced.getLocation())) {
                return true;
            } else if (BedWarsPlugin.isBreakableBlock(replaced.getType()) || region.isLiquid(replaced.getType())) {
                region.putOriginalBlock(block.getLocation(), replaced);
            } else {
                return false;
            }
        }
        region.addBuiltDuringGame(block.getLocation());

        return true;
    }

    public boolean blockBreak(BedWarsPlayer player, BlockHolder block, SPlayerBlockBreakEvent event) {
        if (status != GameStatus.RUNNING) {
            return false; // ?
        }
        if (player.isSpectator()) {
            return false;
        }
        if (BedWarsPlugin.isFarmBlock(block.getType())) {
            return true;
        }
        if (!ArenaUtils.isInArea(block.getLocation(), pos1, pos2)) {
            return false;
        }

        var breakEvent = new PlayerBreakBlockEventImpl(this, player, getPlayerTeam(player), block, true);
        EventManager.fire(breakEvent);

        if (breakEvent.isCancelled()) {
            return false;
        }

        if (region.isLocationModifiedDuringGame(block.getLocation())) {
            region.removeBlockBuiltDuringGame(block.getLocation());

            if (block.getType().isSameType("ender_chest")) {
                var team = getTeamOfChest(block.getLocation());
                if (team != null) {
                    team.removeTeamChest(block.getLocation());
                    var message = Message.of(LangKeys.SPECIALS_TEAM_CHEST_BROKEN).prefixOrDefault(getCustomPrefixComponent());
                    for (BedWarsPlayer gp : team.getPlayers()) {
                        gp.sendMessage(message);
                    }

                    if (breakEvent.isDrops()) {
                        event.dropItems(false);
                        player.getPlayerInventory().addItem(ItemFactory.build("ENDER_CHEST").orElse(ItemFactory.getAir()));
                    }
                }
            }

            if (!breakEvent.isDrops()) {
                try {
                    event.dropItems(false);
                } catch (Throwable tr) {
                    block.setType(BlockTypeHolder.air());
                }
            }
            return true;
        }

        var loc = block.getLocation();
        if (region.isBedBlock(block.getBlockState().orElseThrow())) {
            if (!region.isBedHead(block.getBlockState().orElseThrow())) {
                loc = region.getBedNeighbor(block).getLocation();
            }
        } else if (region.isDoorBlock(block.getBlockState().orElseThrow())) {
            if (!region.isDoorBottomBlock(block.getBlockState().orElseThrow())) {
                loc = loc.subtract(0, 1, 0);
            }
        }
        var targetTeam = getTeamOfTargetBlock(loc);
        if (targetTeam != null) {
            if (configurationContainer.getOrDefault(GameConfigurationContainer.TARGET_BLOCK_CAKE_DESTROY_BY_EATING, false) && block.getType().isSameType("cake")) {
                return false; // when CAKES are in eating mode, don't allow to just break it
            } else {
                var pt = getPlayerTeam(player);
                if (pt == targetTeam) {
                    return false;
                }
                var result = internalProcessInvalidation(targetTeam, targetTeam.getTarget(), player, TargetInvalidationReason.TARGET_BLOCK_DESTROYED);
                if (result) {
                    try {
                        event.dropItems(false);
                    } catch (Throwable ignored) {
                    }
                }
                return result;
            }
        }
        if (BedWarsPlugin.isBreakableBlock(block.getType())) {
            region.putOriginalBlock(block.getLocation(), block.getBlockState().orElseThrow());
            return true;
        }
        return false;
    }

    public @Nullable TeamImpl getTeamOfTargetBlock(@NotNull LocationHolder loc) {
        for (var team : teamsInGame) {
            if (team.getTarget() instanceof TargetBlockImpl && ((TargetBlockImpl) team.getTarget()).getTargetBlock().equals(loc)) {
                return team;
            }
        }
        return null;
    }

    public BWRegion getRegion() {
        return region;
    }

    public TeamImpl getPlayerTeam(BedWarsPlayer player) {
        return teamsInGame.stream()
                .filter(team -> team.getPlayers().contains(player))
                .findFirst()
                .orElse(null);
    }

    public boolean internalProcessInvalidation(@NotNull TeamImpl team, @NotNull Target target, @Nullable PlayerWrapper destroyer, @NotNull TargetInvalidationReason reason) {
        return internalProcessInvalidation(team, target, destroyer, reason, true, false);
    }

    public boolean internalProcessInvalidation(@NotNull TeamImpl team, @NotNull Target target, @Nullable PlayerWrapper destroyer, @NotNull TargetInvalidationReason reason, boolean putOriginalBlocks, boolean ignoreInvalidity) {
        if (!teamsInGame.contains(team) || (!ignoreInvalidity && !target.isValid())) {
            return false;
        }

        @Nullable BlockTypeHolder type = null;
        if (target instanceof TargetBlockImpl) {
            var loc = ((TargetBlockImpl) target).getTargetBlock();
            type = loc.getBlock().getType();
        }

        var preTargetInvalidatedEvent = new PreTargetInvalidatedEventImpl(
                this,
                team,
                target,
                reason,
                type,
                destroyer != null ? PlayerManagerImpl.getInstance().getPlayer(destroyer).orElseThrow() : null
        );
        EventManager.fire(preTargetInvalidatedEvent);

        if (preTargetInvalidatedEvent.isCancelled()) {
            return false;
        }

        if (target instanceof TargetBlockImpl) {
            var loc = ((TargetBlockImpl) target).getTargetBlock();
            var block = loc.getBlock();
            if (type.is("#beds")) {
                if (!region.isBedHead(block.getBlockState().orElseThrow())) {
                    loc = region.getBedNeighbor(block).getLocation();
                }

                if (putOriginalBlocks) {
                    region.putOriginalBlock(loc, block.getBlockState().orElseThrow());
                    if (block.getLocation().equals(loc)) {
                        var neighbor = region.getBedNeighbor(block);
                        region.putOriginalBlock(neighbor.getLocation(), neighbor.getBlockState().orElseThrow());
                    } else {
                        region.putOriginalBlock(loc, region.getBedNeighbor(block).getBlockState().orElseThrow());
                    }
                }
                region.getBedNeighbor(block).setTypeWithoutPhysics(BlockTypeHolder.air());
                block.setType(BlockTypeHolder.air());
            } else if (type.is("#doors", "#tall_flowers")) {
                var neighbour = loc.add(0, type.get("half").map("lower"::equals).orElse(true) ? 1 : -1, 0);
                var neighbourBlock = neighbour.getBlock();
                if (neighbourBlock.getType().is("#doors", "#tall_flowers")) {
                    if (putOriginalBlocks) {
                        region.putOriginalBlock(neighbour, neighbourBlock.getBlockState().orElseThrow());
                    }
                    neighbourBlock.setTypeWithoutPhysics(BlockTypeHolder.air());
                }
                if (putOriginalBlocks) {
                    region.putOriginalBlock(loc, block.getBlockState().orElseThrow());
                }
                loc.getBlock().setType(BlockTypeHolder.air());
            } else {
                if (putOriginalBlocks) {
                    region.putOriginalBlock(loc, block.getBlockState().orElseThrow());
                }
                loc.getBlock().setType(BlockTypeHolder.air());
            }

            ((TargetBlockImpl) target).setValid(false);
        } else if (target instanceof ATargetCountdown) {
            ((ATargetCountdown) target).setRemainingTime(0);
        }

        var postTargetInvalidatedEvent = PostTargetInvalidatedEventImpl.fromPre(preTargetInvalidatedEvent);
        EventManager.fire(postTargetInvalidatedEvent);

        return true;
    }

    public void internalJoinPlayer(BedWarsPlayer gamePlayer) {
        var joinEvent = new PlayerJoinEventImpl(this, gamePlayer);
        EventManager.fire(joinEvent);

        if (joinEvent.isCancelled()) {
            Debug.info(gamePlayer.getName() + " can't join to the game: event cancelled");
            String message = joinEvent.getCancelMessage();
            if (message != null && !message.equals("")) {
                gamePlayer.sendMessage(message);
            }
            gamePlayer.changeGame(null);
            return;
        }
        Debug.info(gamePlayer.getName() + " joined bedwars match " + name);

        boolean isEmpty = players.isEmpty();
        if (!players.contains(gamePlayer)) {
            players.add(gamePlayer);
        }
        updateSigns();

        if (PlayerStatisticManager.isEnabled()) {
            // Load
            PlayerStatisticManager.getInstance().getStatistic(gamePlayer);
        }

        var arenaTime = configurationContainer.getOrDefault(GameConfigurationContainer.ARENA_TIME, ArenaTime.WORLD);
        if (arenaTime.time >= 0) {
            gamePlayer.setPlayerTime(arenaTime.time, false);
        }

        if (arenaWeather != null) {
            gamePlayer.setPlayerWeather(arenaWeather);
        }

        if (TabManager.isEnabled()) {
            players.forEach(TabManager.getInstance()::modifyForPlayer);
        }

        if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-foreign-players").getBoolean()) {
            Server.getConnectedPlayers().stream().filter(p -> PlayerManagerImpl.getInstance().getGameOfPlayer(p).orElse(null) != this).forEach(gamePlayer::hidePlayer);
            players.forEach(p -> p.showPlayer(gamePlayer));
        }

        if (status == GameStatus.WAITING) {
            Debug.info(gamePlayer.getName() + " moving to lobby");
            Message
                    .of(LangKeys.IN_GAME_JOIN)
                    .placeholder("name", gamePlayer.getDisplayName())
                    .placeholder("players", players.size())
                    .placeholder("maxplayers", calculatedMaxPlayers)
                    .prefixOrDefault(getCustomPrefixComponent())
                    .send(getConnectedPlayers().stream().map(PlayerMapper::wrapPlayer).collect(Collectors.toList()));

            gamePlayer.teleport(lobbySpawn, () -> {
                gamePlayer.invClean(); // temp fix for inventory issues?
                SpawnEffects.spawnEffect(GameImpl.this, gamePlayer, "game-effects.lobbyjoin");

                if (configurationContainer.getOrDefault(GameConfigurationContainer.JOIN_RANDOM_TEAM_ON_JOIN, false)) {
                    joinRandomTeam(gamePlayer);
                }

                if (configurationContainer.getOrDefault(GameConfigurationContainer.TEAM_JOIN_ITEM_ENABLED, false)) {
                    int compassPosition = MainConfig.getInstance().node("hotbar", "selector").getInt(0);
                    if (compassPosition >= 0 && compassPosition <= 8) {
                        var compass = MainConfig.getInstance()
                                .readDefinedItem("jointeam", "COMPASS")
                                .withDisplayName(Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_COMPASS_SELECTOR_TEAM).asComponent(gamePlayer));
                        gamePlayer.getPlayerInventory().setItem(compassPosition, compass);
                    }
                }

                int leavePosition = MainConfig.getInstance().node("hotbar", "leave").getInt(8);
                if (leavePosition >= 0 && leavePosition <= 8) {
                    var leave = MainConfig.getInstance()
                                    .readDefinedItem("leavegame", "SLIME_BALL")
                                    .withDisplayName(Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_LEAVE_FROM_GAME_ITEM).asComponent(gamePlayer));
                    gamePlayer.getPlayerInventory().setItem(leavePosition, leave);
                }

                if (gamePlayer.hasPermission(BedWarsPermission.START_ITEM_PERMISSION.asPermission())) {
                    int vipPosition = MainConfig.getInstance().node("hotbar", "start").getInt(1);
                    if (vipPosition >= 0 && vipPosition <= 8) {
                        var startGame = MainConfig.getInstance()
                                        .readDefinedItem("startgame", "DIAMOND")
                                        .withDisplayName(Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_START_GAME_ITEM).asComponent(gamePlayer));
                        gamePlayer.getPlayerInventory().setItem(vipPosition, startGame);
                    }
                }
            });

            if (isEmpty) {
                runTask();
            } else {
                statusbar.addPlayer(gamePlayer);
            }
        }

        if (status == GameStatus.RUNNING || status == GameStatus.GAME_END_CELEBRATING) {
            if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-spectators").getBoolean()) {
                players.stream().filter(p -> p.isSpectator() && !isPlayerInAnyTeam(p)).forEach(gamePlayer::hidePlayer);
            }

            makeSpectator(gamePlayer, true);

            spawners.forEach(itemSpawner -> {
                if (itemSpawner.getHologram() != null) {
                    itemSpawner.getHologram().addViewer(gamePlayer);
                }
            });
            teamsInGame.forEach(currentTeam -> {
                if (currentTeam.getHologram() != null) {
                    currentTeam.getHologram().addViewer(gamePlayer);
                }
            });

            otherVisuals.forEach(visual -> visual.addViewer(gamePlayer));

            if (healthIndicator != null) {
                healthIndicator.addViewer(gamePlayer);
            }
        }

        if (experimentalBoard != null) {
            experimentalBoard.addPlayer(gamePlayer);
        }

        EventManager.fire(new PlayerJoinedEventImpl(this, gamePlayer, getPlayerTeam(gamePlayer)));
    }

    public void internalLeavePlayer(BedWarsPlayer gamePlayer) {
        if (status == GameStatus.DISABLED) {
            return;
        }

        var playerLeaveEvent = new PlayerLeaveEventImpl(this, gamePlayer, getPlayerTeam(gamePlayer));
        EventManager.fire(playerLeaveEvent);
        Debug.info(name + ": player  " + gamePlayer.getName() + " is leaving the game");

        if (experimentalBoard != null) {
            experimentalBoard.removePlayer(gamePlayer);
        }

        if (healthIndicator != null) {
            healthIndicator.removeTrackedPlayer(gamePlayer);
            healthIndicator.removeViewer(gamePlayer);
        }

        if (!gamePlayer.isSpectator()) {
            if (!preServerRestart) {
                Message.of(LangKeys.IN_GAME_LEAVE)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .placeholder("name", gamePlayer.getDisplayName())
                        .placeholder("players", players.size())
                        .placeholder("maxplayers", calculatedMaxPlayers)
                        .send(players);
            }
        } else {
            if (gamePlayer.getSpectatorTarget().isPresent()) {
                gamePlayer.setSpectatorTarget(null);
            }
        }

        players.remove(gamePlayer);
        updateSigns();

        if (status == GameStatus.WAITING) {
            SpawnEffects.spawnEffect(this, gamePlayer, "game-effects.lobbyleave");
        }

        if (TabManager.isEnabled()) {
            TabManager.getInstance().clear(gamePlayer);
            players.forEach(TabManager.getInstance()::modifyForPlayer);
        }

        if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-foreign-players").getBoolean()) {
            players.forEach(p -> p.hidePlayer(gamePlayer));
        }

        statusbar.removePlayer(gamePlayer);
        spawners.forEach(spawner -> {
            if (spawner.getHologram() != null) {
                spawner.getHologram().removeViewer(gamePlayer);
            }
        });
        teamsInGame.forEach(team -> {
            if (team.getHologram() != null && !team.getPlayers().contains(gamePlayer)) {
                team.getHologram().removeViewer(gamePlayer);
            }
            if (team.getProtectHologram() != null && team.getPlayers().contains(gamePlayer)) {
                team.getProtectHologram().removeViewer(gamePlayer);
            }
        });
        otherVisuals.forEach(visual -> visual.removeViewer(gamePlayer));
        gamePlayer.restoreDefaultScoreboard();

        if (MainConfig.getInstance().node("mainlobby", "enabled").getBoolean()
                && !MainConfig.getInstance().node("bungee", "enabled").getBoolean()) {
            try {
                LocationHolder mainLobbyLocation = MiscUtils.readLocationFromString(
                        WorldMapper.getWorld(MainConfig.getInstance().node("mainlobby", "world").getString()).orElseThrow(),
                        Objects.requireNonNull(MainConfig.getInstance().node("mainlobby", "location").getString())
                );
                gamePlayer.teleport(mainLobbyLocation);
                gamePlayer.mainLobbyUsed = true;
            } catch (Throwable t) {
                BedWarsPlugin.getInstance().getLogger().error("You didn't setup the mainlobby properly! Do it via commands, not directly in config.yml!");
            }
        }

        if (status == GameStatus.RUNNING || status == GameStatus.WAITING) {
            var team = getPlayerTeam(gamePlayer);
            if (team != null) {
                team.getPlayers().remove(gamePlayer);
                if (status == GameStatus.WAITING) {
                    if (team.getPlayers().isEmpty()) {
                        teamsInGame.remove(team);
                    }
                }
            }
        }

        if (PlayerStatisticManager.isEnabled()) {
            var playerStatisticManager = PlayerStatisticManager.getInstance();
            var statistic = playerStatisticManager.getStatistic(gamePlayer);
            playerStatisticManager.storeStatistic(statistic);

            playerStatisticManager.unloadStatistic(gamePlayer);
        }

        if (players.isEmpty()) {
            if (!preServerRestart) {
                EventManager.fire(new PlayerLastLeaveEventImpl(this, gamePlayer, playerLeaveEvent.getTeam()));
            }

            if (status != GameStatus.WAITING) {
                afterRebuild = GameStatus.WAITING;
                updateSigns();
                rebuild();
            } else {
                cancelTask();
            }
            countdown = -1;
            teamsInGame.clear();

            for (GameStoreImpl store : gameStore) {
                var villager = store.kill();
                if (villager != null) {
                    EntitiesManagerImpl.getInstance().removeEntityFromGame(villager);
                }
            }
        }
    }

    @SneakyThrows
    public void saveToConfig() {
        var dir = BedWarsPlugin.getInstance().getPluginDescription().getDataFolder().resolve("arenas").toFile();
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
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
        configMap.node("uuid").set(uuid);
        configMap.node("name").set(name);
        configMap.node("pauseCountdown").set(pauseCountdown);
        configMap.node("gameTime").set(gameTime);
        configMap.node("world").set(world.getName());
        configMap.node("pos1").set(MiscUtils.writeLocationToString(pos1));
        configMap.node("pos2").set(MiscUtils.writeLocationToString(pos2));
        configMap.node("specSpawn").set(MiscUtils.writeLocationToString(specSpawn));
        configMap.node("lobbySpawn").set(MiscUtils.writeLocationToString(lobbySpawn));
        if (lobbyPos1 != null) {
            configMap.node("lobbyPos1", MiscUtils.writeLocationToString(lobbyPos1));
        }
        if (lobbyPos2 != null) {
            configMap.node("lobbyPos2", MiscUtils.writeLocationToString(lobbyPos2));
        }
        configMap.node("lobbySpawnWorld").set(lobbySpawn.getWorld().getName());
        configMap.node("minPlayers").set(minPlayers);
        configMap.node("postGameWaiting").set(postGameWaiting);
        configMap.node("game-display-name").set(displayName);
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
        for (var spawner : spawners) {
            spawner.saveTo(configMap.node("spawners").appendListNode());
        }
        for (var store : gameStore) {
            store.saveTo(configMap.node("stores").appendListNode());
        }

        configMap.node("constant").from(configurationContainer.getSaved());

        configMap.node("arenaWeather").set(arenaWeather == null ? "default" : arenaWeather.platformName());

        if (gameVariant != null) {
            configMap.node("variant").set(gameVariant.getName());
        }

        configMap.node("fee").set(fee);

        try {
            loader.save(configMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (status == GameStatus.DISABLED) {
            preparing = true;
            status = GameStatus.WAITING;
            countdown = -1;
            calculatedMaxPlayers = 0;
            for (TeamImpl team : teams) {
                calculatedMaxPlayers += team.getMaxPlayers();
            }
            Tasker.build(GameImpl.this::updateSigns).afterOneTick().start();

            if (MainConfig.getInstance().node("bossbar", "use-xp-bar").getBoolean(false)) {
                statusbar = new XPBarImpl();
            } else {
                statusbar = new BossBarImpl();
            }
            preparing = false;
        }
    }

    public void stop() {
        if (status == GameStatus.DISABLED) {
            return; // Game is already stopped
        }
        var clonedPlayers = new ArrayList<>(players);
        for (BedWarsPlayer p : clonedPlayers) {
            p.changeGame(null);
        }
        if (status != GameStatus.REBUILDING) {
            status = GameStatus.DISABLED;
            updateSigns();
        } else {
            afterRebuild = GameStatus.DISABLED;
        }
    }

    @Override
    public void joinToGame(BWPlayer p) {
        if (!(p instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        var player = (BedWarsPlayer) p;

        if (status == GameStatus.DISABLED) {
            return;
        }

        if (preparing) {
            Tasker.build(() -> joinToGame(player)).delay(1L, TaskerTime.TICKS).start();
            return;
        }

        if (status == GameStatus.REBUILDING) {
            if (isBungeeEnabled()) {
                BungeeUtils.movePlayerToBungeeServer(player, false);
                BungeeUtils.sendPlayerBungeeMessage(player, Message
                                .of(LangKeys.IN_GAME_ERRORS_GAME_IS_REBUILDING)
                                .prefixOrDefault(getCustomPrefixComponent())
                                .placeholder("arena", this.name)
                                .asComponent(player)
                                .toLegacy()
                        );
            } else {
                Message
                        .of(LangKeys.IN_GAME_ERRORS_GAME_IS_REBUILDING)
                        .placeholder("arena", this.name)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .send(player);
            }
            return;
        }

        if ((status == GameStatus.RUNNING || status == GameStatus.GAME_END_CELEBRATING)
                && !configurationContainer.getOrDefault(GameConfigurationContainer.ALLOW_SPECTATOR_JOIN, false)) {
            if (isBungeeEnabled()) {
                BungeeUtils.movePlayerToBungeeServer(player, false);
                BungeeUtils.sendPlayerBungeeMessage(player,
                        Message
                                .of(LangKeys.IN_GAME_ERRORS_GAME_ALREADY_RUNNING)
                                .placeholder("arena", this.name)
                                .prefixOrDefault(getCustomPrefixComponent())
                                .asComponent(player)
                                .toLegacy()
                );
            } else {
                Message
                        .of(LangKeys.IN_GAME_ERRORS_GAME_ALREADY_RUNNING)
                        .placeholder("arena", this.name)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .send(player);
            }
            return;
        }

        if (players.size() >= calculatedMaxPlayers && status == GameStatus.WAITING) {
            if (player.canJoinFullGame()) {
                List<BedWarsPlayer> withoutVIP = getPlayersWithoutVIP();

                if (withoutVIP.size() == 0) {
                    Message
                            .of(LangKeys.IN_GAME_ERRORS_VIP_GAME_IS_FULL)
                            .prefixOrDefault(getCustomPrefixComponent())
                            .send(player);
                    return;
                }

                BedWarsPlayer kickPlayer;
                if (withoutVIP.size() == 1) {
                    kickPlayer = withoutVIP.get(0);
                } else {
                    kickPlayer = withoutVIP.get(MiscUtils.randInt(0, players.size() - 1));
                }

                if (isBungeeEnabled()) {
                    BungeeUtils.sendPlayerBungeeMessage(kickPlayer,
                            Message
                                    .of(LangKeys.IN_GAME_ERRORS_GAME_KICKED_BY_VIP)
                                    .placeholder("arena", this.name)
                                    .prefixOrDefault(getCustomPrefixComponent())
                                    .asComponent(kickPlayer)
                                    .toLegacy()
                    );
                } else {
                    Message
                            .of(LangKeys.IN_GAME_ERRORS_GAME_KICKED_BY_VIP)
                            .placeholder("arena", this.name)
                            .prefixOrDefault(getCustomPrefixComponent())
                            .send(kickPlayer);
                }
                kickPlayer.changeGame(null);
            } else {
                if (isBungeeEnabled()) {
                    BungeeUtils.movePlayerToBungeeServer(player, false);
                    Tasker.build(() -> BungeeUtils.sendPlayerBungeeMessage(player,
                            Message
                                    .of(LangKeys.IN_GAME_ERRORS_GAME_IS_FULL)
                                    .placeholder("arena", GameImpl.this.name)
                                    .prefixOrDefault(getCustomPrefixComponent())
                                    .asComponent(player)
                                    .toLegacy()
                    )).delay(5, TaskerTime.TICKS).start();
                } else {
                    Message
                            .of(LangKeys.IN_GAME_ERRORS_GAME_IS_FULL)
                            .placeholder("arena", this.name)
                            .prefixOrDefault(getCustomPrefixComponent())
                            .send(player);
                }
                return;
            }
        }

        if (MainConfig.getInstance().node("economy", "enabled").getBoolean(true) && EconomyManager.isAvailable()) {
            if (fee > 0) {
                if (!EconomyManager.withdrawPlayer(player, fee).isSuccessful()) {
                    Message.of(LangKeys.IN_GAME_ECONOMY_MISSING_COINS)
                            .placeholder("coins", fee)
                            .placeholder("currency", EconomyManager.getCurrencyNameSingular())
                            .send(player);
                    return;
                }
            }
        }

        player.changeGame(this);
    }

    @Override
    public void leaveFromGame(BWPlayer p) {
        if (!(p instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        var player = (BedWarsPlayer) p;

        if (status == GameStatus.DISABLED) {
            return;
        }
        if (player.isInGame() && player.getGame() == this) {
            if (MainConfig.getInstance().node("economy", "enabled").getBoolean(true) && EconomyManager.isAvailable()) {
                if (fee > 0 && MainConfig.getInstance().node("economy", "return-fee").getBoolean(true)) {
                    EconomyUtils.deposit(player, fee);
                }
            }
            player.changeGame(null);
        }
    }

    public TeamImpl getFirstTeamThatIsntInGame() {
        for (TeamImpl team : teams) {
            if (!teamsInGame.contains(team)) {
                return team;
            }
        }
        return null;
    }

    public List<BedWarsPlayer> getPlayersInTeam(TeamImpl team) {
        return team.getPlayers();
    }

    public boolean internalTeamJoin(BedWarsPlayer player, TeamImpl teamForJoin, boolean ignoreTeamSize) {
        var cur = getPlayerTeam(player);
        var event = new PlayerJoinTeamEventImpl(this, player, teamForJoin, cur);
        EventManager.fire(event);

        if (event.isCancelled()) {
            return false;
        }

        if (cur == teamForJoin) {
            Message
                    .of(LangKeys.IN_GAME_TEAM_SELECTION_ALREADY_SELECTED)
                    .prefixOrDefault(getCustomPrefixComponent())
                    .placeholder("team", Component.text(teamForJoin.getName(), teamForJoin.getColor().getTextColor()))
                    .placeholder("players", teamForJoin.countConnectedPlayers())
                    .placeholder("maxplayers", teamForJoin.getMaxPlayers())
                    .send(player);
            return false;
        }
        if (!ignoreTeamSize && teamForJoin.countConnectedPlayers() >= teamForJoin.getMaxPlayers()) {
            if (cur != null) {
                Message
                        .of(LangKeys.IN_GAME_TEAM_SELECTION_FULL_NO_CHANGE)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .placeholder("team", Component.text(teamForJoin.getName(), teamForJoin.getColor().getTextColor()))
                        .placeholder("oldteam", Component.text(cur.getName(), cur.getColor().getTextColor()))
                        .send(player);
            } else {
                Message
                        .of(LangKeys.IN_GAME_TEAM_SELECTION_FULL)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .placeholder("team", Component.text(teamForJoin.getName(), teamForJoin.getColor().getTextColor()))
                        .send(player);
            }
            return false;
        }

        if (cur != null) {
            cur.getPlayers().remove(player);

            if (cur.getPlayers().isEmpty()) {
                teamsInGame.remove(cur);
            }
            Debug.info(name + ": player " + player.getName() + " left the team " + cur.getName());
        }

        teamForJoin.getPlayers().add(player);

        Debug.info(name + ": player " + player.getName() + " joined the team " + teamForJoin.getName());

        Message
                .of(LangKeys.IN_GAME_TEAM_SELECTION_SELECTED)
                .prefixOrDefault(getCustomPrefixComponent())
                .placeholder("team", Component.text(teamForJoin.getName(), teamForJoin.getColor().getTextColor()))
                .placeholder("players", teamForJoin.getPlayers().size())
                .placeholder("maxplayers", teamForJoin.getMaxPlayers())
                .send(player);

        if (this.status == GameStatus.WAITING) {
            if (configurationContainer.getOrDefault(GameConfigurationContainer.ADD_WOOL_TO_INVENTORY_ON_JOIN, false)) {
                int colorPosition = MainConfig.getInstance().node("hotbar", "color").getInt(1);
                if (colorPosition >= 0 && colorPosition <= 8) {
                    var item = ItemFactory.build(teamForJoin.getColor().material1_13 + "_WOOL",
                            builder -> builder.displayName(Component.text(teamForJoin.getName(), teamForJoin.getColor().getTextColor()))
                    ).orElse(ItemFactory.getAir());
                    player.getPlayerInventory().setItem(colorPosition, item);
                }
            }

            if (configurationContainer.getOrDefault(GameConfigurationContainer.COLORED_LEATHER_BY_TEAM_IN_LOBBY, false)) {
                var chestplate = ItemFactory.build("LEATHER_CHESTPLATE", builder ->
                        builder.color(teamForJoin.getColor().getLeatherColor())
                ).orElse(ItemFactory.getAir());
                player.getPlayerInventory().setChestplate(chestplate);
            }
        }

        if (!teamsInGame.contains(teamForJoin)) {
            teamsInGame.add(teamForJoin);
        }

        if (TabManager.isEnabled()) {
            players.forEach(TabManager.getInstance()::modifyForPlayer);
        }

        EventManager.fire(new PlayerJoinedTeamEventImpl(this, player, teamForJoin, cur));

        return true;
    }

    public void joinRandomTeam(BedWarsPlayer player) {
        // TODO: add api event to allow manipulation with this process
        var teamForJoin = this.chooseRandomTeamForPlayerToJoin(false, false);

        if (teamForJoin == null) {
            return;
        }

        internalTeamJoin(player, teamForJoin, false);
    }

    public @Nullable TeamImpl chooseRandomTeamForPlayerToJoin(boolean ignoreTeamSize, boolean onlyActiveTeams) {
        @Nullable TeamImpl teamForJoin = null;
        if (!onlyActiveTeams && teamsInGame.size() < 2) {
            teamForJoin = getFirstTeamThatIsntInGame();
        } else {
            @Nullable TeamImpl lowest = null;

            for (var team : teamsInGame) {
                if (!ignoreTeamSize && team.getPlayers().size() >= team.getMaxPlayers()) {
                    continue; // skip full teams
                }

                if (lowest == null) {
                    lowest = team;
                }

                if (lowest.getPlayers().size() > team.getPlayers().size()) {
                    lowest = team;
                }
            }
            if (lowest != null) {
                teamForJoin = lowest;
            } else if (!onlyActiveTeams) {
                teamForJoin = getFirstTeamThatIsntInGame();
            }
        }

        return teamForJoin;
    }

    public LocationHolder makeSpectator(BedWarsPlayer gamePlayer, boolean leaveItem) {
        Debug.info(gamePlayer.getName() + " spawning as spectator");
        gamePlayer.setSpectator(true);
        gamePlayer.teleport(specSpawn, () -> {
            if (!configurationContainer.getOrDefault(GameConfigurationContainer.KEEP_INVENTORY_ON_DEATH, false) || leaveItem) {
                gamePlayer.invClean(); // temp fix for inventory issues?
            }
            gamePlayer.setAllowFlight(true);
            gamePlayer.setFlying(true);
            gamePlayer.setGameMode(GameModeHolder.of("spectator"));

            if (leaveItem) {
                if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-spectators").getBoolean()) {
                    players.forEach(p -> p.hidePlayer(gamePlayer));
                }

                int leavePosition = MainConfig.getInstance().node("hotbar", "leave").getInt(8);
                if (leavePosition >= 0 && leavePosition <= 8) {
                    var leave = MainConfig.getInstance()
                            .readDefinedItem("leavegame", "SLIME_BALL")
                            .withDisplayName(Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_LEAVE_FROM_GAME_ITEM).asComponent(gamePlayer));
                    gamePlayer.getPlayerInventory().setItem(leavePosition, leave);
                }
            }

            if (TabManager.isEnabled()) {
                players.forEach(TabManager.getInstance()::modifyForPlayer);
            }
            healthIndicator.removeTrackedPlayer(gamePlayer);

            Tasker
                    .build(() -> {
                        if (!gamePlayer.getGameMode().is("spectator")) { // Fix Multiverse overriding our gamemode
                            gamePlayer.setGameMode(GameModeHolder.of("spectator"));
                        }
                    })
                    .delay(2, TaskerTime.TICKS)
                    .start();
        }, true);

        return specSpawn;
    }

    public void makePlayerFromSpectator(BedWarsPlayer gamePlayer) {
        Debug.info(gamePlayer.getName() + " changing spectator to regular player");
        var currentTeam = getPlayerTeam(gamePlayer);

        if (gamePlayer.getGame() == this && currentTeam != null) {
            gamePlayer.setSpectator(false);
            if (gamePlayer.getSpectatorTarget().isPresent()) {
                gamePlayer.setSpectatorTarget(null);
            }
            gamePlayer.teleport(MiscUtils.findEmptyLocation(currentTeam.getRandomSpawn()), () -> {
                gamePlayer.setAllowFlight(false);
                gamePlayer.setFlying(false);
                gamePlayer.setGameMode(GameModeHolder.of("survival"));

                if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-spectators").getBoolean()) {
                    players.forEach(p -> p.showPlayer(gamePlayer));
                }

                if (configurationContainer.getOrDefault(GameConfigurationContainer.RESPAWN_PROTECTION_ENABLED, true)) {
                    RespawnProtection respawnProtection = addProtectedPlayer(gamePlayer);
                    respawnProtection.runProtection();
                }

                if (configurationContainer.getOrDefault(GameConfigurationContainer.PLAYER_RESPAWN_ITEMS_ENABLED, false)) {
                    var playerRespawnItems = configurationContainer.getOrDefault(GameConfigurationContainerImpl.PLAYER_RESPAWN_ITEMS_ITEMS, List.of());
                    if (!playerRespawnItems.isEmpty()) {
                        MiscUtils.giveItemsToPlayer(playerRespawnItems, gamePlayer, currentTeam.getColor());
                    } else {
                        Debug.warn("You have wrongly configured player-respawn-items.items!", true);
                    }
                }
                MiscUtils.giveItemsToPlayer(gamePlayer.getPermanentItemsPurchased(), gamePlayer, currentTeam.getColor());

                if (configurationContainer.getOrDefault(GameConfigurationContainer.KEEP_ARMOR_ON_DEATH, false)) {
                    final var armorContents = gamePlayer.getArmorContents();
                    if (armorContents != null) {
                        gamePlayer.getPlayerInventory().setArmorContents(armorContents);
                    }
                }

                if (TabManager.isEnabled()) {
                    players.forEach(TabManager.getInstance()::modifyForPlayer);
                }
                healthIndicator.addTrackedPlayer(gamePlayer);

                EventManager.fire(new PlayerRespawnedEventImpl(this, gamePlayer));
            });
        }
    }

    public void setBossbarProgress(int count, int max) {
        double progress = (double) count / (double) max;
        statusbar.setProgress((float) progress);
        if (statusbar instanceof XPBarImpl) {
            XPBarImpl xpbar = (XPBarImpl) statusbar;
            xpbar.setSeconds(count);
        }
    }

    public void run() {
        // Phase 1: Check if game is running
        if (status == GameStatus.DISABLED) { // Game is not running, why cycle is still running?
            cancelTask();
            return;
        }
        var statusE = new GameChangedStatusEventImpl(this);
        // Phase 2: If this is first tick, prepare waiting lobby
        if (countdown == -1 && status == GameStatus.WAITING) {
            Debug.info(name + ": preparing lobby");
            previousCountdown = countdown = pauseCountdown;
            previousStatus = GameStatus.WAITING;
            var title = Message.of(LangKeys.IN_GAME_BOSSBAR_WAITING).asComponent();
            statusbar.setProgress(0);
            statusbar.setVisible(configurationContainer.getOrDefault(GameConfigurationContainer.BOSSBAR_LOBBY_ENABLED, false));
            for (BedWarsPlayer p : players) {
                statusbar.addPlayer(p);
            }
            if (statusbar instanceof BossBarImpl) {
                var bossbar = (BossBarImpl) statusbar;
                bossbar.setMessage(title);
                bossbar.setColor(configurationContainer.getOrDefault(GameConfigurationContainerImpl.BOSSBAR_LOBBY_COLOR, BossBarColor.PURPLE));
                bossbar.setStyle(configurationContainer.getOrDefault(GameConfigurationContainerImpl.BOSSBAR_LOBBY_DIVISION, BossBarDivision.NO_DIVISION));
            }
            if (teamSelectorInventory == null) {
                teamSelectorInventory = new TeamSelectorInventory(this);
            }

            if (experimentalBoard == null) {
                experimentalBoard = new GameSidebar(this);
            }
            updateSigns();
            Debug.info(name + ": lobby prepared");
        }

        // Phase 3: Prepare information about next tick for tick event and update
        // bossbar with scoreboard
        int nextCountdown = countdown;
        GameStatus nextStatus = status;

        if (status == GameStatus.WAITING) {
            // Game start item
            if (gameStartItem) {
                if (players.size() >= getMinPlayers()) {
                    for (BedWarsPlayer player : players) {
                        if (getPlayerTeam(player) == null) {
                            joinRandomTeam(player);
                        }
                    }
                }
                if (players.size() > 1) {
                    countdown = 0;
                    gameStartItem = false;
                }
            }
            if (forceGameToStart) {
                nextCountdown = gameTime;
                nextStatus = GameStatus.RUNNING;
                forceGameToStart = false;

                for (BedWarsPlayer player : players) {
                    if (getPlayerTeam(player) == null) {
                        joinRandomTeam(player);
                    }
                }
                if (teamsInGame.size() == 1) { // I don't think zero can happen as at least one player will be there
                    for (var team : teams) {
                        if (!teamsInGame.contains(team)) {
                            team.setForced(true);
                            teamsInGame.add(team);
                            break;
                        }
                    }
                }
            } else if (
                    players.size() >= getMinPlayers()
                    && (
                            teamsInGame.size() > 1
                            || (configurationContainer.getOrDefault(GameConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY, false) && countRespawnable() < players.size())
                    )
            ) {
                if (countdown == 0) {
                    nextCountdown = gameTime;
                    nextStatus = GameStatus.RUNNING;
                } else {
                    nextCountdown--;

                    if (countdown <= 10 && countdown >= 1 && countdown != previousCountdown) {

                        for (BedWarsPlayer player : players) {
                            player.showTitle(Title.title(Component.text(Integer.toString(countdown), Color.YELLOW), Component.empty(), TitleUtils.defaultTimes()));
                            player.playSound(
                                    SoundStart.sound(
                                            SpecialSoundKey.key(MainConfig.getInstance().node("sounds", "countdown", "sound").getString("ui.button.click")),
                                            SoundSource.AMBIENT,
                                            (float) MainConfig.getInstance().node("sounds", "countdown", "volume").getDouble(1),
                                            (float) MainConfig.getInstance().node("sounds", "countdown", "pitch").getDouble(1)
                                    )
                            );
                        }
                    }
                }
            } else {
                nextCountdown = countdown = pauseCountdown;
            }
            setBossbarProgress(countdown, pauseCountdown);
        } else if (status == GameStatus.RUNNING) {
            if (countdown == 0) {
                nextCountdown = postGameWaiting;
                nextStatus = GameStatus.GAME_END_CELEBRATING;
            } else {
                nextCountdown--;
            }
            setBossbarProgress(countdown, gameTime);
        } else if (status == GameStatus.GAME_END_CELEBRATING) {
            if (countdown == 0) {
                nextCountdown = 0;
                nextStatus = GameStatus.REBUILDING;
            } else {
                nextCountdown--;
            }
            setBossbarProgress(countdown, postGameWaiting);
        }

        // Phase 4: Call Tick Event
        var tick = new GameTickEventImpl(this, previousCountdown, previousStatus, countdown, status,
                nextCountdown, nextStatus, nextCountdown, nextStatus);
        EventManager.fire(tick);
        Debug.info(name + ": tick passed: " + tick.getPreviousCountdown() + "," + tick.getCountdown() + "," + tick.getNextCountdown() + " (" + tick.getPreviousStatus() + "," + tick.getStatus() + "," + tick.getNextStatus() + ")");

        // Phase 5: Update Previous information
        previousCountdown = countdown;
        previousStatus = status;

        // Phase 6: Process tick
        // Phase 6.1: If status changed
        if (status != tick.getNextStatus()) {
            // Phase 6.1.1: Prepare game if next status is RUNNING
            if (tick.getNextStatus() == GameStatus.RUNNING) {
                Debug.info(name + ": preparing game");
                preparing = true;
                var startE = new GameStartEventImpl(this);
                EventManager.fire(startE);
                EventManager.fire(statusE);

                if (startE.isCancelled()) {
                    tick.setNextCountdown(pauseCountdown);
                    tick.setNextStatus(GameStatus.WAITING);
                    preparing = false;
                } else {

                    if (configurationContainer.getOrDefault(GameConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY, false)) {
                        for (BedWarsPlayer player : players) {
                            if (getPlayerTeam(player) == null) {
                                joinRandomTeam(player);
                            }
                        }
                    }

                    statusbar.setProgress(0);
                    statusbar.setVisible(configurationContainer.getOrDefault(GameConfigurationContainer.BOSSBAR_GAME_ENABLED, false));
                    if (statusbar instanceof BossBarImpl) {
                        var bossbar = (BossBarImpl) statusbar;
                        bossbar.setMessage(Message.of(LangKeys.IN_GAME_BOSSBAR_RUNNING).asComponent());
                        bossbar.setColor(configurationContainer.getOrDefault(GameConfigurationContainerImpl.BOSSBAR_GAME_COLOR, BossBarColor.PURPLE));
                        bossbar.setStyle(configurationContainer.getOrDefault(GameConfigurationContainerImpl.BOSSBAR_GAME_DIVISION, BossBarDivision.NO_DIVISION));
                    }
                    if (teamSelectorInventory != null)
                        teamSelectorInventory.destroy();
                    teamSelectorInventory = null;

                    Tasker.build(this::updateSigns).delay(3, TaskerTime.TICKS).start();
                    for (GameStoreImpl store : gameStore) {
                        var villager = store.spawn();
                        if (villager instanceof EntityLiving) {
                            EntitiesManagerImpl.getInstance().addEntityToGame(villager, this);
                            ((EntityLiving) villager).setAI(false);
                            ((EntityLiving) villager).getLocation().getNearbyEntities(1).forEach(entity -> {
                                if (entity.getEntityType().equals(((EntityLiving) villager).getEntityType()) && entity.getLocation().getBlock().equals(((EntityLiving) villager).getLocation().getBlock()) && !villager.equals(entity)) {
                                    entity.remove();
                                }
                            });
                        } else if (villager instanceof NPC) {
                            otherVisuals.add((NPC) villager);
                            players.forEach(((NPC) villager)::addViewer);
                        }
                    }

                    for (ItemSpawnerImpl spawner : spawners) {
                        spawner.start(this);

                        UpgradeStorage storage = UpgradeRegistry.getUpgrade("spawner");
                        if (storage != null) {
                            storage.addUpgrade(this, spawner);
                        }
                    }

                    var title = Message
                                    .of(LangKeys.IN_GAME_GAME_START_TITLE)
                                    .join(LangKeys.IN_GAME_GAME_START_SUBTITLE)
                                    .placeholder("arena", this.name)
                                    .times(TitleUtils.defaultTimes());
                    if (configurationContainer.getOrDefault(GameConfigurationContainer.SHOW_GAME_INFO_ON_START, false)) {
                        Message
                                .of(LangKeys.IN_GAME_MESSAGES_GAME_START)
                                .placeholderRaw("resources", MiscUtils.center(gameVariant.getItemSpawnerTypes().stream().map(ItemSpawnerTypeImpl::getName).collect(Collectors.joining(", ")), 49))
                                .send(players);
                    }
                    for (BedWarsPlayer player : this.players) {
                        Debug.info(name + ": moving " + player.getName() + " into game");
                        var team = getPlayerTeam(player);
                        player.getPlayerInventory().clear();
                        // Player still had armor on legacy versions
                        player.getPlayerInventory().setHelmet(null);
                        player.getPlayerInventory().setChestplate(null);
                        player.getPlayerInventory().setLeggings(null);
                        player.getPlayerInventory().setBoots(null);
                        player.showTitle(title);
                        if (team == null) {
                            var loc = makeSpectator(player, true);
                                player.playSound(
                                        SoundStart.sound(
                                                SpecialSoundKey.key(MainConfig.getInstance().node("sounds", "game_start", "sound").getString("entity.player.levelup")),
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
                                player.setGameMode(GameModeHolder.of("survival"));
                                if (configurationContainer.getOrDefault(GameConfigurationContainer.GAME_START_ITEMS_ENABLED, false)) {
                                    var givenGameStartItems = configurationContainer.getOrDefault(GameConfigurationContainerImpl.GAME_START_ITEMS_ITEMS, List.of());
                                    if (!givenGameStartItems.isEmpty()) {
                                        MiscUtils.giveItemsToPlayer(givenGameStartItems, player, team.getColor());
                                    } else {
                                        Debug.warn("You have wrongly configured game-start-items.items!", true);
                                    }
                                }
                                SpawnEffects.spawnEffect(this, player, "game-effects.start");
                                player.playSound(
                                        SoundStart.sound(
                                                SpecialSoundKey.key(MainConfig.getInstance().node("sounds", "game_start", "sound").getString("entity.player.levelup")),
                                                SoundSource.AMBIENT,
                                                (float) MainConfig.getInstance().node("sounds", "game_start", "volume").getDouble(1),
                                                (float) MainConfig.getInstance().node("sounds", "game_start", "pitch").getDouble(1)
                                        )
                                );
                            });
                        }
                    }

                    if (configurationContainer.getOrDefault(GameConfigurationContainer.REMOVE_UNUSED_TARGET_BLOCKS, false)) {
                        for (TeamImpl team : teams) {
                            if (!teamsInGame.contains(team) && team.getTarget() instanceof TargetBlockImpl) {
                                LocationHolder loc = ((TargetBlockImpl) team.getTarget()).getTargetBlock();
                                BlockHolder block = loc.getBlock();
                                if (region.isBedBlock(block.getBlockState().orElseThrow())) {
                                    region.putOriginalBlock(block.getLocation(), block.getBlockState().orElseThrow());
                                    var neighbor = region.getBedNeighbor(block);
                                    region.putOriginalBlock(neighbor.getLocation(), neighbor.getBlockState().orElseThrow());
                                    neighbor.setTypeWithoutPhysics(BlockTypeHolder.air());
                                } else {
                                    region.putOriginalBlock(loc, block.getBlockState().orElseThrow());
                                }
                                block.setType(BlockTypeHolder.air());
                            }
                        }
                    }

                    for (var team : teamsInGame) {
                        team.start();
                    }

                    if (Server.isVersion(1, 15) && (!PlatformService.getInstance().getFakeDeath().isAvailable() || !configurationContainer.getOrDefault(GameConfigurationContainer.ALLOW_FAKE_DEATH, false))) {
                        world.setGameRuleValue(GameRuleHolder.of("doImmediateRespawn"), true);
                    }
                    preparing = false;

                    var startedEvent = new GameStartedEventImpl(this);
                    EventManager.fire(startedEvent);
                    EventManager.fire(statusE);
                    Debug.info(name + ": game prepared");

                    if (configurationContainer.getOrDefault(GameConfigurationContainer.ENABLE_BELOW_NAME_HEALTH_INDICATOR, false)) {
                        healthIndicator = HealthIndicator.of()
                                .symbol(Component.text("\u2665", Color.RED))
                                .show()
                                .startUpdateTask(4, TaskerTime.TICKS);
                        players.forEach(healthIndicator::addViewer);
                        players.stream().filter(bedWarsPlayer -> !bedWarsPlayer.isSpectator()).forEach(healthIndicator::addTrackedPlayer);
                    }
                }

                // show records
                RecordSave.getInstance().getRecord(this.getName()).ifPresentOrElse(record ->
                        Message.of(LangKeys.IN_GAME_RECORD_CURRENT)
                                .prefixOrDefault(getCustomPrefixComponent())
                                .placeholder("time", getFormattedTimeLeft(record.getTime()))
                                .placeholderRaw("team-members", String.join(", ", record.getWinners()))
                                .send(players), () ->
                        Message.of(LangKeys.IN_GAME_RECORD_NO)
                                        .prefixOrDefault(getCustomPrefixComponent())
                                        .send(players)
                );

            }
            // Phase 6.2: If status is same as before
        } else {
            // Phase 6.2.1: On game tick (if not interrupted by a change of status)
            if (status == GameStatus.RUNNING && tick.getNextStatus() == GameStatus.RUNNING) {
                int runningTeams = 0;
                var invalidated = new ArrayList<TargetBlockDestroyedInfo>();
                for (var t : teamsInGame) {
                    runningTeams += t.isAlive() ? 1 : 0;
                    if (t.isAlive() && t.getTarget() instanceof ATargetCountdown && t.getTarget().isValid()) {
                        ((ATargetCountdown) t.getTarget()).setRemainingTime(((ATargetCountdown) t.getTarget()).getRemainingTime() - 1);
                        if (!t.getTarget().isValid()) {
                            var info = new TargetBlockDestroyedInfo(this, t);
                            if (t.getTarget() instanceof TargetBlockCountdownImpl && configurationContainer.getOrDefault(GameConfigurationContainer.USE_CERTAIN_POPULAR_SERVER_TITLES, false)) {
                                invalidated.add(info);
                            }

                            internalProcessInvalidation(t, t.getTarget(), null, TargetInvalidationReason.TIMEOUT, true, true);
                        }
                    }
                }
                if (!invalidated.isEmpty()) {
                    if (teamsInGame.stream().anyMatch(team -> team.getTarget().isValid())) {
                        for (var t : invalidated) {
                            Message
                                    .of(t.isItBedBlock() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_BED_CERTAIN_POPULAR_SERVER : (t.isItAnchor() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANCHOR_CERTAIN_POPULAR_SERVER : (t.isItCake() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_CAKE_CERTAIN_POPULAR_SERVER : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANY_CERTAIN_POPULAR_SERVER)))
                                    .join(LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_SUBTITLE_VICTIM)
                                    .times(TitleUtils.defaultTimes())
                                    .title(t.getTeam().getPlayers());
                        }
                    } else {
                        var allBeds = invalidated.stream().allMatch(TargetBlockDestroyedInfo::isItBedBlock);
                        var allAnchors = invalidated.stream().allMatch(TargetBlockDestroyedInfo::isItAnchor);
                        var allCakes = invalidated.stream().allMatch(TargetBlockDestroyedInfo::isItCake);
                        for (var t : invalidated) {
                            Message
                                    .of(t.isItBedBlock() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_BED_CERTAIN_POPULAR_SERVER : (t.isItAnchor() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANCHOR_CERTAIN_POPULAR_SERVER : (t.isItCake() ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_CAKE_CERTAIN_POPULAR_SERVER : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANY_CERTAIN_POPULAR_SERVER)))
                                    .join(allBeds ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ALL_BEDS : (allAnchors ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ALL_ANCHORS : (allCakes ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ALL_CAKES : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ALL_TARGET_BLOCKS)))
                                    .times(TitleUtils.defaultTimes())
                                    .title(t.getTeam().getPlayers());
                        }
                    }
                }
                if (runningTeams <= 1) {
                    if (runningTeams == 1) {
                        TeamImpl winner = null;
                        for (var t : teamsInGame) {
                            if (t.isAlive()) {
                                winner = t;
                                String time = getFormattedTimeLeft(gameTime - countdown);
                                var message = Message
                                        .of(LangKeys.IN_GAME_END_TEAM_WIN)
                                        .prefixOrDefault(getCustomPrefixComponent())
                                        .placeholder("team", Component.text(t.getName(), t.getColor().getTextColor()))
                                        .placeholder("time", time);
                                boolean madeRecord = processRecord(t, gameTime - countdown);
                                for (BedWarsPlayer player : players) {
                                    player.sendMessage(message);
                                    if (getPlayerTeam(player) == t) {
                                        Message.of(LangKeys.IN_GAME_END_YOU_WON)
                                                .join(LangKeys.IN_GAME_END_TEAM_WIN)
                                                .placeholder("team", Component.text(t.getName(), t.getColor().getTextColor()))
                                                .placeholder("time", time)
                                                .times(TitleUtils.defaultTimes())
                                                .title(player);
                                        EconomyUtils.deposit(player, configurationContainer.getOrDefault(GameConfigurationContainer.ECONOMY_REWARD_WIN, 0.0));

                                        SpawnEffects.spawnEffect(this, player, "game-effects.end");

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
                                                GameImpl.this.dispatchRewardCommands("player-win-run-immediately", player, statistic.getScore());
                                            } else {
                                                GameImpl.this.dispatchRewardCommands("player-win-run-immediately", player, 0);
                                            }
                                            Tasker.build(() -> {
                                                if (PlayerStatisticManager.isEnabled()) {
                                                    var statistic = PlayerStatisticManager.getInstance().getStatistic(player);
                                                    GameImpl.this.dispatchRewardCommands("player-win", player, statistic.getScore());
                                                } else {
                                                    GameImpl.this.dispatchRewardCommands("player-win", player, 0);
                                                }
                                            }).delay((2 + postGameWaiting) * 20L, TaskerTime.TICKS).start();
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

                        var endingEvent = new GameEndingEventImpl(this, winner);
                        EventManager.fire(endingEvent);
                        EventManager.fire(statusE);
                        Debug.info(name + ": game is ending");

                        tick.setNextCountdown(postGameWaiting);
                        tick.setNextStatus(GameStatus.GAME_END_CELEBRATING);
                    } else {
                        tick.setNextStatus(GameStatus.REBUILDING);
                        tick.setNextCountdown(0);
                    }
                }
            }
        }

        // Phase 7: Update status and countdown for next tick
        countdown = tick.getNextCountdown();
        status = tick.getNextStatus();

        // Phase 8: Check if game end celebrating started and remove title on bossbar
        if (status == GameStatus.GAME_END_CELEBRATING && previousStatus != status) {
            if (statusbar instanceof BossBarImpl) {
                var bossbar = (BossBarImpl) statusbar;
                bossbar.setMessage(Component.empty());
            }
        }

        // Phase 9: Check if status is rebuilding and rebuild game
        if (status == GameStatus.REBUILDING) {
            var event = new GameEndEventImpl(this);
            EventManager.fire(event);
            EventManager.fire(statusE);

            var message = Message
                    .of(LangKeys.IN_GAME_END_GAME_END)
                    .prefixOrDefault(getCustomPrefixComponent());
            for (BedWarsPlayer player : List.copyOf(players)) {
                player.sendMessage(message);
                player.changeGame(null);

                if (MainConfig.getInstance().node("rewards", "enabled").getBoolean()) {
                    Tasker.build(() -> {
                        if (PlayerStatisticManager.isEnabled()) {
                            var statistic = PlayerStatisticManager.getInstance()
                                    .getStatistic(player);
                            GameImpl.this.dispatchRewardCommands("player-end-game", player, statistic.getScore());
                        } else {
                            GameImpl.this.dispatchRewardCommands("player-end-game", player, 0);
                        }
                    }).delay(40, TaskerTime.TICKS).start();
                }
            }

            if (status == GameStatus.REBUILDING) { // If status is still rebuilding
                rebuild();
            }

            if (isBungeeEnabled()) {
                preServerRestart = true;

                if (!getConnectedPlayers().isEmpty()) {
                    kickAllPlayers();
                }

                Tasker.build(() -> {
                    if (MainConfig.getInstance().node("bungee", "serverRestart").getBoolean()) {
                        EventManager.fire(new ServerRestartEventImpl());

                        PlayerMapper.getConsoleSender().tryToDispatchCommand("restart");
                    } else if (MainConfig.getInstance().node("bungee", "serverStop").getBoolean()) {
                        Server.shutdown();
                    }
                }).delay(30, TaskerTime.TICKS).start();
            }
        }
    }

    public void rebuild() {
        if (healthIndicator != null) {
            healthIndicator.destroy();
            healthIndicator = null;
        }
        if (experimentalBoard != null) {
            experimentalBoard.destroy();
            experimentalBoard = null;
        }
        otherVisuals.forEach(visual -> {
            if (visual.shown()) {
                visual.destroy();
            }
        });
        otherVisuals.clear();
        Debug.info(name + ": rebuilding starts");
        teamsInGame.forEach(TeamImpl::destroy);
        teamsInGame.clear();
        activeSpecialItems.clear();
        activeDelays.clear();

        EventManager.fire(new PreRebuildingEventImpl(this));

        for (ItemSpawnerImpl spawner : spawners) {
            spawner.destroy();
        }
        for (GameStoreImpl store : gameStore) {
            var villager = store.kill();
            if (villager != null) {
                EntitiesManagerImpl.getInstance().removeEntityFromGame(villager);
            }
        }

        region.regen();
        // Remove items
        for (EntityBasic e : this.world.getEntities()) {
            if (ArenaUtils.isInArea(e.getLocation(), pos1, pos2)) {
                if (e instanceof EntityItem) {
                    removeEntity(e);
                }
            }
        }

        // Chest clearing
        for (var entry : usedChests.entrySet()) {
            var location = entry.getKey();
            var chunk = location.getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            var block = location.getBlock();
            var contents = entry.getValue();

            var state = block.getBlockState();
            if (state.isPresent() && state.get().holdsInventory()) {
                state.get().getInventory().orElseThrow().setContents(contents);
            }
        }
        usedChests.clear();

        // Clear fake ender chests
        for (var inv : fakeEnderChests.values()) {
            inv.clear();
        }
        fakeEnderChests.clear();

        // Remove remaining entities registered by other plugins
        for (var entity : EntitiesManagerImpl.getInstance().getEntities(this)) {
            var chunk = entity.getEntity().getLocation().getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            entity.getEntity().remove();
            EntitiesManagerImpl.getInstance().removeEntityFromGame(entity);
        }

        UpgradeRegistry.clearAll(this);

        EventManager.fire(new PostRebuildingEventImpl(this));

        this.status = this.afterRebuild;
        this.countdown = -1;
        updateSigns();
        cancelTask();
        Debug.info(name + ": rebuilding ends");

    }

    public boolean processRecord(TeamImpl t, int wonTime) {
        var record = RecordSave.getInstance().getRecord(this.getName());
        if (record.map(RecordSave.Record::getTime).orElse(Integer.MAX_VALUE) > wonTime) {
            RecordSave.getInstance().saveRecord(RecordSave.Record.builder()
                    .game(this.getName())
                    .time(wonTime)
                    .team(t.getName())
                    .winners(t.getPlayers().stream().map(SenderWrapper::getName).collect(Collectors.toList()))
                    .build()
            );
            return true;
        }
        return false;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void runTask() {
        if (task != null) {
            if (Tasker.getRunningTasks().containsKey(task.getId())) {
                task.cancel();
            }
            task = null;
        }
        task = Tasker.build(GameImpl.this::run).repeat(1, TaskerTime.SECONDS).start();
    }

    private void cancelTask() {
        if (task != null) {
            if (Tasker.getRunningTasks().containsKey(task.getId())) {
                task.cancel();
            }
            task = null;
        }
    }

    public void selectTeam(BedWarsPlayer playerGameProfile, String displayName) {
        if (status == GameStatus.WAITING) {
            displayName = MiscUtils.stripColor(displayName);
            playerGameProfile.closeInventory();
            for (TeamImpl team : teams) {
                if (displayName.equals(team.getName())) {
                    internalTeamJoin(playerGameProfile, team, false);
                    break;
                }
            }
        }
    }

    public int getTimeLeft() {
        return this.countdown;
    }

    public String getFormattedTimeLeft() {
        return getFormattedTimeLeft(this.countdown);
    }

    public String getFormattedTimeLeft(int countdown) {
        int min;
        int sec;
        String minStr;
        String secStr;

        min = (int) Math.floor(countdown / 60.0);
        sec = countdown % 60;

        minStr = (min < 10) ? "0" + min : String.valueOf(min);
        secStr = (sec < 10) ? "0" + sec : String.valueOf(sec);

        return minStr + ":" + secStr;
    }

    public void updateSigns() {
        final var config = MainConfig.getInstance();
        final var gameSigns = BedWarsSignService.getInstance().getSignsForKey(this.name);

        if (gameSigns.isEmpty()) {
            return;
        }

        String[] statusLine;
        String[] playersLine;
        BlockTypeHolder blockBehindMaterial;
        switch (status) {
            case REBUILDING:
                statusLine = LangKeys.SIGN_STATUS_REBUILDING_STATUS;
                playersLine = LangKeys.SIGN_STATUS_REBUILDING_PLAYERS;
                blockBehindMaterial = MiscUtils.getBlockTypeFromString(config.node("sign", "block-behind", "rebuilding").getString(), "BROWN_STAINED_GLASS");
                break;
            case RUNNING:
            case GAME_END_CELEBRATING:
                statusLine = LangKeys.SIGN_STATUS_RUNNING_STATUS;
                playersLine = LangKeys.SIGN_STATUS_RUNNING_PLAYERS;
                blockBehindMaterial = MiscUtils.getBlockTypeFromString(config.node("sign", "block-behind", "in-game").getString(), "GREEN_STAINED_GLASS");
                break;
            case WAITING:
                statusLine = LangKeys.SIGN_STATUS_WAITING_STATUS;
                playersLine = LangKeys.SIGN_STATUS_WAITING_PLAYERS;
                blockBehindMaterial = MiscUtils.getBlockTypeFromString(config.node("sign", "block-behind", "waiting").getString(), "ORANGE_STAINED_GLASS");
                break;
            case DISABLED:
            default:
                statusLine = LangKeys.SIGN_STATUS_DISABLED_STATUS;
                playersLine = LangKeys.SIGN_STATUS_DISABLED_PLAYERS;
                blockBehindMaterial = MiscUtils.getBlockTypeFromString(config.node("sign", "block-behind", "game-disabled").getString(), "RED_STAINED_GLASS");
                break;
        }

        var statusMessage = Message.of(statusLine);
        var playerMessage = Message.of(playersLine)
                .placeholder("players", players.size())
                .placeholder("maxplayers", calculatedMaxPlayers);

        final var texts = MainConfig.getInstance().node("sign", "lines").childrenList().stream()
                .map(ConfigurationNode::getString)
                .map(s -> Objects.requireNonNullElse(s, "")
                        .replace("%arena%", this.displayName != null && !this.displayName.isBlank() ? Component.fromMiniMessage(this.displayName).toLegacy() : this.getName())
                        .replace("%status%", statusMessage.asComponent().toLegacy())
                        .replace("%players%", playerMessage.asComponent().toLegacy()))
                .collect(Collectors.toList());

        final var finalBlockBehindMaterial = blockBehindMaterial;
        for (var signBlock : gameSigns) {
            signBlock.getLocation().asOptional(LocationHolder.class)
                    .ifPresent(location -> {
                        if (location.getChunk().isLoaded()) {
                            var blockState = location.getBlock().getBlockState();
                            if (blockState.isPresent() && blockState.get() instanceof SignHolder) {
                                var sign = (SignHolder) blockState.get();
                                for (int i = 0; i < texts.size() && i < 4; i++) {
                                    sign.line(i, Component.fromLegacy(texts.get(i)));
                                }
                                sign.updateBlock();
                            }

                            if (config.node("sign", "block-behind", "enabled").getBoolean(false)) {
                                final var optionalBlock = SignUtils.getBlockBehindSign(signBlock);
                                if (optionalBlock.isPresent()) {
                                    final var glassBlock = optionalBlock.get();
                                    glassBlock.setType(finalBlockBehindMaterial);
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void selectPlayerTeam(BWPlayer player, Team team) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        var bwPlayer = (BedWarsPlayer) player;
        if (!bwPlayer.isInGame() || bwPlayer.getGame() != this) {
            return;
        }

        selectTeam(bwPlayer, team.getName());
    }

    @Override
    public WorldHolder getGameWorld() {
        return world;
    }

    @Override
    public LocationHolder getSpectatorSpawn() {
        return specSpawn;
    }

    @Override
    public int countConnectedPlayers() {
        return players.size();
    }

    @Override
    public List<BedWarsPlayer> getConnectedPlayers() {
        return List.copyOf(players);
    }

    @Override
    public List<TeamImpl> getAvailableTeams() {
        return List.copyOf(teams);
    }

    @Override
    public List<TeamImpl> getActiveTeams() {
        return List.copyOf(teamsInGame);
    }

    @Override
    public TeamImpl getTeamOfPlayer(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        var bwPlayer = (BedWarsPlayer) player;
        if (!bwPlayer.isInGame()) {
            return null;
        }
        return getPlayerTeam(bwPlayer);
    }

    @Override
    public boolean isLocationInArena(Object location) {
        if (location == null) {
            return false;
        }
        return ArenaUtils.isInArea(LocationMapper.wrapLocation(location), pos1, pos2);
    }

    public boolean isLocationInArena(LocationHolder location) {
        return ArenaUtils.isInArea(location, pos1, pos2);
    }

    @Override
    public WorldHolder getLobbyWorld() {
        if (lobbySpawn == null) return null;
        return lobbySpawn.getWorld();
    }

    @Override
    public int getLobbyCountdown() {
        return pauseCountdown;
    }

    @Override
    public TeamImpl getTeamOfChest(Object location) {
        if (location == null) {
            return null;
        }
        return getTeamOfChest(LocationMapper.wrapLocation(location));
    }

    public TeamImpl getTeamOfChest(LocationHolder location) {
        for (var team : teamsInGame) {
            if (team.isTeamChestRegistered(location)) {
                return team;
            }
        }
        return null;
    }

    public void addChestForFutureClear(LocationHolder loc, Container inventory) {
        if (!usedChests.containsKey(loc)) {
            var contents = inventory.getContents();
            var clone = new Item[contents.length];
            for (int i = 0; i < contents.length; i++) {
                var stack = contents[i];
                if (stack != null)
                    clone[i] = stack.clone();
            }
            usedChests.put(loc, clone);
        }
    }

    @Override
    public int getMaxPlayers() {
        return calculatedMaxPlayers;
    }

    @Override
    public int countGameStores() {
        return gameStore.size();
    }

    @Override
    public int countAvailableTeams() {
        return teams.size();
    }

    @Override
    public int countActiveTeams() {
        return teamsInGame.size();
    }

    @Override
    public boolean isPlayerInAnyTeam(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return getTeamOfPlayer((BedWarsPlayer) player) != null;
    }

    @Override
    public boolean isTeamActive(Team team) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return teamsInGame.contains(team);
    }

    @Override
    public boolean isPlayerInTeam(BWPlayer player, Team team) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return getTeamOfPlayer((BedWarsPlayer) player) == team;
    }

    @Override
    public int countTeamChests() {
        int total = 0;
        for (TeamImpl team : teamsInGame) {
            total += team.countTeamChests();
        }
        return total;
    }

    @Override
    public List<SpecialItem> getActiveSpecialItems() {
        return List.copyOf(activeSpecialItems);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem> List<I> getActiveSpecialItems(Class<I> type) {
        return activeSpecialItems.stream()
                .filter(type::isInstance)
                .map(specialItem -> (I) specialItem)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialItem> getActiveSpecialItemsOfTeam(Team team) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return activeSpecialItems.stream()
                .filter(item -> item.getTeam().equals(team))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem> List<I> getActiveSpecialItemsOfTeam(Team team, Class<I> type) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return activeSpecialItems.stream()
                .filter(item -> type.isInstance(item) && item.getTeam().equals(team))
                .map(item -> (I) item)
                .collect(Collectors.toList());
    }

    @Override
    public SpecialItem getFirstActiveSpecialItemOfTeam(Team team) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return activeSpecialItems.stream()
                .filter(item -> item.getTeam().equals(team))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem> I getFirstActiveSpecialItemOfTeam(Team team, Class<I> type) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return (I) activeSpecialItems.stream()
                .filter(item -> item.getTeam().equals(team) && type.isInstance(item))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<SpecialItem> getActiveSpecialItemsOfPlayer(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return activeSpecialItems.stream()
                .filter(item -> item.getPlayer().equals(player))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem> List<I> getActiveSpecialItemsOfPlayer(BWPlayer player, Class<I> type) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return activeSpecialItems.stream()
                .filter(item -> item.getPlayer().equals(player) && type.isInstance(item))
                .map(item -> (I) item)
                .collect(Collectors.toList());
    }

    @Override
    public SpecialItem getFirstActiveSpecialItemOfPlayer(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return activeSpecialItems.stream()
                .filter(item -> item.getPlayer().equals(player))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem> I getFirstActiveSpecialItemOfPlayer(BWPlayer player, Class<I> type) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return (I) activeSpecialItems.stream()
                .filter(item -> item.getPlayer().equals(player) && type.isInstance(item))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void registerSpecialItem(SpecialItem item) {
        if (!activeSpecialItems.contains(item)) {
            activeSpecialItems.add(item);
        }
    }

    @Override
    public void unregisterSpecialItem(SpecialItem item) {
        activeSpecialItems.remove(item);
    }

    @Override
    public boolean isRegisteredSpecialItem(SpecialItem item) {
        return activeSpecialItems.contains(item);
    }

    @Override
    public List<DelayFactory> getActiveDelays() {
        return List.copyOf(activeDelays);
    }

    @Override
    public List<DelayFactory> getActiveDelaysOfPlayer(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return activeDelays.stream()
                .filter(delayFactory -> delayFactory.getPlayer().equals(player))
                .collect(Collectors.toList());
    }

    @Override
    public DelayFactory getActiveDelay(BWPlayer player, Class<? extends SpecialItem> specialItem) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return activeDelays.stream()
                .filter(delayFactory -> delayFactory.getPlayer().equals(player) && specialItem.isInstance(delayFactory.getSpecialItem()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void registerDelay(DelayFactory delayFactory) {
        if (!activeDelays.contains(delayFactory)) {
            activeDelays.add(delayFactory);
        }
    }

    @Override
    public void unregisterDelay(DelayFactory delayFactory) {
        activeDelays.remove(delayFactory);
    }

    @Override
    public boolean isDelayActive(BWPlayer player, Class<? extends SpecialItem> specialItem) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return activeDelays.stream()
                .filter(delayFactory -> delayFactory.getPlayer().equals(player) && specialItem.isInstance(delayFactory.getSpecialItem()))
                .findFirst()
                .map(DelayFactory::isDelayActive)
                .orElse(false);
    }

    @Override
    @Nullable
    public WeatherHolder getArenaWeather() {
        return arenaWeather;
    }

    public void setArenaWeather(WeatherHolder arenaWeather) {
        this.arenaWeather = arenaWeather;
    }

    @Override
    public List<ItemSpawnerImpl> getItemSpawners() {
        return List.copyOf(spawners);
    }

    public void dispatchRewardCommands(String type, PlayerWrapper player, int score) {
        if (!MainConfig.getInstance().node("rewards", "enabled").getBoolean()) {
            return;
        }

        MainConfig.getInstance().node("rewards", type).childrenList()
                .stream()
                .map(ConfigurationNode::getString)
                .filter(Objects::nonNull)
                .map(s -> s
                        .replace("{player}", player.getName())
                        .replace("{score}", Integer.toString(score))
                )
                .map(s -> s.startsWith("/") ? s.substring(1) : s)
                .forEach(player::tryToDispatchCommand);
    }

    @Override
    public void selectPlayerRandomTeam(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        joinRandomTeam((BedWarsPlayer) player);
    }

    @Override
    public StatusBar getStatusBar() {
        return statusbar;
    }

    public void kickAllPlayers() {
        for (var player : getConnectedPlayers()) {
            leaveFromGame(player);
        }
    }

    @Override
    public boolean getBungeeEnabled() {
        return MainConfig.getInstance().node("bungee", "enabled").getBoolean();
    }

    @Override
    public boolean isEntityShop(Object entity) {
        for (var store : gameStore) {
            if (EntityMapper.wrapEntity(entity).map(e -> e.equals(store.getEntity())).orElse(false)) {
                return true;
            }
        }
        return false;
    }

    public RespawnProtection addProtectedPlayer(BedWarsPlayer player) {
        int time = configurationContainer.getOrDefault(GameConfigurationContainer.RESPAWN_PROTECTION_TIME, 10);

        var respawnProtection = new RespawnProtection(this, player, time);
        respawnProtectionMap.put(player, respawnProtection);

        return respawnProtection;
    }

    public void removeProtectedPlayer(BedWarsPlayer player) {
        var respawnProtection = respawnProtectionMap.get(player);
        if (respawnProtection == null) {
            return;
        }

        try {
            respawnProtection.getTask().cancel();
        } catch (Exception ignored) {
        }

        respawnProtectionMap.remove(player);
    }

    @Override
    public boolean isProtectionActive(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return (respawnProtectionMap.containsKey(player));
    }

    public List<BedWarsPlayer> getPlayersWithoutVIP() {
        List<BedWarsPlayer> gamePlayerList = new ArrayList<>(this.players);
        gamePlayerList.removeIf(BedWarsPlayer::canJoinFullGame);

        return gamePlayerList;
    }

    public Container getFakeEnderChest(BedWarsPlayer player) {
        if (!fakeEnderChests.containsKey(player)) {
            fakeEnderChests.put(player, InventoryTypeHolder.of("ender_chest").createContainer().orElseThrow());
        }
        return fakeEnderChests.get(player);
    }

    @Override
    public int getPostGameWaiting() {
        return this.postGameWaiting;
    }

    public void setPostGameWaiting(int time) {
        this.postGameWaiting = time;
    }

    @Override
    public Component getCustomPrefixComponent() {
        return Component.fromMiniMessage(configurationContainer.getOrDefault(GameConfigurationContainer.PREFIX, "[BW]"));
    }

    @Override
    public Component getDisplayNameComponent() {
        if (this.displayName != null && !this.displayName.isBlank()) {
            return Component.fromMiniMessage(this.displayName);
        } else {
            return Component.text(this.name);
        }
    }

    @Override
    public @Nullable LocationHolder getLobbyPos1() {
        return lobbyPos1;
    }

    @Override
    public @Nullable LocationHolder getLobbyPos2() {
        return lobbyPos2;
    }

    @Override
    public boolean invalidateTarget(Team team) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return internalProcessInvalidation((TeamImpl) team, team.getTarget(), null, TargetInvalidationReason.PLUGIN);
    }

    public void setLobbyPos1(LocationHolder pos1) {
        lobbyPos1 = pos1;
    }

    public void setLobbyPos2(LocationHolder pos2) {
        lobbyPos2 = pos2;
    }

    @Override
    public boolean isInEditMode() {
        return AdminCommand.gc.containsKey(name);
    }

    public void showOtherVisual(Visual<?> visual) {
        players.forEach(visual::addViewer);
        otherVisuals.add(visual);
    }

    public void removeOtherVisual(Visual<?> visual) {
        visual.destroy();
        otherVisuals.remove(visual);
    }

    public void setGameVariant(@NotNull VariantImpl gameVariant) {
        this.gameVariant = gameVariant;
        this.configurationContainer.setParentContainer(this.gameVariant.getConfigurationContainer());
    }
}
