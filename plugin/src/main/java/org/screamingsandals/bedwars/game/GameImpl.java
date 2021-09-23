package org.screamingsandals.bedwars.game;

import com.onarandombox.MultiverseCore.api.Core;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.ArenaTime;
import org.screamingsandals.bedwars.api.boss.StatusBar;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.game.ItemSpawner;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.api.upgrades.UpgradeRegistry;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.api.utils.DelayFactory;
import org.screamingsandals.bedwars.boss.BossBarImpl;
import org.screamingsandals.bedwars.boss.XPBarImpl;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.commands.StatsCommand;
import org.screamingsandals.bedwars.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.RecordSave;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.events.*;
import org.screamingsandals.bedwars.holograms.StatisticsHolograms;
import org.screamingsandals.bedwars.inventories.TeamSelectorInventory;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.listener.Player116ListenerUtils;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.region.BWRegion;
import org.screamingsandals.bedwars.region.FlatteningRegion;
import org.screamingsandals.bedwars.region.LegacyRegion;
import org.screamingsandals.bedwars.scoreboard.ScreamingScoreboard;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.tab.TabManager;
import org.screamingsandals.bedwars.utils.*;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.entity.*;
import org.screamingsandals.lib.entity.type.EntityTypeHolder;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.event.player.SPlayerBlockBreakEvent;
import org.screamingsandals.lib.healthindicator.HealthIndicator;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.lang.Message;;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.npc.NPC;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.player.SenderWrapper;
import org.screamingsandals.lib.player.gamemode.GameModeHolder;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.adventure.wrapper.BossBarColorWrapper;
import org.screamingsandals.lib.utils.adventure.wrapper.ComponentWrapper;
import org.screamingsandals.lib.visuals.Visual;
import org.screamingsandals.lib.block.BlockHolder;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.LocationMapper;
import org.screamingsandals.lib.world.WorldHolder;
import org.screamingsandals.lib.world.chunk.ChunkHolder;
import org.screamingsandals.lib.block.state.BlockStateHolder;
import org.screamingsandals.lib.world.gamerule.GameRuleHolder;
import org.screamingsandals.lib.world.weather.WeatherHolder;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GameImpl implements Game<BedWarsPlayer, TeamImpl, BlockHolder, WorldHolder, LocationHolder, EntityBasic, ComponentWrapper, GameStoreImpl, ItemSpawnerImpl> {
    public boolean gameStartItem;
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
    private int pauseCountdown;
    private int gameTime;
    private int minPlayers;
    private final List<BedWarsPlayer> players = new ArrayList<>();
    private WorldHolder world;
    private List<GameStoreImpl> gameStore = new ArrayList<>();
    private ArenaTime arenaTime = ArenaTime.WORLD;
    private WeatherHolder arenaWeather = null;
    private BossBar.Color lobbyBossBarColor = null;
    private BossBar.Color gameBossBarColor = null;
    private String customPrefix = null;
    private boolean preServerRestart = false;
    @Getter
    private File file;

    // STATUS
    private GameStatus previousStatus = GameStatus.DISABLED;
    private GameStatus status = GameStatus.DISABLED;
    private GameStatus afterRebuild = GameStatus.WAITING;
    private int countdown = -1, previousCountdown = -1;
    private int calculatedMaxPlayers;
    private TaskerTask task;
    private final List<TeamImpl> teamsInGame = new ArrayList<>();
    private final BWRegion region = BedWarsPlugin.isLegacy() ? new LegacyRegion() : new FlatteningRegion();
    private TeamSelectorInventory teamSelectorInventory;
    private final Scoreboard gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private StatusBar<PlayerWrapper> statusbar;
    private final Map<Location, ItemStack[]> usedChests = new HashMap<>();
    private final List<SpecialItem<?,?,?>> activeSpecialItems = new ArrayList<>();
    private final List<DelayFactory> activeDelays = new ArrayList<>();
    private final Map<BedWarsPlayer, Inventory> fakeEnderChests = new HashMap<>();
    private int postGameWaiting = 3;
    private ScreamingScoreboard experimentalBoard = null;
    private HealthIndicator healthIndicator = null;
    @Getter
    private final List<Visual<?>> otherVisuals = new ArrayList<>();

    @Getter
    private final GameConfigurationContainer configurationContainer = new GameConfigurationContainer();

    private boolean preparing = false;

    private GameImpl() {

    }

    public static GameImpl loadGame(File file) {
        return loadGame(file, true);
    }

    public static GameImpl loadGame(File file, boolean firstAttempt) {
        try {
            if (!file.exists()) {
                return null;
            }

            final var loader = YamlConfigurationLoader.builder()
                    .file(file)
                    .build();

            final ConfigurationNode configMap;
            try {
                configMap = loader.load();
            } catch (ConfigurateException e) {
                e.printStackTrace();
                return null;
            }

            final var game = new GameImpl();
            game.file = file;
            game.name = configMap.node("name").getString();
            game.pauseCountdown = configMap.node("pauseCountdown").getInt();
            game.gameTime = configMap.node("gameTime").getInt();

            var worldName = Objects.requireNonNull(configMap.node("world").getString());
            game.world = LocationMapper.getWorld(worldName).orElse(null);

            if (game.world == null) {
                if (Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")) {
                    PlayerMapper.getConsoleSender().sendMessage(ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.RED + "World " + worldName
                            + " was not found, but we found Multiverse-Core, so we will try to load this world.");

                    Core multiverse = (Core) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
                    if (multiverse != null && multiverse.getMVWorldManager().loadWorld(worldName)) {
                        PlayerMapper.getConsoleSender().sendMessage(ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.GREEN + "World " + worldName
                                + " was succesfully loaded with Multiverse-Core, continue in arena loading.");

                        game.world = LocationMapper.getWorld(worldName).orElseThrow();
                    } else {
                        PlayerMapper.getConsoleSender().sendMessage(ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.RED + "Arena " + game.name
                                + " can't be loaded, because world " + worldName + " is missing!");
                        return null;
                    }
                } else if (firstAttempt) {
                    PlayerMapper.getConsoleSender().sendMessage(
                            ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.YELLOW + "Arena " + game.name + " can't be loaded, because world " + worldName + " is missing! We will try it again after all plugins will be loaded!");
                    Tasker.build(() -> loadGame(file, false)).delay(10L, TaskerTime.TICKS).start();
                    return null;
                } else {
                    PlayerMapper.getConsoleSender().sendMessage(
                            ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.RED + "Arena " + game.name + " can't be loaded, because world " + worldName + " is missing!");
                    return null;
                }
            }

            if (BedWarsPlugin.getVersionNumber() >= 115) {
                game.world.setGameRuleValue(GameRuleHolder.of("doImmediateRespawn"), true); // TODO: remove this
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
            var lobbySpawnWorld = LocationMapper.getWorld(Objects.requireNonNull(spawnWorld)).orElse(null);
            if (lobbySpawnWorld == null) {
                if (Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")) {
                    PlayerMapper.getConsoleSender().sendMessage(NamedTextColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.RED + "World " + spawnWorld
                            + " was not found, but we found Multiverse-Core, so we will try to load this world.");

                    Core multiverse = (Core) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
                    if (multiverse != null && multiverse.getMVWorldManager().loadWorld(spawnWorld)) {
                        PlayerMapper.getConsoleSender().sendMessage(ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.GREEN + "World " + spawnWorld
                                + " was successfully loaded with Multiverse-Core, continue in arena loading.");

                        lobbySpawnWorld = LocationMapper.getWorld(Objects.requireNonNull(spawnWorld)).orElseThrow();
                    } else {
                        PlayerMapper.getConsoleSender().sendMessage(ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.RED + "Arena " + game.name
                                + " can't be loaded, because world " + spawnWorld + " is missing!");
                        return null;
                    }
                } else if (firstAttempt) {
                    Bukkit.getConsoleSender().sendMessage(
                            ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.YELLOW + "Arena " + game.name + " can't be loaded, because world " + worldName + " is missing! We will try it again after all plugins will be loaded!");
                    Tasker.build(() -> loadGame(file, false)).delay(10L, TaskerTime.TICKS).start();
                    return null;
                } else {
                    Bukkit.getConsoleSender().sendMessage(
                            ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.RED + "Arena " + game.name + " can't be loaded, because world " + spawnWorld + " is missing!");
                    return null;
                }
            }

            var lobbyPos1 = configMap.node("lobbyPos1").getString();
            var lobbyPos2 = configMap.node("lobbyPos2").getString();
            if (lobbyPos1 != null && lobbyPos2 != null) {
                game.lobbyPos1 = MiscUtils.readLocationFromString(lobbySpawnWorld, lobbyPos1);
                game.lobbyPos2 = MiscUtils.readLocationFromString(lobbySpawnWorld, lobbyPos2);
            }

            game.lobbySpawn = MiscUtils.readLocationFromString(lobbySpawnWorld, Objects.requireNonNull(configMap.node("lobbySpawn").getString()));
            game.minPlayers = configMap.node("minPlayers").getInt(2);
            configMap.node("teams").childrenMap().forEach((teamN, team) -> {
                var t = new TeamImpl();
                t.setColor(TeamColorImpl.valueOf(MiscUtils.convertColorToNewFormat(team.node("color").getString(), team.node("isNewColor").getBoolean())));
                t.setName(teamN.toString());
                t.setTargetBlock(MiscUtils.readLocationFromString(game.world, Objects.requireNonNull(team.node("bed").getString())));
                t.setMaxPlayers(team.node("maxPlayers").getInt());
                t.setTeamSpawn(MiscUtils.readLocationFromString(game.world, Objects.requireNonNull(team.node("spawn").getString())));
                t.setGame(game);

                game.teams.add(t);
            });
            configMap.node("spawners").childrenList().forEach(spawner -> {
                var spawnerType = spawner.node("type").getString();
                if (spawnerType == null || BedWarsPlugin.getSpawnerType(spawnerType.toLowerCase()) == null) {
                    throw new UnsupportedOperationException("Wrongly configured spawner type!");
                }
                game.spawners.add(new ItemSpawnerImpl(
                        MiscUtils.readLocationFromString(game.world, Objects.requireNonNull(spawner.node("location").getString())),
                        BedWarsPlugin.getSpawnerType(spawnerType.toLowerCase()),
                        spawner.node("customName").getString(),
                        spawner.node("hologramEnabled").getBoolean(true),
                        spawner.node("startLevel").getDouble(1),
                        game.getTeamFromName(spawner.node("team").getString()),
                        spawner.node("maxSpawnedResources").getInt(-1),
                        spawner.node("floatingEnabled").getBoolean(),
                        Hologram.RotationMode.valueOf(spawner.node("rotationMode").getString("Y")),
                        ItemSpawner.HologramType.valueOf(spawner.node("hologramType").getString("DEFAULT"))
                ));
            }
            );
            configMap.node("stores").childrenList().forEach(store -> {
                if (store.isMap()) {
                    game.gameStore.add(new GameStoreImpl(
                            LocationMapper.wrapLocation(MiscUtils.readLocationFromString(game.world, Objects.requireNonNull(store.node("loc").getString()))),
                            store.node("shop").getString(),
                            store.node("parent").getBoolean(true),
                            EntityTypeHolder.of(store.node("type").getString("VILLAGER").toUpperCase()),
                            store.node("name").getString(""),
                            !store.node("name").empty(),
                            store.node("isBaby").getBoolean(),
                            store.node("skin").getString()
                    ));
                } else {
                    game.gameStore.add(new GameStoreImpl(
                            LocationMapper.wrapLocation(MiscUtils.readLocationFromString(game.world, Objects.requireNonNull(store.getString()))),
                            null,
                            true,
                            EntityTypeHolder.of("villager"),
                            "",
                            false,
                            false,
                            null
                    ));
                }
            });

            game.configurationContainer.applyNode(configMap.node("constant"));

            game.arenaTime = ArenaTime.valueOf(configMap.node("arenaTime").getString(ArenaTime.WORLD.name()).toUpperCase());
            game.arenaWeather = loadWeather(configMap.node("arenaWeather").getString("default").toUpperCase());

            game.postGameWaiting = configMap.node("postGameWaiting").getInt(3);
            game.customPrefix = configMap.node("customPrefix").getString();

            try {
                game.lobbyBossBarColor = loadBossBarColor(
                        configMap.node("lobbyBossBarColor").getString("default").toUpperCase());
                game.gameBossBarColor = loadBossBarColor(configMap.node("gameBossBarColor").getString("default").toUpperCase());
            } catch (Throwable t) {
                // We're using 1.8
            }

            game.start();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.GREEN + "Arena " + ChatColor.WHITE + game.name + " (" + file.getName() + ")" + ChatColor.GREEN + " loaded!");
            return game;
        } catch (Throwable throwable) {
            Debug.warn("Something went wrong while loading arena file " + file.getName() + ". Please report this to our Discord or GitHub!", true);
            throwable.printStackTrace();
            return null;
        }
    }

    public void removeEntity(EntityBasic e) {
        if (ArenaUtils.isInArea(e.getLocation(), pos1, pos2)) {
            final ChunkHolder chunk = e.getLocation().getChunk();
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

    public static BossBar.Color loadBossBarColor(String color) {
        try {
            return BossBar.Color.valueOf(color);
        } catch (Exception e) {
            return null;
        }
    }

    public static GameImpl createGame(String name) {
        GameImpl game = new GameImpl();
        game.name = name;
        game.pauseCountdown = 60;
        game.gameTime = 3600;
        game.minPlayers = 2;

        return game;
    }

    public static String bedExistString() {
        return MainConfig.getInstance().node("scoreboard", "bedExists").getString();
    }

    public static String bedLostString() {
        return MainConfig.getInstance().node("scoreboard", "bedLost").getString();
    }

    public static String anchorEmptyString() {
        return MainConfig.getInstance().node("scoreboard", "anchorEmpty").getString();
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
        return (int) this.players.stream().filter(t -> t.isSpectator && getPlayerTeam(t) == null).count();
    }

    public int countSpectating() {
        return (int) this.players.stream().filter(t -> t.isSpectator).count();
    }

    public int countRespawnable() {
        return (int) this.players.stream().filter(t -> getPlayerTeam(t) != null).count();
    }

    public int countAlive() {
        return (int) this.players.stream().filter(t -> !t.isSpectator).count();
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
        return status == GameStatus.RUNNING && region.isBlockAddedDuringGame(loc);
    }

    @Deprecated
    public boolean isBlockAddedDuringGame(Object loc) {
        return status == GameStatus.RUNNING && region.isBlockAddedDuringGame(loc);
    }

    public boolean blockPlace(BedWarsPlayer player, BlockHolder block, BlockStateHolder replaced, Item itemInHand) {
        if (status != GameStatus.RUNNING) {
            return false; // ?
        }
        if (player.isSpectator) {
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
            if (region.isBlockAddedDuringGame(replaced.getLocation())) {
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
        if (player.isSpectator) {
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

        if (region.isBlockAddedDuringGame(block.getLocation())) {
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
                        event.setDropItems(false);
                        player.getPlayerInventory().addItem(ItemFactory.build("ENDER_CHEST").orElse(ItemFactory.getAir()));
                    }
                }
            }

            if (!breakEvent.isDrops()) {
                try {
                    event.setDropItems(false);
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
        }
        if (isTargetBlock(loc)) {
            if (region.isBedBlock(block.getBlockState().orElseThrow())) {
                if (getPlayerTeam(player).getTargetBlock().equals(loc)) {
                    return false;
                }
                bedDestroyed(loc, player, true, false, false);
                region.putOriginalBlock(block.getLocation(), block.getBlockState().orElseThrow());
                if (block.getLocation().equals(loc)) {
                    var neighbor = region.getBedNeighbor(block);
                    region.putOriginalBlock(neighbor.getLocation(), neighbor.getBlockState().orElseThrow());
                } else {
                    region.putOriginalBlock(loc, region.getBedNeighbor(block).getBlockState().orElseThrow());
                }
                try {
                    event.setDropItems(false);
                } catch (Throwable tr) {
                    if (region.isBedHead(block.getBlockState().orElseThrow())) {
                        region.getBedNeighbor(block).setType(BlockTypeHolder.air());
                    } else {
                        block.setType(BlockTypeHolder.air());
                    }
                }
                return true;
            } else if (configurationContainer.getOrDefault(ConfigurationContainer.CAKE_TARGET_BLOCK_EATING, Boolean.class, false) && block.getType().isSameType("cake")) {
                return false; // when CAKES are in eating mode, don't allow to just break it
            } else {
                if (getPlayerTeam(player).getTargetBlock().equals(loc)) {
                    return false;
                }
                bedDestroyed(loc, player, false, block.getType().isSameType("respawn_anchor"), block.getType().isSameType("cake"));
                region.putOriginalBlock(loc, block.getBlockState().orElseThrow());
                try {
                    event.setDropItems(false);
                } catch (Throwable tr) {
                    block.setType(BlockTypeHolder.air());
                }
                return true;
            }
        }
        if (BedWarsPlugin.isBreakableBlock(block.getType())) {
            region.putOriginalBlock(block.getLocation(), block.getBlockState().orElseThrow());
            return true;
        }
        return false;
    }

    public void targetBlockExplode(TeamImpl team) {
        var loc = team.getTargetBlock();
        var block = loc.getBlock();
        if (region.isBedBlock(block.getBlockState().orElseThrow())) {
            if (!region.isBedHead(block.getBlockState().orElseThrow())) {
                loc = region.getBedNeighbor(block).getLocation();
            }
        }
        if (isTargetBlock(loc)) {
            if (region.isBedBlock(block.getBlockState().orElseThrow())) {
                bedDestroyed(loc, null, true, false, false);
                region.putOriginalBlock(block.getLocation(), block.getBlockState().orElseThrow());
                if (block.getLocation().equals(loc)) {
                    var neighbor = region.getBedNeighbor(block);
                    region.putOriginalBlock(neighbor.getLocation(), neighbor.getBlockState().orElseThrow());
                } else {
                    region.putOriginalBlock(loc, region.getBedNeighbor(block).getBlockState().orElseThrow());
                }
                if (region.isBedHead(block.getBlockState().orElseThrow())) {
                    region.getBedNeighbor(block).setType(BlockTypeHolder.air());
                } else {
                    block.setType(BlockTypeHolder.air());
                }
            } else {
                bedDestroyed(loc, null, false, block.getType().isSameType("respawn_anchor"), block.getType().isSameType("cake"));
                region.putOriginalBlock(loc, block.getBlockState().orElseThrow());
                block.setType(BlockTypeHolder.air());
            }
        }
    }

    private boolean isTargetBlock(LocationHolder loc) {
        for (var team : teamsInGame) {
            if (team.isTargetBlockIntact() && team.getTargetBlock().equals(loc)) {
                return true;
            }
        }
        return false;
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

    public void bedDestroyed(LocationHolder loc, PlayerWrapper destroyer, boolean isItBedBlock, boolean isItAnchor, boolean isItCake) {
        if (status == GameStatus.RUNNING) {
            for (var team : teamsInGame) {
                if (team.getTargetBlock().equals(loc)) {
                    Debug.info(name + ": target block of  " + team.getName() + " has been destroyed");
                    team.setTargetBlockIntact(false);
                    updateScoreboard();
                    String coloredDestroyer = "explosion";
                    if (destroyer != null) {
                        coloredDestroyer = getPlayerTeam(PlayerManagerImpl.getInstance().getPlayer(destroyer.getUuid()).orElseThrow()).getColor().chatColor.toString() + destroyer.getDisplayName();
                    }
                    for (BedWarsPlayer player : players) {
                        var message = Message
                                .of(isItBedBlock ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_BED : (isItAnchor ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANCHOR : (isItCake ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_CAKE : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANY)))
                                .placeholder("team", AdventureHelper.toComponent(team.getColor().chatColor + team.getName()))
                                .placeholder("broker", AdventureHelper.toComponent(coloredDestroyer));

                        message.clone()
                                .join(getPlayerTeam(player) == team ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_SUBTITLE_VICTIM : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_SUBTITLE)
                                .times(TitleUtils.defaultTimes())
                                .title(player);


                        var bbdmsEvent = new BedDestroyedMessageSendEventImpl(this, player, destroyer != null ? destroyer.as(BedWarsPlayer.class) : null, team, message);
                        EventManager.fire(bbdmsEvent);
                        if (!bbdmsEvent.isCancelled()) {
                            bbdmsEvent.getMessage().send(player);
                        }

                        SpawnEffects.spawnEffect(this, player, "game-effects.beddestroy");
                        if (getPlayerTeam(player) == team) {
                            // TODO: adventure equivalent
                            Sounds.playSound(player, player.getLocation(),
                                    MainConfig.getInstance().node("sounds", "my_bed_destroyed", "sound").getString(),
                                    Sounds.ENTITY_ENDER_DRAGON_GROWL, (float) MainConfig.getInstance().node("sounds", "my_bed_destroyed", "volume").getDouble(),
                                    (float) MainConfig.getInstance().node("sounds", "my_bed_destroyed", "pitch").getDouble());
                        } else {
                            // TODO: adventure equivalent
                            Sounds.playSound(player, player.getLocation(),
                                    MainConfig.getInstance().node("sounds", "bed_destroyed", "sound").getString(),
                                    Sounds.ENTITY_ENDER_DRAGON_GROWL, (float) MainConfig.getInstance().node("sounds", "bed_destroyed", "volume").getDouble(),
                                    (float) MainConfig.getInstance().node("sounds", "bed_destroyed", "pitch").getDouble());
                        }
                    }

                    if (team.getHologram() != null) {
                        team.getHologram().replaceLine(0, Message.of(isItBedBlock ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROYED_BED : (isItAnchor ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROYED_ANCHOR : (isItCake ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROYED_CAKE : LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROYED_ANY))));
                        team.getPlayers().forEach(team.getHologram()::addViewer);
                    }

                    if (team.getProtectHologram() != null) {
                        team.getProtectHologram().destroy();
                        team.setProtectHologram(null);
                    }

                    var targetBlockDestroyed = new TargetBlockDestroyedEventImpl(this, destroyer != null ? PlayerManagerImpl.getInstance().getPlayer(destroyer.getUuid()).orElseThrow() : null, team);
                    EventManager.fire(targetBlockDestroyed);

                    if (destroyer != null) {
                        if (PlayerStatisticManager.isEnabled()) {
                            var statistic = PlayerStatisticManager.getInstance().getStatistic(PlayerMapper.wrapPlayer(destroyer));
                            statistic.addDestroyedBeds(1);
                            statistic.addScore(MainConfig.getInstance().node("statistics", "scores", "bed-destroy").getInt(25));
                        }
                        BedWarsPlugin.depositPlayer(destroyer, MainConfig.getInstance().node("vault", "reward", "bed-destroy").getInt());

                        dispatchRewardCommands("player-destroy-bed", destroyer,
                                MainConfig.getInstance().node("statistics", "scores", "bed-destroy").getInt(25));
                    }
                }
            }
        }
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

        if (arenaTime.time >= 0) {
            gamePlayer.as(Player.class).setPlayerTime(arenaTime.time, false); // TODO: remove transformation
        }

        if (arenaWeather != null) {
            gamePlayer.setPlayerWeather(arenaWeather); // TODO: remove transformation
        }

        if (TabManager.isEnabled()) {
            players.forEach(TabManager.getInstance()::modifyForPlayer);
        }

        if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-foreign-players").getBoolean()) {
            Bukkit.getOnlinePlayers().stream().filter(p -> PlayerManagerImpl.getInstance().getGameOfPlayer(p.getUniqueId()).orElse(null) != this).forEach(gamePlayer::hidePlayer);
            players.forEach(p -> p.showPlayer(gamePlayer.as(Player.class)));  // TODO: remove transformation
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

                if (configurationContainer.getOrDefault(ConfigurationContainer.JOIN_RANDOM_TEAM_ON_JOIN, Boolean.class, false)) {
                    joinRandomTeam(gamePlayer);
                }

                if (configurationContainer.getOrDefault(ConfigurationContainer.COMPASS, Boolean.class, false)) {
                    int compassPosition = MainConfig.getInstance().node("hotbar", "selector").getInt(0);
                    if (compassPosition >= 0 && compassPosition <= 8) {
                        var compass = MainConfig.getInstance().readDefinedItem("jointeam", "COMPASS");
                        compass.setDisplayName(Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_COMPASS_SELECTOR_TEAM).asComponent(gamePlayer));
                        gamePlayer.getPlayerInventory().setItem(compassPosition, compass);
                    }
                }

                int leavePosition = MainConfig.getInstance().node("hotbar", "leave").getInt(8);
                if (leavePosition >= 0 && leavePosition <= 8) {
                    var leave = MainConfig.getInstance().readDefinedItem("leavegame", "SLIME_BALL");
                    leave.setDisplayName(Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_LEAVE_FROM_GAME_ITEM).asComponent(gamePlayer));
                    gamePlayer.getPlayerInventory().setItem(leavePosition, leave);
                }

                if (gamePlayer.hasPermission(BedWarsPermission.START_ITEM_PERMISSION.asPermission())) {
                    int vipPosition = MainConfig.getInstance().node("hotbar", "start").getInt(1);
                    if (vipPosition >= 0 && vipPosition <= 8) {
                        var startGame = MainConfig.getInstance().readDefinedItem("startgame", "DIAMOND");
                        startGame.setDisplayName(Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_START_GAME_ITEM).asComponent(gamePlayer));
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
                players.stream().filter(p -> p.isSpectator && !isPlayerInAnyTeam(p)).forEach(p -> gamePlayer.hidePlayer(p.as(Player.class)));  // TODO: remove transformation
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

        if (!gamePlayer.isSpectator) {
            if (!preServerRestart) {
                Message.of(LangKeys.IN_GAME_LEAVE)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .placeholder("name", gamePlayer.getDisplayName())
                        .placeholder("players", players.size())
                        .placeholder("maxplayers", calculatedMaxPlayers)
                        .send(players);
            }
        } else {
            if (gamePlayer.as(Player.class).getSpectatorTarget() != null) {
                gamePlayer.as(Player.class).setSpectatorTarget(null); // TODO: remove transformation
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
            players.forEach(p -> p.hidePlayer(gamePlayer.as(Player.class))); // TODO: remove transformation
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
        gamePlayer.as(Player.class).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard()); // TODO: ScreamingLib equivalent

        if (MainConfig.getInstance().node("mainlobby", "enabled").getBoolean()
                && !MainConfig.getInstance().node("bungee", "enabled").getBoolean()) {
            try {
                LocationHolder mainLobbyLocation = MiscUtils.readLocationFromString(
                        LocationMapper.getWorld(MainConfig.getInstance().node("mainlobby", "world").getString()).orElseThrow(),
                        Objects.requireNonNull(MainConfig.getInstance().node("mainlobby", "location").getString())
                );
                gamePlayer.teleport(mainLobbyLocation);
                gamePlayer.mainLobbyUsed = true;
            } catch (Throwable t) {
                Bukkit.getLogger().severe("You didn't setup properly the mainlobby! Do it via commands not directly in config.yml");
            }
        }

        if (status == GameStatus.RUNNING || status == GameStatus.WAITING) {
            var team = getPlayerTeam(gamePlayer);
            if (team != null) {
                team.getPlayers().remove(gamePlayer);
                if (status == GameStatus.WAITING) {
                    team.getScoreboardTeam().removeEntry(gamePlayer.getName());
                    if (team.getPlayers().isEmpty()) {
                        teamsInGame.remove(team);
                        team.getScoreboardTeam().unregister();
                    }
                } else {
                    updateScoreboard();
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

            if (gameScoreboard.getObjective("display") != null) {
                gameScoreboard.getObjective("display").unregister();
            }
            if (gameScoreboard.getObjective("lobby") != null) {
                gameScoreboard.getObjective("lobby").unregister();
            }
            gameScoreboard.clearSlot(DisplaySlot.SIDEBAR);

            for (var team : teamsInGame) {
                team.getScoreboardTeam().unregister();
            }
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
        if (!dir.exists())
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        if (file == null) {
            do {
                file = new File(dir, UUID.randomUUID() + ".yml");
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
        var loader = YamlConfigurationLoader.builder()
                .file(file)
                .build();

        var configMap = loader.createNode();
        configMap.node("name").set(name);
        configMap.node("pauseCountdown").set(pauseCountdown);
        configMap.node("gameTime").set(gameTime);
        configMap.node("world").set(world.getName());
        configMap.node("pos1").set(MiscUtils.setLocationToString(pos1));
        configMap.node("pos2").set(MiscUtils.setLocationToString(pos2));
        configMap.node("specSpawn").set(MiscUtils.setLocationToString(specSpawn));
        configMap.node("lobbySpawn").set(MiscUtils.setLocationToString(lobbySpawn));
        if (lobbyPos1 != null)
            configMap.node("lobbyPos1", MiscUtils.setLocationToString(lobbyPos1));
        if (lobbyPos2 != null)
            configMap.node("lobbyPos2", MiscUtils.setLocationToString(lobbyPos2));
        configMap.node("lobbySpawnWorld").set(lobbySpawn.getWorld().getName());
        configMap.node("minPlayers").set(minPlayers);
        configMap.node("postGameWaiting").set(postGameWaiting);
        configMap.node("customPrefix").set(customPrefix);
        if (!teams.isEmpty()) {
            for (var t : teams) {
                var teamNode = configMap.node("teams", t.getName());
                teamNode.node("isNewColor").set(true);
                teamNode.node("color").set(t.getColor().name());
                teamNode.node("maxPlayers").set(t.getMaxPlayers());
                teamNode.node("bed").set(MiscUtils.setLocationToString(t.getTargetBlock()));
                teamNode.node("spawn").set(MiscUtils.setLocationToString(t.getTeamSpawn()));
            }
        }
        for (var spawner : spawners) {
            var spawnerNode = configMap.node("spawners").appendListNode();
            spawnerNode.node("location").set(MiscUtils.setLocationToString(spawner.getLocation()));
            spawnerNode.node("type").set(spawner.getItemSpawnerType().getConfigKey());
            spawnerNode.node("customName").set(spawner.getCustomName());
            spawnerNode.node("startLevel").set(spawner.getBaseAmountPerSpawn());
            spawnerNode.node("hologramEnabled").set(spawner.isHologramEnabled());
            spawnerNode.node("team").set(Optional.ofNullable(spawner.getTeam()).map(org.screamingsandals.bedwars.api.Team::getName).orElse(null));
            spawnerNode.node("maxSpawnedResources").set(spawner.getMaxSpawnedResources());
            spawnerNode.node("floatingEnabled").set(spawner.isFloatingBlockEnabled());
            spawnerNode.node("rotationMode").set(spawner.getRotationMode());
            spawnerNode.node("hologramType").set(spawner.getHologramType());
        }
        for (var store : gameStore) {
            var storeNode = configMap.node("stores").appendListNode();
            storeNode.node("loc").set(MiscUtils.setLocationToString(store.getStoreLocation()));
            storeNode.node("shop").set(store.getShopFile());
            storeNode.node("parent").set(store.isUseParent() ? "true" : "false");
            storeNode.node("type").set(store.getEntityType().getPlatformName());
            if (store.isEnabledCustomName()) {
                storeNode.node("name").set(store.getShopCustomName());
            }
            storeNode.node("isBaby").set(store.isBaby() ? "true" : "false");
            storeNode.node("skin").set(store.getSkinName());
        }

        configMap.node("constant").from(configurationContainer.getSaved());

        configMap.node("arenaTime").set(arenaTime.name());
        configMap.node("arenaWeather").set(arenaWeather == null ? "default" : arenaWeather.getPlatformName());

        try {
            configMap.node("lobbyBossBarColor").set(lobbyBossBarColor == null ? "default" : lobbyBossBarColor.name());
            configMap.node("gameBossBarColor").set(gameBossBarColor == null ? "default" : gameBossBarColor.name());
        } catch (Throwable t) {
            // We're using 1.8
        }

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
        List<BedWarsPlayer> clonedPlayers = (List<BedWarsPlayer>) ((ArrayList<BedWarsPlayer>) players).clone();
        for (BedWarsPlayer p : clonedPlayers)
            p.changeGame(null);
        if (status != GameStatus.REBUILDING) {
            status = GameStatus.DISABLED;
            updateSigns();
        } else {
            afterRebuild = GameStatus.DISABLED;
        }
    }

    @Override
    public void joinToGame(BedWarsPlayer player) {
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
                BungeeUtils.sendPlayerBungeeMessage(player, AdventureHelper.toLegacy(Message
                                .of(LangKeys.IN_GAME_ERRORS_GAME_IS_REBUILDING)
                                .prefixOrDefault(getCustomPrefixComponent())
                                .placeholder("arena", this.name)
                                .asComponent(player)
                        ));
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
                && !configurationContainer.getOrDefault(ConfigurationContainer.SPECTATOR_JOIN, Boolean.class, false)) {
            if (isBungeeEnabled()) {
                BungeeUtils.movePlayerToBungeeServer(player, false);
                BungeeUtils.sendPlayerBungeeMessage(player, AdventureHelper.toLegacy(
                        Message
                                .of(LangKeys.IN_GAME_ERRORS_GAME_ALREADY_RUNNING)
                                .placeholder("arena", this.name)
                                .prefixOrDefault(getCustomPrefixComponent())
                                .asComponent(player)
                ));
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
                    BungeeUtils.sendPlayerBungeeMessage(kickPlayer, AdventureHelper.toLegacy(
                            Message
                                    .of(LangKeys.IN_GAME_ERRORS_GAME_KICKED_BY_VIP)
                                    .placeholder("arena", this.name)
                                    .prefixOrDefault(getCustomPrefixComponent())
                                    .asComponent(kickPlayer)
                    ));
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
                    Tasker.build(() -> BungeeUtils.sendPlayerBungeeMessage(player, AdventureHelper.toLegacy(
                            Message
                                    .of(LangKeys.IN_GAME_ERRORS_GAME_IS_FULL)
                                    .placeholder("arena", GameImpl.this.name)
                                    .prefixOrDefault(getCustomPrefixComponent())
                                    .asComponent(player)
                    ))).delay(5, TaskerTime.TICKS).start();
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

        player.changeGame(this);
    }

    @Override
    public void leaveFromGame(BedWarsPlayer player) {
        if (status == GameStatus.DISABLED) {
            return;
        }
        if (player.isInGame() && player.getGame() == this) {
            player.changeGame(null);
            if (status == GameStatus.RUNNING || status == GameStatus.GAME_END_CELEBRATING) {
                updateScoreboard();
            }
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

    public TeamImpl getTeamWithLowestPlayers() {
        TeamImpl lowest = null;

        for (var team : teamsInGame) {
            if (lowest == null) {
                lowest = team;
            }

            if (lowest.getPlayers().size() > team.getPlayers().size()) {
                lowest = team;
            }
        }

        return lowest;
    }

    public List<BedWarsPlayer> getPlayersInTeam(TeamImpl team) {
        return team.getPlayers();
    }

    private void internalTeamJoin(BedWarsPlayer player, TeamImpl teamForJoin) {
        TeamImpl current = null;
        if (teamsInGame.contains(teamForJoin)) {
            current = teamForJoin;
        }

        var cur = getPlayerTeam(player);
        var event = new PlayerJoinTeamEventImpl(this, player, current, cur);
        EventManager.fire(event);

        if (event.isCancelled()) {
            return;
        }

        if (current == null) {
            current = teamForJoin;
            org.bukkit.scoreboard.Team scoreboardTeam = gameScoreboard.getTeam(teamForJoin.getName());
            if (scoreboardTeam == null) {
                scoreboardTeam = gameScoreboard.registerNewTeam(teamForJoin.getName());
            }
            if (!BedWarsPlugin.isLegacy()) {
                scoreboardTeam.setColor(teamForJoin.getColor().chatColor);
            } else {
                scoreboardTeam.setPrefix(teamForJoin.getColor().chatColor.toString());
            }
            scoreboardTeam.setAllowFriendlyFire(configurationContainer.getOrDefault(ConfigurationContainer.FRIENDLY_FIRE, Boolean.class, false));
            current.setScoreboardTeam(scoreboardTeam);
        }

        if (cur == current) {
            Message
                    .of(LangKeys.IN_GAME_TEAM_SELECTION_ALREADY_SELECTED)
                    .prefixOrDefault(getCustomPrefixComponent())
                    .placeholder("team", AdventureHelper.toComponent(teamForJoin.getColor().chatColor + teamForJoin.getName()))
                    .placeholder("players", current.countConnectedPlayers())
                    .placeholder("maxplayers", current.getMaxPlayers())
                    .send(player);
            return;
        }
        if (current.countConnectedPlayers() >= current.getMaxPlayers()) {
            if (cur != null) {
                Message
                        .of(LangKeys.IN_GAME_TEAM_SELECTION_FULL_NO_CHANGE)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .placeholder("team", AdventureHelper.toComponent(teamForJoin.getColor().chatColor + teamForJoin.getName()))
                        .placeholder("oldteam", AdventureHelper.toComponent(cur.getColor().chatColor + cur.getName()))
                        .send(player);
            } else {
                Message
                        .of(LangKeys.IN_GAME_TEAM_SELECTION_FULL)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .placeholder("team", AdventureHelper.toComponent(teamForJoin.getColor().chatColor + teamForJoin.getName()))
                        .send(player);
            }
            return;
        }

        if (cur != null) {
            cur.getPlayers().remove(player);
            cur.getScoreboardTeam().removeEntry(player.getName());

            if (cur.getPlayers().isEmpty()) {
                teamsInGame.remove(cur);
                cur.getScoreboardTeam().unregister();
            }
            Debug.info(name + ": player " + player.getName() + " left the team " + cur.getName());
        }

        current.getPlayers().add(player);
        current.getScoreboardTeam().addEntry(player.getName());

        Debug.info(name + ": player " + player.getName() + " joined the team " + current.getName());

        Message
                .of(LangKeys.IN_GAME_TEAM_SELECTION_SELECTED)
                .prefixOrDefault(getCustomPrefixComponent())
                .placeholder("team", AdventureHelper.toComponent(teamForJoin.getColor().chatColor + teamForJoin.getName()))
                .placeholder("players", current.getPlayers().size())
                .placeholder("maxplayers", current.getMaxPlayers())
                .send(player);

        if (configurationContainer.getOrDefault(ConfigurationContainer.ADD_WOOL_TO_INVENTORY_ON_JOIN, Boolean.class, false)) {
            int colorPosition = MainConfig.getInstance().node("hotbar", "color").getInt(1);
            if (colorPosition >= 0 && colorPosition <= 8) {
                var item = ItemFactory.build(teamForJoin.getColor().material1_13 + "_WOOL").orElse(ItemFactory.getAir());
                item.setDisplayName(AdventureHelper.toComponent(teamForJoin.getColor().chatColor + teamForJoin.getName()));
                player.getPlayerInventory().setItem(colorPosition, item);
            }
        }

        if (configurationContainer.getOrDefault(ConfigurationContainer.COLORED_LEATHER_BY_TEAM_IN_LOBBY, Boolean.class, false)) {
            var chestplate = ItemFactory.build("LEATHER_CHESTPLATE").orElse(ItemFactory.getAir());
            chestplate.setColor(teamForJoin.getColor().getLeatherColor());
            player.getPlayerInventory().setChestplate(chestplate);
        }

        if (!teamsInGame.contains(current)) {
            teamsInGame.add(current);
        }

        EventManager.fire(new PlayerJoinedTeamEventImpl(this, player, current, cur));
    }

    public void joinRandomTeam(BedWarsPlayer player) {
        TeamImpl teamForJoin;
        if (teamsInGame.size() < 2) {
            teamForJoin = getFirstTeamThatIsntInGame();
        } else {
            var current = getTeamWithLowestPlayers();
            if (current.getPlayers().size() >= current.getMaxPlayers()) {
                teamForJoin = getFirstTeamThatIsntInGame();
            } else {
                teamForJoin = current;
            }
        }

        if (teamForJoin == null) {
            return;
        }

        internalTeamJoin(player, teamForJoin);
    }

    public LocationHolder makeSpectator(BedWarsPlayer gamePlayer, boolean leaveItem) {
        Debug.info(gamePlayer.getName() + " spawning as spectator");
        Player player = gamePlayer.as(Player.class); // TODO: remove transformation
        gamePlayer.isSpectator = true;
        gamePlayer.teleport(specSpawn, () -> {
            if (!configurationContainer.getOrDefault(ConfigurationContainer.KEEP_INVENTORY, Boolean.class, false) || leaveItem) {
                gamePlayer.invClean(); // temp fix for inventory issues?
            }
            player.setAllowFlight(true);  // TODO: SLib equivalent
            player.setFlying(true);
            gamePlayer.setGameMode(GameModeHolder.of("spectator"));

            if (leaveItem) {
                if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-spectators").getBoolean()) {
                    players.forEach(p -> p.hidePlayer(player));
                }

                int leavePosition = MainConfig.getInstance().node("hotbar", "leave").getInt(8);
                if (leavePosition >= 0 && leavePosition <= 8) {
                    var leave = MainConfig.getInstance().readDefinedItem("leavegame", "SLIME_BALL");
                    leave.setDisplayName(Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_LEAVE_FROM_GAME_ITEM).asComponent(gamePlayer));
                    gamePlayer.getPlayerInventory().setItem(leavePosition, leave);
                }
            }

            if (TabManager.isEnabled()) {
                players.forEach(TabManager.getInstance()::modifyForPlayer);
            }
            healthIndicator.removeTrackedPlayer(gamePlayer);
        }, true);

        return specSpawn;
    }

    public void makePlayerFromSpectator(BedWarsPlayer gamePlayer) {
        Debug.info(gamePlayer.getName() + " changing spectator to regular player");
        Player player = gamePlayer.as(Player.class); // TODO: remove transformation
        var currentTeam = getPlayerTeam(gamePlayer);

        if (gamePlayer.getGame() == this && currentTeam != null) {
            gamePlayer.isSpectator = false;
            if (player.getSpectatorTarget() != null) {
                player.setSpectatorTarget(null);
            }
            gamePlayer.teleport(MiscUtils.findEmptyLocation(currentTeam.getTeamSpawn()), () -> {
                player.setAllowFlight(false); // TODO: SLib equivalent
                player.setFlying(false);
                gamePlayer.setGameMode(GameModeHolder.of("survival"));

                if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-spectators").getBoolean()) {
                    players.forEach(p -> p.showPlayer(player));
                }

                if (MainConfig.getInstance().node("respawn", "protection-enabled").getBoolean(true)) {
                    RespawnProtection respawnProtection = addProtectedPlayer(gamePlayer);
                    respawnProtection.runProtection();
                }

                if (configurationContainer.getOrDefault(ConfigurationContainer.ENABLE_PLAYER_RESPAWN_ITEMS, Boolean.class, false)) {
                    var playerRespawnItems = MainConfig.getInstance().node("player-respawn-items", "items")
                            .childrenList()
                            .stream()
                            .map(ItemFactory::build)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList());
                    if (!playerRespawnItems.isEmpty()) {
                        MiscUtils.giveItemsToPlayer(playerRespawnItems, gamePlayer, currentTeam.getColor());
                    } else {
                        Debug.warn("You have wrongly configured player-respawn-items.items!", true);
                    }
                }
                MiscUtils.giveItemsToPlayer(gamePlayer.getPermaItemsPurchased(), gamePlayer, currentTeam.getColor());

                if (configurationContainer.getOrDefault(ConfigurationContainer.KEEP_ARMOR, Boolean.class, false)) {
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
            var title = AdventureHelper.toLegacy(Message.of(LangKeys.IN_GAME_BOSSBAR_WAITING).asComponent());
            statusbar.setProgress(0);
            statusbar.setVisible(configurationContainer.getOrDefault(ConfigurationContainer.LOBBY_BOSSBAR, Boolean.class, false));
            for (BedWarsPlayer p : players) {
                statusbar.addPlayer(p);
            }
            if (statusbar instanceof BossBarImpl) {
                var bossbar = (BossBarImpl) statusbar;
                bossbar.setMessage(title);
                bossbar.setColor(lobbyBossBarColor != null ? lobbyBossBarColor
                        : MainConfig.getInstance().node("bossbar", "lobby", "color").getString(""));
                bossbar
                        .setStyle(MainConfig.getInstance().node("bossbar", "lobby", "style").getString(""));
            }
            if (teamSelectorInventory == null) {
                teamSelectorInventory = new TeamSelectorInventory(this);
            }

            if (experimentalBoard == null && MainConfig.getInstance().node("experimental", "new-scoreboard-system", "enabled").getBoolean(false)) {
                experimentalBoard = new ScreamingScoreboard(this);
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

            if (players.size() >= getMinPlayers()
                    && (configurationContainer.getOrDefault(ConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY, Boolean.class, false) || teamsInGame.size() > 1)) {
                if (countdown == 0) {
                    nextCountdown = gameTime;
                    nextStatus = GameStatus.RUNNING;
                } else {
                    nextCountdown--;

                    if (countdown <= 10 && countdown >= 1 && countdown != previousCountdown) {

                        for (BedWarsPlayer player : players) {
                            TitleUtils.send(player, ChatColor.YELLOW + Integer.toString(countdown), "");
                            // TODO: adventure equivalent
                            Sounds.playSound(player.as(Player.class), player.as(Player.class).getLocation(),
                                    MainConfig.getInstance().node("sounds", "countdown", "sound").getString(), Sounds.UI_BUTTON_CLICK,
                                    (float) MainConfig.getInstance().node("sounds", "countdown", "volume").getDouble(),
                                    (float) MainConfig.getInstance().node("sounds", "countdown", "pitch").getDouble());
                        }
                    }
                }
            } else {
                nextCountdown = countdown = pauseCountdown;
            }
            setBossbarProgress(countdown, pauseCountdown);
            updateLobbyScoreboard();
        } else if (status == GameStatus.RUNNING) {
            if (countdown == 0) {
                nextCountdown = postGameWaiting;
                nextStatus = GameStatus.GAME_END_CELEBRATING;
            } else {
                nextCountdown--;
            }
            setBossbarProgress(countdown, gameTime);
            updateScoreboardTimer();
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

                    if (configurationContainer.getOrDefault(ConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY, Boolean.class, false)) {
                        for (BedWarsPlayer player : players) {
                            if (getPlayerTeam(player) == null) {
                                joinRandomTeam(player);
                            }
                        }
                    }

                    statusbar.setProgress(0);
                    statusbar.setVisible(configurationContainer.getOrDefault(ConfigurationContainer.GAME_BOSSBAR, Boolean.class, false));
                    if (statusbar instanceof BossBarImpl) {
                        var bossbar = (BossBarImpl) statusbar;
                        bossbar.setMessage(AdventureHelper.toLegacy(Message.of(LangKeys.IN_GAME_BOSSBAR_RUNNING).asComponent()));
                        bossbar.setColor(gameBossBarColor != null ? gameBossBarColor
                                : MainConfig.getInstance().node("bossbar", "game", "color").getString(""));
                        bossbar.setStyle(
                                MainConfig.getInstance().node("bossbar", "game", "style").getString(""));
                    }
                    if (teamSelectorInventory != null)
                        teamSelectorInventory.destroy();
                    teamSelectorInventory = null;

                    if (gameScoreboard.getObjective("lobby") != null) {
                        gameScoreboard.getObjective("lobby").unregister();
                    }
                    gameScoreboard.clearSlot(DisplaySlot.SIDEBAR);
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
                            makeSpectator(player, true);
                        } else {
                            player.teleport(team.getTeamSpawn(), () -> {
                                player.setGameMode(GameModeHolder.of("survival"));
                                if (configurationContainer.getOrDefault(ConfigurationContainer.ENABLE_GAME_START_ITEMS, Boolean.class, false)) {
                                    var givedGameStartItems = MainConfig.getInstance().node("game-start-items", "items")
                                            .childrenList()
                                            .stream()
                                            .map(ItemFactory::build)
                                            .filter(Optional::isPresent)
                                            .map(Optional::get)
                                            .collect(Collectors.toList());
                                    if (!givedGameStartItems.isEmpty()) {
                                        MiscUtils.giveItemsToPlayer(givedGameStartItems, player, team.getColor());
                                    } else {
                                        Debug.warn("You have wrongly configured game-start-items.items!", true);
                                    }
                                }
                                SpawnEffects.spawnEffect(this, player, "game-effects.start");
                            });
                        }
                        // TODO: adventure equivalent
                        Sounds.playSound(player.as(Player.class), player.as(Player.class).getLocation(),
                                MainConfig.getInstance().node("sounds", "game_start", "sound").getString(),
                                Sounds.ENTITY_PLAYER_LEVELUP,
                                (float) MainConfig.getInstance().node("sounds", "game_start", "volume").getDouble(),
                                (float) MainConfig.getInstance().node("sounds", "game_start", "pitch").getDouble());
                    }

                    if (configurationContainer.getOrDefault(ConfigurationContainer.REMOVE_UNUSED_TARGET_BLOCKS, Boolean.class, false)) {
                        for (TeamImpl team : teams) {
                            TeamImpl ct = null;
                            if (!teamsInGame.contains(team)) {
                                LocationHolder loc = team.getTargetBlock();
                                BlockHolder block = loc.getBlock();
                                if (region.isBedBlock(block.getBlockState().orElseThrow())) {
                                    region.putOriginalBlock(block.getLocation(), block.getBlockState().orElseThrow());
                                    var neighbor = region.getBedNeighbor(block);
                                    region.putOriginalBlock(neighbor.getLocation(), neighbor.getBlockState().orElseThrow());
                                    neighbor.as(Block.class).setType(Material.AIR, false);  // TODO: remove this
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

                    if (BedWarsPlugin.getVersionNumber() >= 115 && !MainConfig.getInstance().node("allow-fake-death").getBoolean()) {
                        world.setGameRuleValue(GameRuleHolder.of("doImmediateRespawn"), true);
                    }
                    preparing = false;

                    var startedEvent = new GameStartedEventImpl(this);
                    EventManager.fire(startedEvent);
                    EventManager.fire(statusE);
                    updateScoreboard();
                    Debug.info(name + ": game prepared");

                    if (configurationContainer.getOrDefault(ConfigurationContainer.HEALTH_INDICATOR, Boolean.class, false)) {
                        healthIndicator = HealthIndicator.of()
                                .symbol(Component.text("\u2665", NamedTextColor.RED))
                                .show()
                                .startUpdateTask(4, TaskerTime.TICKS);
                        players.forEach(healthIndicator::addViewer);
                        players.stream().filter(bedWarsPlayer -> !bedWarsPlayer.isSpectator).forEach(healthIndicator::addTrackedPlayer);
                    }
                }
            }
            // Phase 6.2: If status is same as before
        } else {
            // Phase 6.2.1: On game tick (if not interrupted by a change of status)
            if (status == GameStatus.RUNNING && tick.getNextStatus() == GameStatus.RUNNING) {
                int runningTeams = 0;
                for (var t : teamsInGame) {
                    runningTeams += t.isAlive() ? 1 : 0;
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
                                        .placeholder("team", AdventureHelper.toComponent(t.getColor().chatColor + t.getName()))
                                        .placeholder("time", time);
                                boolean madeRecord = processRecord(t, gameTime - countdown);
                                for (BedWarsPlayer player : players) {
                                    player.sendMessage(message);
                                    if (getPlayerTeam(player) == t) {
                                        Message.of(LangKeys.IN_GAME_END_YOU_WON)
                                                .join(LangKeys.IN_GAME_END_TEAM_WIN)
                                                .placeholder("team", AdventureHelper.toComponent(t.getColor().chatColor + t.getName()))
                                                .placeholder("time", time)
                                                .times(TitleUtils.defaultTimes())
                                                .title(player);
                                        BedWarsPlugin.depositPlayer(player, BedWarsPlugin.getVaultWinReward());

                                        SpawnEffects.spawnEffect(this, player, "game-effects.end");

                                        if (PlayerStatisticManager.isEnabled()) {
                                            var statistic = PlayerStatisticManager.getInstance()
                                                    .getStatistic(player);
                                            statistic.addWins(1);
                                            statistic.addScore(MainConfig.getInstance().node("statistics", "scores", "win").getInt(50));

                                            if (madeRecord) {
                                                statistic.addScore(MainConfig.getInstance().node("statistics", "scores", "record").getInt(100));
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
                                            Tasker.build(() -> {
                                                if (PlayerStatisticManager.isEnabled()) {
                                                    var statistic = PlayerStatisticManager.getInstance()
                                                            .getStatistic(player);
                                                    GameImpl.this.dispatchRewardCommands("player-win", player,
                                                            statistic.getScore());
                                                } else {
                                                    GameImpl.this.dispatchRewardCommands("player-win", player, 0);
                                                }
                                            }).delay((2 + postGameWaiting) * 20L, TaskerTime.TICKS).start();
                                        }
                                    } else {
                                        Message.of(LangKeys.IN_GAME_END_YOU_LOST)
                                                .join(LangKeys.IN_GAME_END_TEAM_WIN)
                                                .placeholder("team", AdventureHelper.toComponent(t.getColor().chatColor + t.getName()))
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

                        Bukkit.getServer()
                                .dispatchCommand(Bukkit.getServer().getConsoleSender(), "restart"); // TODO: replace this
                    } else if (MainConfig.getInstance().node("bungee", "serverStop").getBoolean()) {
                        Bukkit.shutdown(); // TODO: replace this
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
            if (visual.isShown()) {
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
        for (Map.Entry<Location, ItemStack[]> entry : usedChests.entrySet()) {
            Location location = entry.getKey();
            Chunk chunk = location.getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            Block block = location.getBlock();
            ItemStack[] contents = entry.getValue();
            if (block.getState() instanceof InventoryHolder) {
                InventoryHolder chest = (InventoryHolder) block.getState();
                chest.getInventory().setContents(contents);
            }
        }
        usedChests.clear();

        // Clear fake ender chests
        for (Inventory inv : fakeEnderChests.values()) {
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
                    .team(t.getColor().chatColor + t.getName())
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
            displayName = ChatColor.stripColor(displayName);
            playerGameProfile.closeInventory();
            for (TeamImpl team : teams) {
                if (displayName.equals(team.getName())) {
                    internalTeamJoin(playerGameProfile, team);
                    break;
                }
            }
        }
    }

    public void updateScoreboard() {
        if (!configurationContainer.getOrDefault(ConfigurationContainer.GAME_SCOREBOARD, Boolean.class, false)) {
            return;
        }

        if (MainConfig.getInstance().node("experimental", "new-scoreboard-system", "enabled").getBoolean(false)) {
            return;
        }

        Objective obj = this.gameScoreboard.getObjective("display");
        if (obj == null) {
            obj = this.gameScoreboard.registerNewObjective("display", "dummy");
        }

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(this.formatScoreboardTitle());

        for (var team : teamsInGame) {
            this.gameScoreboard.resetScores(this.formatScoreboardTeam(team, false, false));
            this.gameScoreboard.resetScores(this.formatScoreboardTeam(team, false, true));
            this.gameScoreboard.resetScores(this.formatScoreboardTeam(team, true, false));

            Score score = obj.getScore(this.formatScoreboardTeam(team, !team.isTargetBlockIntact(), team.isTargetBlockIntact() && team.getTargetBlock().getBlock().getType().isSameType("respawn_anchor") && Player116ListenerUtils.isAnchorEmpty(team.getTargetBlock().getBlock())));  // TODO: remove transformation
            score.setScore(team.countConnectedPlayers());
        }

        for (BedWarsPlayer player : players) {
            player.as(Player.class).setScoreboard(gameScoreboard); // TODO: SLib equivalent
        }
    }

    private String formatScoreboardTeam(TeamImpl team, boolean destroy, boolean empty) {
        if (team == null) {
            return "";
        }

        return MainConfig.getInstance().node("scoreboard", "teamTitle").getString("%bed%%color%%team%")
                .replace("%color%", team.getColor().chatColor.toString()).replace("%team%", team.getName())
                .replace("%bed%", destroy ? bedLostString() : (empty ? anchorEmptyString() : bedExistString()));
    }

    private void updateScoreboardTimer() {
        if (this.status != GameStatus.RUNNING || !configurationContainer.getOrDefault(ConfigurationContainer.GAME_SCOREBOARD, Boolean.class, false)) {
            return;
        }

        if (MainConfig.getInstance().node("experimental", "new-scoreboard-system", "enabled").getBoolean(false)) {
            return;
        }

        Objective obj = this.gameScoreboard.getObjective("display");
        if (obj == null) {
            obj = this.gameScoreboard.registerNewObjective("display", "dummy");
        }

        obj.setDisplayName(this.formatScoreboardTitle());

        for (BedWarsPlayer player : players) {
            player.as(Player.class).setScoreboard(gameScoreboard); // TODO: SLib equivalent
        }
    }

    public String formatScoreboardTitle() {
        return Objects.requireNonNull(MainConfig.getInstance().node("scoreboard", "title").getString())
                .replace("%game%", this.name)
                .replace("%time%", this.getFormattedTimeLeft());
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
                        .replaceAll("%arena%", this.getName())
                        .replaceAll("%status%", AdventureHelper.toLegacy(statusMessage.asComponent()))
                        .replaceAll("%players%", AdventureHelper.toLegacy(playerMessage.asComponent())))
                .collect(Collectors.toList());

        final var finalBlockBehindMaterial = blockBehindMaterial;
        for (var signBlock : gameSigns) {
            signBlock.getLocation().asOptional(LocationHolder.class)
                    .flatMap(locationHolder -> locationHolder.asOptional(Location.class))
                    .ifPresent(location -> {
                        if (location.getChunk().isLoaded()) {
                            BlockState blockState = location.getBlock().getState();
                            if (blockState instanceof Sign) {
                                Sign sign = (Sign) blockState;
                                for (int i = 0; i < texts.size() && i < 4; i++) {
                                    sign.setLine(i, texts.get(i));
                                }
                                sign.update();
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

    private void updateLobbyScoreboard() {
        if (status != GameStatus.WAITING || !configurationContainer.getOrDefault(ConfigurationContainer.LOBBY_SCOREBOARD, Boolean.class, false)) {
            return;
        }

        if (MainConfig.getInstance().node("experimental", "new-scoreboard-system", "enabled").getBoolean(false)) {
            return;
        }

        Objective obj = gameScoreboard.getObjective("lobby");
        if (obj == null) {
            obj = gameScoreboard.registerNewObjective("lobby", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.setDisplayName(this.formatLobbyScoreboardString(
                    MainConfig.getInstance().node("lobby-scoreboard", "title").getString("§eBEDWARS")));
        }

        var rows = MainConfig.getInstance().node("lobby-scoreboard", "content").childrenList()
                .stream()
                .map(ConfigurationNode::getString)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (rows.isEmpty()) {
            return;
        }

        rows = resizeAndMakeUnique(rows);

        //reset only scores that are changed instead of resetting all entries every tick
        //helps resolve scoreboard flickering
        int i = 15;
        for (String row : rows) {
            try {
                final String element = formatLobbyScoreboardString(row);
                final Score score = obj.getScore(element);

                if (score.getScore() != i) {
                    score.setScore(i);
                    for (String entry : gameScoreboard.getEntries()) {
                        if (obj.getScore(entry).getScore() == i && !entry.equalsIgnoreCase(element)) {
                            gameScoreboard.resetScores(entry);
                        }
                    }
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                e.printStackTrace();
            }
            i--;
        }


        players.forEach(player -> player.as(Player.class).setScoreboard(gameScoreboard)); // TODO: SLib equivalent
    }

    public List<String> resizeAndMakeUnique(List<String> lines) {
        final List<String> content = new ArrayList<>();

        lines.forEach(line -> {
            String copy = line;
            if (copy == null) {
                copy = " ";
            }

            //avoid exceptions returned by getScore()
            if (copy.length() > 40) {
                copy = copy.substring(40);
            }


            final StringBuilder builder = new StringBuilder(copy);
            while (content.contains(builder.toString())) {
                builder.append(" ");
            }
            content.add(builder.toString());
        });

        if (content.size() > 15) {
            return content.subList(0, 15);
        }
        return content;
    }

    public String formatLobbyScoreboardString(String str) {
        String finalStr = str;

        finalStr = finalStr.replace("%arena%", name);
        finalStr = finalStr.replace("%players%", String.valueOf(players.size()));
        finalStr = finalStr.replace("%maxplayers%", String.valueOf(calculatedMaxPlayers));

        return finalStr;
    }

    @Override
    public void selectPlayerTeam(BedWarsPlayer player, TeamImpl team) {
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player.getUuid())) {
            return;
        }
        BedWarsPlayer profile = PlayerManagerImpl.getInstance().getPlayer(player.getUuid()).orElseThrow();
        if (profile.getGame() != this) {
            return;
        }

        selectTeam(profile, team.getName());
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
    public TeamImpl getTeamOfPlayer(BedWarsPlayer player) {
        if (!player.isInGame()) {
            return null;
        }
        return getPlayerTeam(player);
    }

    @Override
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
    public TeamImpl getTeamOfChest(LocationHolder location) {
        for (var team : teamsInGame) {
            if (team.isTeamChestRegistered(location)) {
                return team;
            }
        }
        return null;
    }

    public void addChestForFutureClear(Location loc, Inventory inventory) {
        if (!usedChests.containsKey(loc)) {
            ItemStack[] contents = inventory.getContents();
            ItemStack[] clone = new ItemStack[contents.length];
            for (int i = 0; i < contents.length; i++) {
                ItemStack stack = contents[i];
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
    public boolean isPlayerInAnyTeam(BedWarsPlayer player) {
        return getTeamOfPlayer(player) != null;
    }

    @Override
    public boolean isTeamActive(TeamImpl team) {
        return teamsInGame.contains(team);
    }

    @Override
    public boolean isPlayerInTeam(BedWarsPlayer player, TeamImpl team) {
        return getTeamOfPlayer(player) == team;
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
    public int countTeamChests(TeamImpl team) {
        return team.countTeamChests();
    }

    @Override
    public List<SpecialItem<?,?,?>> getActiveSpecialItems() {
        return List.copyOf(activeSpecialItems);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem<?,?,?>> List<I> getActiveSpecialItems(Class<I> type) {
        return activeSpecialItems.stream()
                .filter(type::isInstance)
                .map(specialItem -> (I) specialItem)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialItem<?,?,?>> getActiveSpecialItemsOfTeam(TeamImpl team) {
        return activeSpecialItems.stream()
                .filter(item -> item.getTeam().equals(team))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem<?,?,?>> List<I> getActiveSpecialItemsOfTeam(TeamImpl team, Class<I> type) {
        return activeSpecialItems.stream()
                .filter(item -> type.isInstance(item) && item.getTeam().equals(team))
                .map(item -> (I) item)
                .collect(Collectors.toList());
    }

    @Override
    public SpecialItem<?,?,?> getFirstActiveSpecialItemOfTeam(TeamImpl team) {
        return activeSpecialItems.stream()
                .filter(item -> item.getTeam().equals(team))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem<?,?,?>> I getFirstActiveSpecialItemOfTeam(TeamImpl team, Class<I> type) {
        return (I) activeSpecialItems.stream()
                .filter(item -> item.getTeam().equals(team) && type.isInstance(item))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<SpecialItem<?,?,?>> getActiveSpecialItemsOfPlayer(BedWarsPlayer player) {
        return activeSpecialItems.stream()
                .filter(item -> item.getPlayer().equals(player))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem<?,?,?>> List<I> getActiveSpecialItemsOfPlayer(BedWarsPlayer player, Class<I> type) {
        return activeSpecialItems.stream()
                .filter(item -> item.getPlayer().equals(player) && type.isInstance(item))
                .map(item -> (I) item)
                .collect(Collectors.toList());
    }

    @Override
    public SpecialItem<?,?,?> getFirstActiveSpecialItemOfPlayer(BedWarsPlayer player) {
        return activeSpecialItems.stream()
                .filter(item -> item.getPlayer().equals(player))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem<?,?,?>> I getFirstActiveSpecialItemOfPlayer(BedWarsPlayer player, Class<I> type) {
        return (I) activeSpecialItems.stream()
                .filter(item -> item.getPlayer().equals(player) && type.isInstance(item))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void registerSpecialItem(SpecialItem<?,?,?> item) {
        if (!activeSpecialItems.contains(item)) {
            activeSpecialItems.add(item);
        }
    }

    @Override
    public void unregisterSpecialItem(SpecialItem<?,?,?> item) {
        activeSpecialItems.remove(item);
    }

    @Override
    public boolean isRegisteredSpecialItem(SpecialItem<?,?,?> item) {
        return activeSpecialItems.contains(item);
    }

    @Override
    public List<DelayFactory> getActiveDelays() {
        return List.copyOf(activeDelays);
    }

    @Override
    public List<DelayFactory> getActiveDelaysOfPlayer(BedWarsPlayer player) {
        return activeDelays.stream()
                .filter(delayFactory -> delayFactory.getPlayer().equals(player))
                .collect(Collectors.toList());
    }

    @Override
    public DelayFactory getActiveDelay(BedWarsPlayer player, Class<? extends SpecialItem<?,?,?>> specialItem) {
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
    public boolean isDelayActive(BedWarsPlayer player, Class<? extends SpecialItem<?,?,?>> specialItem) {
        return activeDelays.stream()
                .filter(delayFactory -> delayFactory.getPlayer().equals(player) && specialItem.isInstance(delayFactory.getSpecialItem()))
                .findFirst()
                .map(DelayFactory::isDelayActive)
                .orElse(false);
    }

    @Override
    public ArenaTime getArenaTime() {
        return arenaTime;
    }

    public void setArenaTime(ArenaTime arenaTime) {
        this.arenaTime = arenaTime;
    }

    @Override
    public WeatherHolder getArenaWeather() {
        return arenaWeather;
    }

    public void setArenaWeather(WeatherHolder arenaWeather) {
        this.arenaWeather = arenaWeather;
    }

    @Override
    public BossBarColorWrapper getLobbyBossBarColor() {
        return new BossBarColorWrapper(this.lobbyBossBarColor);
    }

    public void setLobbyBossBarColor(BossBar.Color color) {
        this.lobbyBossBarColor = color;
    }

    @Override
    public BossBarColorWrapper getGameBossBarColor() {
        return new BossBarColorWrapper(this.gameBossBarColor);
    }

    public void setGameBossBarColor(BossBar.Color color) {
        this.gameBossBarColor = color;
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
                        .replaceAll("\\{player}", player.getName())
                        .replaceAll("\\{score}", Integer.toString(score))
                )
                .map(s -> s.startsWith("/") ? s.substring(1) : s)
                .forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s));
    }

    @Override
    public void selectPlayerRandomTeam(BedWarsPlayer player) {
        joinRandomTeam(player);
    }

    @Override
    public StatusBar<?> getStatusBar() {
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
    public boolean isEntityShop(EntityBasic entity) {
        for (var store : gameStore) {
            if (entity.equals(store.getEntity())) {
                return true;
            }
        }
        return false;
    }

    public RespawnProtection addProtectedPlayer(BedWarsPlayer player) {
        int time = MainConfig.getInstance().node("respawn", "protection-time").getInt(10);

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
    public boolean isProtectionActive(BedWarsPlayer player) {
        return (respawnProtectionMap.containsKey(player));
    }

    public List<BedWarsPlayer> getPlayersWithoutVIP() {
        List<BedWarsPlayer> gamePlayerList = new ArrayList<>(this.players);
        gamePlayerList.removeIf(BedWarsPlayer::canJoinFullGame);

        return gamePlayerList;
    }

    public Inventory getFakeEnderChest(BedWarsPlayer player) {
        if (!fakeEnderChests.containsKey(player)) {
            fakeEnderChests.put(player, Bukkit.createInventory(player.as(Player.class), InventoryType.ENDER_CHEST)); // TODO: remove transformation
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
    public String getCustomPrefix() {
        return customPrefix;
    }

    @Override
    public ComponentWrapper getCustomPrefixComponent() {
        return new ComponentWrapper(AdventureHelper.toComponentNullable(customPrefix));
    }

    @Override
    public @Nullable LocationHolder getLobbyPos1() {
        return lobbyPos1;
    }

    @Override
    public @Nullable LocationHolder getLobbyPos2() {
        return lobbyPos2;
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

    public void setCustomPrefix(String customPrefix) {
        this.customPrefix = customPrefix;
    }

    public void showOtherVisual(Visual<?> visual) {
        players.forEach(visual::addViewer);
        otherVisuals.add(visual);
    }

    public void removeOtherVisual(Visual<?> visual) {
        visual.destroy();
        otherVisuals.remove(visual);
    }
}
