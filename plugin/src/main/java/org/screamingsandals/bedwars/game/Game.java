package org.screamingsandals.bedwars.game;

import static misat11.lib.lang.I.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.ArenaTime;
import org.screamingsandals.bedwars.api.InGameConfigBooleanConstants;
import org.screamingsandals.bedwars.api.Region;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.boss.BossBar;
import org.screamingsandals.bedwars.api.boss.BossBar19;
import org.screamingsandals.bedwars.api.boss.StatusBar;
import org.screamingsandals.bedwars.api.events.*;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.api.upgrades.UpgradeRegistry;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.api.utils.DelayFactory;
import org.screamingsandals.bedwars.boss.BossBarSelector;
import org.screamingsandals.bedwars.boss.XPBar;
import org.screamingsandals.bedwars.commands.StatsCommand;
import org.screamingsandals.bedwars.config.Configurator;
import org.screamingsandals.bedwars.inventories.TeamSelectorInventory;
import org.screamingsandals.bedwars.listener.Player116ListenerUtils;
import org.screamingsandals.bedwars.region.FlatteningRegion;
import org.screamingsandals.bedwars.region.LegacyRegion;
import org.screamingsandals.bedwars.statistics.PlayerStatistic;
import org.screamingsandals.bedwars.utils.*;
import org.screamingsandals.lib.debug.Debug;
import org.screamingsandals.lib.nms.entity.EntityUtils;
import org.screamingsandals.lib.nms.holograms.Hologram;
import org.screamingsandals.lib.signmanager.SignBlock;
import org.screamingsandals.simpleinventories.utils.StackParser;

import com.onarandombox.MultiverseCore.api.Core;
import com.onarandombox.MultiverseCore.api.MVWorldManager;

public class Game implements org.screamingsandals.bedwars.api.game.Game {
    private String name;
    private Location pos1;
    private Location pos2;
    private Location lobbySpawn;
    private Location specSpawn;
    private List<Team> teams = new ArrayList<>();
    private List<ItemSpawner> spawners = new ArrayList<>();
    private Map<Player, RespawnProtection> respawnProtectionMap = new HashMap<>();
    private int pauseCountdown;
    private int gameTime;
    private int minPlayers;
    private List<GamePlayer> players = new ArrayList<>();
    private World world;
    private List<GameStore> gameStore = new ArrayList<>();
    private ArenaTime arenaTime = ArenaTime.WORLD;
    private WeatherType arenaWeather = null;
    private BarColor lobbyBossBarColor = null;
    private BarColor gameBossBarColor = null;

    // Boolean settings
    public static final String COMPASS_ENABLED = "compass-enabled";
    private InGameConfigBooleanConstants compassEnabled = InGameConfigBooleanConstants.INHERIT;

    public static final String JOIN_RANDOM_TEAM_AFTER_LOBBY = "join-randomly-after-lobby-timeout";
    private InGameConfigBooleanConstants joinRandomTeamAfterLobby = InGameConfigBooleanConstants.INHERIT;

    public static final String JOIN_RANDOM_TEAM_ON_JOIN = "join-randomly-on-lobby-join";
    private InGameConfigBooleanConstants joinRandomTeamOnJoin = InGameConfigBooleanConstants.INHERIT;

    public static final String ADD_WOOL_TO_INVENTORY_ON_JOIN = "add-wool-to-inventory-on-join";
    private InGameConfigBooleanConstants addWoolToInventoryOnJoin = InGameConfigBooleanConstants.INHERIT;

    public static final String PREVENT_KILLING_VILLAGERS = "prevent-killing-villagers";
    private InGameConfigBooleanConstants preventKillingVillagers = InGameConfigBooleanConstants.INHERIT;

    public static final String PLAYER_DROPS = "player-drops";
    private InGameConfigBooleanConstants playerDrops = InGameConfigBooleanConstants.INHERIT;

    public static final String FRIENDLY_FIRE = "friendlyfire";
    private InGameConfigBooleanConstants friendlyfire = InGameConfigBooleanConstants.INHERIT;

    public static final String COLORED_LEATHER_BY_TEAM_IN_LOBBY = "in-lobby-colored-leather-by-team";
    private InGameConfigBooleanConstants coloredLeatherByTeamInLobby = InGameConfigBooleanConstants.INHERIT;

    public static final String KEEP_INVENTORY = "keep-inventory-on-death";
    private InGameConfigBooleanConstants keepInventory = InGameConfigBooleanConstants.INHERIT;

    public static final String CRAFTING = "allow-crafting";
    private InGameConfigBooleanConstants crafting = InGameConfigBooleanConstants.INHERIT;

    public static final String GLOBAL_LOBBY_BOSSBAR = "bossbar.lobby.enable";
    public static final String LOBBY_BOSSBAR = "lobbybossbar";
    private InGameConfigBooleanConstants lobbybossbar = InGameConfigBooleanConstants.INHERIT;

    public static final String GLOBAL_GAME_BOSSBAR = "bossbar.game.enable";
    public static final String GAME_BOSSBAR = "bossbar";
    private InGameConfigBooleanConstants gamebossbar = InGameConfigBooleanConstants.INHERIT;

    public static final String GLOBAL_SCOREBOARD = "scoreboard.enable";
    public static final String SCOREBOARD = "scoreboard";
    private InGameConfigBooleanConstants ascoreboard = InGameConfigBooleanConstants.INHERIT;

    public static final String GLOBAL_LOBBY_SCOREBOARD = "lobby-scoreboard.enabled";
    public static final String LOBBY_SCOREBOARD = "lobbyscoreboard";
    private InGameConfigBooleanConstants lobbyscoreboard = InGameConfigBooleanConstants.INHERIT;

    public static final String PREVENT_SPAWNING_MOBS = "prevent-spawning-mobs";
    private InGameConfigBooleanConstants preventSpawningMobs = InGameConfigBooleanConstants.INHERIT;

    public static final String SPAWNER_HOLOGRAMS = "spawner-holograms";
    private InGameConfigBooleanConstants spawnerHolograms = InGameConfigBooleanConstants.INHERIT;

    public static final String SPAWNER_DISABLE_MERGE = "spawner-disable-merge";
    private InGameConfigBooleanConstants spawnerDisableMerge = InGameConfigBooleanConstants.INHERIT;

    public static final String GAME_START_ITEMS = "game-start-items";
    private InGameConfigBooleanConstants gameStartItems = InGameConfigBooleanConstants.INHERIT;

    public static final String PLAYER_RESPAWN_ITEMS = "player-respawn-items";
    private InGameConfigBooleanConstants playerRespawnItems = InGameConfigBooleanConstants.INHERIT;

    public static final String SPAWNER_HOLOGRAMS_COUNTDOWN = "spawner-holograms-countdown";
    private InGameConfigBooleanConstants spawnerHologramsCountdown = InGameConfigBooleanConstants.INHERIT;

    public static final String DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA = "damage-when-player-is-not-in-arena";
    private InGameConfigBooleanConstants damageWhenPlayerIsNotInArena = InGameConfigBooleanConstants.INHERIT;

    public static final String REMOVE_UNUSED_TARGET_BLOCKS = "remove-unused-target-blocks";
    private InGameConfigBooleanConstants removeUnusedTargetBlocks = InGameConfigBooleanConstants.INHERIT;

    public static final String ALLOW_BLOCK_FALLING = "allow-block-falling";
    private InGameConfigBooleanConstants allowBlockFalling = InGameConfigBooleanConstants.INHERIT;

    public static final String HOLO_ABOVE_BED = "holo-above-bed";
    private InGameConfigBooleanConstants holoAboveBed = InGameConfigBooleanConstants.INHERIT;

    public static final String SPECTATOR_JOIN = "allow-spectator-join";
    private InGameConfigBooleanConstants spectatorJoin = InGameConfigBooleanConstants.INHERIT;

    public static final String STOP_TEAM_SPAWNERS_ON_DIE = "stop-team-spawners-on-die";
    private InGameConfigBooleanConstants stopTeamSpawnersOnDie = InGameConfigBooleanConstants.INHERIT;

    public static final String GLOBAL_ANCHOR_AUTO_FILL = "target-block.respawn-anchor.fill-on-start";
    public static final String ANCHOR_AUTO_FILL = "anchor-auto-fill";
    private InGameConfigBooleanConstants anchorAutoFill = InGameConfigBooleanConstants.INHERIT;

    public static final String GLOBAL_ANCHOR_DECREASING = "target-block.respawn-anchor.enable-decrease";
    public static final String ANCHOR_DECREASING = "anchor-decreasing";
    private InGameConfigBooleanConstants anchorDecreasing = InGameConfigBooleanConstants.INHERIT;

    public static final String GLOBAL_CAKE_TARGET_BLOCK_EATING = "target-block.cake.destroy-by-eating";
    public static final String CAKE_TARGET_BLOCK_EATING = "cake-target-block-eating";
    private InGameConfigBooleanConstants cakeTargetBlockEating = InGameConfigBooleanConstants.INHERIT;

    public static final String GLOBAL_TARGET_BLOCK_EXPLOSIONS = "target-block.allow-destroying-with-explosions";
    public static final String TARGET_BLOCK_EXPLOSIONS = "target-block-explosions";
    private InGameConfigBooleanConstants targetBlockExplosions = InGameConfigBooleanConstants.INHERIT;

    public boolean gameStartItem;
    private boolean preServerRestart = false;
    public static final int POST_GAME_WAITING = 3;

    // STATUS
    private GameStatus previousStatus = GameStatus.DISABLED;
    private GameStatus status = GameStatus.DISABLED;
    private GameStatus afterRebuild = GameStatus.WAITING;
    private int countdown = -1, previousCountdown = -1;
    private int calculatedMaxPlayers;
    private BukkitTask task;
    private List<CurrentTeam> teamsInGame = new ArrayList<>();
    private Region region = Main.isLegacy() ? new LegacyRegion() : new FlatteningRegion();
    private TeamSelectorInventory teamSelectorInventory;
    private Scoreboard gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private StatusBar statusbar;
    private Map<Location, ItemStack[]> usedChests = new HashMap<>();
    private List<SpecialItem> activeSpecialItems = new ArrayList<>();
    private List<DelayFactory> activeDelays = new ArrayList<>();
    private List<Hologram> createdHolograms = new ArrayList<>();
    private Map<ItemSpawner, Hologram> countdownHolograms = new HashMap<>();
    private Map<GamePlayer, Inventory> fakeEnderChests = new HashMap<>();

    private Game() {

    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        if (this.world == null) {
            this.world = world;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public void setLobbySpawn(Location lobbySpawn) {
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

    public boolean checkMinPlayers() {
        return players.size() >= getMinPlayers();
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int countPlayers() {
        return this.players.size();
    }

    public List<GameStore> getGameStores() {
        return gameStore;
    }

    public Location getSpecSpawn() {
        return specSpawn;
    }

    public void setSpecSpawn(Location specSpawn) {
        this.specSpawn = specSpawn;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    @Override
    public org.screamingsandals.bedwars.api.Team getTeamFromName(String name) {
        Team team = null;
        for (Team t : getTeams()) {
            if (t.getName().equalsIgnoreCase(name)) {
                team = t;
            }
        }
        return team;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<ItemSpawner> getSpawners() {
        return spawners;
    }

    public void setGameStores(List<GameStore> gameStore) {
        this.gameStore = gameStore;
    }

    public TeamSelectorInventory getTeamSelectorInventory() {
        return teamSelectorInventory;
    }

    public boolean isBlockAddedDuringGame(Location loc) {
        return status == GameStatus.RUNNING && region.isBlockAddedDuringGame(loc);
    }

    public boolean blockPlace(GamePlayer player, Block block, BlockState replaced, ItemStack itemInHand) {
        if (status != GameStatus.RUNNING) {
            return false; // ?
        }
        if (player.isSpectator) {
            return false;
        }
        if (Main.isFarmBlock(block.getType())) {
            return true;
        }
        if (!GameCreator.isInArea(block.getLocation(), pos1, pos2)) {
            return false;
        }

        BedwarsPlayerBuildBlock event = new BedwarsPlayerBuildBlock(this, player.player, getPlayerTeam(player), block,
                itemInHand, replaced);
        Main.getInstance().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        if (replaced.getType() != Material.AIR) {
            if (region.isBlockAddedDuringGame(replaced.getLocation())) {
                return true;
            } else if (Main.isBreakableBlock(replaced.getType()) || region.isLiquid(replaced.getType())) {
                region.putOriginalBlock(block.getLocation(), replaced);
            } else {
                return false;
            }
        }
        region.addBuiltDuringGame(block.getLocation());

        return true;
    }

    public boolean blockBreak(GamePlayer player, Block block, BlockBreakEvent event) {
        if (status != GameStatus.RUNNING) {
            return false; // ?
        }
        if (player.isSpectator) {
            return false;
        }
        if (Main.isFarmBlock(block.getType())) {
            return true;
        }
        if (!GameCreator.isInArea(block.getLocation(), pos1, pos2)) {
            return false;
        }

        BedwarsPlayerBreakBlock breakEvent = new BedwarsPlayerBreakBlock(this, player.player, getPlayerTeam(player),
                block);
        Main.getInstance().getServer().getPluginManager().callEvent(breakEvent);

        if (breakEvent.isCancelled()) {
            return false;
        }

        if (region.isBlockAddedDuringGame(block.getLocation())) {
            region.removeBlockBuiltDuringGame(block.getLocation());

            if (block.getType() == Material.ENDER_CHEST) {
                CurrentTeam team = getTeamOfChest(block);
                if (team != null) {
                    team.removeTeamChest(block);
                    String message = i18n("team_chest_broken");
                    for (GamePlayer gp : team.players) {
                        gp.player.sendMessage(message);
                    }

                    if (breakEvent.isDrops()) {
                        event.setDropItems(false);
                        player.player.getInventory().addItem(new ItemStack(Material.ENDER_CHEST));
                    }
                }
            }

            if (!breakEvent.isDrops()) {
                try {
                    event.setDropItems(false);
                } catch (Throwable tr) {
                    block.setType(Material.AIR);
                }
            }
            return true;
        }

        Location loc = block.getLocation();
        if (region.isBedBlock(block.getState())) {
            if (!region.isBedHead(block.getState())) {
                loc = region.getBedNeighbor(block).getLocation();
            }
        }
        if (isTargetBlock(loc)) {
            if (region.isBedBlock(block.getState())) {
                if (getPlayerTeam(player).teamInfo.bed.equals(loc)) {
                    return false;
                }
                bedDestroyed(loc, player.player, true, false, false);
                region.putOriginalBlock(block.getLocation(), block.getState());
                if (block.getLocation().equals(loc)) {
                    Block neighbor = region.getBedNeighbor(block);
                    region.putOriginalBlock(neighbor.getLocation(), neighbor.getState());
                } else {
                    region.putOriginalBlock(loc, region.getBedNeighbor(block).getState());
                }
                try {
                    event.setDropItems(false);
                } catch (Throwable tr) {
                    if (region.isBedHead(block.getState())) {
                        region.getBedNeighbor(block).setType(Material.AIR);
                    } else {
                        block.setType(Material.AIR);
                    }
                }
                return true;
            } else if (getOriginalOrInheritedCakeTargetBlockEating() && block.getType().name().contains("CAKE")) {
                return false; // when CAKES are in eating mode, don't allow to just break it
            } else {
                if (getPlayerTeam(player).teamInfo.bed.equals(loc)) {
                    return false;
                }
                bedDestroyed(loc, player.player, false, "RESPAWN_ANCHOR".equals(block.getType().name()), block.getType().name().contains("CAKE"));
                region.putOriginalBlock(loc, block.getState());
                try {
                    event.setDropItems(false);
                } catch (Throwable tr) {
                    block.setType(Material.AIR);
                }
                return true;
            }
        }
        if (Main.isBreakableBlock(block.getType())) {
            region.putOriginalBlock(block.getLocation(), block.getState());
            return true;
        }
        return false;
    }

    public void targetBlockExplode(RunningTeam team) {
        Location loc = team.getTargetBlock();
        Block block = loc.getBlock();
        if (region.isBedBlock(block.getState())) {
            if (!region.isBedHead(block.getState())) {
                loc = region.getBedNeighbor(block).getLocation();
            }
        }
        if (isTargetBlock(loc)) {
            if (region.isBedBlock(block.getState())) {
                bedDestroyed(loc, null, true, false, false);
                region.putOriginalBlock(block.getLocation(), block.getState());
                if (block.getLocation().equals(loc)) {
                    Block neighbor = region.getBedNeighbor(block);
                    region.putOriginalBlock(neighbor.getLocation(), neighbor.getState());
                } else {
                    region.putOriginalBlock(loc, region.getBedNeighbor(block).getState());
                }
                if (region.isBedHead(block.getState())) {
                    region.getBedNeighbor(block).setType(Material.AIR);
                } else {
                    block.setType(Material.AIR);
                }
            } else {
                bedDestroyed(loc, null, false, "RESPAWN_ANCHOR".equals(block.getType().name()), block.getType().name().contains("CAKE"));
                region.putOriginalBlock(loc, block.getState());
                block.setType(Material.AIR);
            }
        }
    }

    private boolean isTargetBlock(Location loc) {
        for (CurrentTeam team : teamsInGame) {
            if (team.isBed && team.teamInfo.bed.equals(loc)) {
                return true;
            }
        }
        return false;
    }

    public Region getRegion() {
        return region;
    }

    public CurrentTeam getPlayerTeam(GamePlayer player) {
        for (CurrentTeam team : teamsInGame) {
            if (team.players.contains(player)) {
                return team;
            }
        }
        return null;
    }

    public CurrentTeam getCurrentTeamFromTeam(org.screamingsandals.bedwars.api.Team team) {
        for (CurrentTeam currentTeam : teamsInGame) {
            if (currentTeam.teamInfo == team) {
                return currentTeam;
            }
        }
        return null;
    }

    public void bedDestroyed(Location loc, Player broker, boolean isItBedBlock, boolean isItAnchor, boolean isItCake) {
        if (status == GameStatus.RUNNING) {
            for (CurrentTeam team : teamsInGame) {
                if (team.teamInfo.bed.equals(loc)) {
                    team.isBed = false;
                    updateScoreboard();
                    String colored_broker = "explosion";
                    if (broker != null) {
                        colored_broker = getPlayerTeam(Main.getPlayerGameProfile(broker)).teamInfo.color.chatColor + broker.getDisplayName();
                    }
                    for (GamePlayer player : players) {
                        final String key = isItBedBlock ? "bed_is_destroyed" : (isItAnchor ? "anchor_is_destroyed" : (isItCake ? "cake_is_destroyed" : "target_is_destroyed"));
                        Title.send(player.player,
                                i18n(key, false)
                                        .replace("%team%", team.teamInfo.color.chatColor + team.teamInfo.name)
                                        .replace("%broker%", colored_broker),
                                i18n(getPlayerTeam(player) == team ? "bed_is_destroyed_subtitle_for_victim"
                                        : "bed_is_destroyed_subtitle", false));
                        player.player.sendMessage(i18n(key)
                                .replace("%team%", team.teamInfo.color.chatColor + team.teamInfo.name)
                                .replace("%broker%", colored_broker));
                        SpawnEffects.spawnEffect(this, player.player, "game-effects.beddestroy");
                        Sounds.playSound(player.player, player.player.getLocation(),
                                Main.getConfigurator().config.getString("sounds.on_bed_destroyed"),
                                Sounds.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                    }

                    if (team.hasBedHolo()) {
                        team.getBedHolo().setLine(0,
                                i18nonly(isItBedBlock ? "protect_your_bed_destroyed" : (isItAnchor ? "protect_your_anchor_destroyed" : (isItCake ? "protect_your_cake_destroyed" : "protect_your_target_destroyed"))));
                        team.getBedHolo().addViewers(team.getConnectedPlayers());
                    }

                    if (team.hasProtectHolo()) {
                        team.getProtectHolo().destroy();
                    }

                    BedwarsTargetBlockDestroyedEvent targetBlockDestroyed = new BedwarsTargetBlockDestroyedEvent(this,
                            broker, team);
                    Main.getInstance().getServer().getPluginManager().callEvent(targetBlockDestroyed);

                    if (broker != null) {
                        if (Main.isPlayerStatisticsEnabled()) {
                            PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(broker);
                            statistic.setCurrentDestroyedBeds(statistic.getCurrentDestroyedBeds() + 1);
                            statistic.setCurrentScore(statistic.getCurrentScore()
                                    + Main.getConfigurator().config.getInt("statistics.scores.bed-destroy", 25));
                        }

                        dispatchRewardCommands("player-destroy-bed", broker,
                                Main.getConfigurator().config.getInt("statistics.scores.bed-destroy", 25));
                    }
                }
            }
        }
    }

    public void internalJoinPlayer(GamePlayer gamePlayer) {
        BedwarsPlayerJoinEvent joinEvent = new BedwarsPlayerJoinEvent(this, gamePlayer.player);
        Main.getInstance().getServer().getPluginManager().callEvent(joinEvent);

        if (joinEvent.isCancelled()) {
            String message = joinEvent.getCancelMessage();
            if (message != null && !message.equals("")) {
                gamePlayer.player.sendMessage(message);
            }
            gamePlayer.changeGame(null);
            return;
        }

        boolean isEmpty = players.isEmpty();
        if (!players.contains(gamePlayer)) {
            players.add(gamePlayer);
        }
        updateSigns();

        if (Main.isPlayerStatisticsEnabled()) {
            // Load
            Main.getPlayerStatisticsManager().getStatistic(gamePlayer.player);
        }

        if (arenaTime.time >= 0) {
            gamePlayer.player.setPlayerTime(arenaTime.time, false);
        }

        if (arenaWeather != null) {
            gamePlayer.player.setPlayerWeather(arenaWeather);
        }

        if (status == GameStatus.WAITING) {
            mpr("join").replace("name", gamePlayer.player.getDisplayName())
                    .replace("players", players.size())
                    .replace("maxplayers", calculatedMaxPlayers)
                    .send(getConnectedPlayers());

            gamePlayer.teleport(lobbySpawn, () -> {
                SpawnEffects.spawnEffect(Game.this, gamePlayer.player, "game-effects.lobbyjoin");

                if (getOriginalOrInheritedJoinRandomTeamOnJoin()) {
                    joinRandomTeam(gamePlayer);
                }

                if (getOriginalOrInheritedCompassEnabled()) {
                    int compassPosition = Main.getConfigurator().config.getInt("hotbar.selector", 0);
                    if (compassPosition >= 0 && compassPosition <= 8) {
                        ItemStack compass = Main.getConfigurator().readDefinedItem("jointeam", "COMPASS");
                        ItemMeta metaCompass = compass.getItemMeta();
                        metaCompass.setDisplayName(i18n("compass_selector_team", false));
                        compass.setItemMeta(metaCompass);
                        gamePlayer.player.getInventory().setItem(compassPosition, compass);
                    }
                }

                int leavePosition = Main.getConfigurator().config.getInt("hotbar.leave", 8);
                if (leavePosition >= 0 && leavePosition <= 8) {
                    ItemStack leave = Main.getConfigurator().readDefinedItem("leavegame", "SLIME_BALL");
                    ItemMeta leaveMeta = leave.getItemMeta();
                    leaveMeta.setDisplayName(i18n("leave_from_game_item", false));
                    leave.setItemMeta(leaveMeta);
                    gamePlayer.player.getInventory().setItem(leavePosition, leave);
                }

                if (gamePlayer.player.hasPermission("bw.vip.startitem")
                        || gamePlayer.player.hasPermission("misat11.bw.vip.startitem")) {
                    int vipPosition = Main.getConfigurator().config.getInt("hotbar.start", 1);
                    if (vipPosition >= 0 && vipPosition <= 8) {
                        ItemStack startGame = Main.getConfigurator().readDefinedItem("startgame", "DIAMOND");
                        ItemMeta startGameMeta = startGame.getItemMeta();
                        startGameMeta.setDisplayName(i18n("start_game_item", false));
                        startGame.setItemMeta(startGameMeta);

                        gamePlayer.player.getInventory().setItem(vipPosition, startGame);
                    }
                }
            });

            if (isEmpty) {
                runTask();
            } else {
                statusbar.addPlayer(gamePlayer.player);
            }
        }

        if (status == GameStatus.RUNNING || status == GameStatus.GAME_END_CELEBRATING) {

            makeSpectator(gamePlayer, true);
            createdHolograms.forEach(hologram -> hologram.addViewer(gamePlayer.player));
        }

        BedwarsPlayerJoinedEvent joinedEvent = new BedwarsPlayerJoinedEvent(this, getPlayerTeam(gamePlayer), gamePlayer.player);
        Main.getInstance().getServer().getPluginManager().callEvent(joinedEvent);
    }

    public void internalLeavePlayer(GamePlayer gamePlayer) {
        if (status == GameStatus.DISABLED) {
            return;
        }

        BedwarsPlayerLeaveEvent playerLeaveEvent = new BedwarsPlayerLeaveEvent(this, gamePlayer.player,
                getPlayerTeam(gamePlayer));
        Main.getInstance().getServer().getPluginManager().callEvent(playerLeaveEvent);

        String message = i18n("leave").replace("%name%", gamePlayer.player.getDisplayName())
                .replace("%players%", Integer.toString(players.size()))
                .replaceAll("%maxplayers%", Integer.toString(calculatedMaxPlayers));

        if (!preServerRestart) {
            for (GamePlayer p : players) {
                p.player.sendMessage(message);
            }
        }

        players.remove(gamePlayer);
        updateSigns();

        if (status == GameStatus.WAITING) {
            SpawnEffects.spawnEffect(this, gamePlayer.player, "game-effects.lobbyleave");
        }

        statusbar.removePlayer(gamePlayer.player);
        createdHolograms.forEach(holo -> holo.removeViewer(gamePlayer.player));

        gamePlayer.player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        if (Main.getConfigurator().config.getBoolean("mainlobby.enabled")
                && !Main.getConfigurator().config.getBoolean("bungee.enabled")) {
            Location mainLobbyLocation = MiscUtils.readLocationFromString(
                    Bukkit.getWorld(Main.getConfigurator().config.getString("mainlobby.world")),
                    Main.getConfigurator().config.getString("mainlobby.location"));
            gamePlayer.teleport(mainLobbyLocation);
        }

        if (status == GameStatus.RUNNING || status == GameStatus.WAITING) {
            CurrentTeam team = getPlayerTeam(gamePlayer);
            if (team != null) {
                team.players.remove(gamePlayer);
                if (status == GameStatus.WAITING) {
                    team.getScoreboardTeam().removeEntry(gamePlayer.player.getName());
                    if (team.players.isEmpty()) {
                        teamsInGame.remove(team);
                        team.getScoreboardTeam().unregister();
                    }
                } else {
                    updateScoreboard();
                }
            }
        }

        if (Main.isPlayerStatisticsEnabled()) {
            PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(gamePlayer.player);
            Main.getPlayerStatisticsManager().storeStatistic(statistic);

            Main.getPlayerStatisticsManager().unloadStatistic(gamePlayer.player);
        }

        if (players.isEmpty()) {
            if (!preServerRestart) {
                BedWarsPlayerLastLeaveEvent playerLastLeaveEvent = new BedWarsPlayerLastLeaveEvent(this, gamePlayer.player,
                        getPlayerTeam(gamePlayer));
                Main.getInstance().getServer().getPluginManager().callEvent(playerLastLeaveEvent);
            }

            if (status != GameStatus.WAITING) {
                afterRebuild = GameStatus.WAITING;
                updateSigns();
                rebuild();
            } else {
                status = GameStatus.WAITING;
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
            for (CurrentTeam team : teamsInGame) {
                team.getScoreboardTeam().unregister();
            }
            teamsInGame.clear();
            for (GameStore store : gameStore) {
                LivingEntity villager = store.kill();
                if (villager != null) {
                    Main.unregisterGameEntity(villager);
                }
            }
        }
    }

    public static Game loadGame(File file) {
        return loadGame(file, true);
    }

    public static Game loadGame(File file, boolean firstAttempt) {
        try {
            if (!file.exists()) {
                return null;
            }

            final FileConfiguration configMap = new YamlConfiguration();
            try {
                configMap.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
                return null;
            }

            final Game game = new Game();
            game.name = configMap.getString("name");
            game.pauseCountdown = configMap.getInt("pauseCountdown");
            game.gameTime = configMap.getInt("gameTime");

            String worldName = configMap.getString("world");
            game.world = Bukkit.getWorld(worldName);

            if (game.world == null) {
                if (Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")) {
                    Bukkit.getConsoleSender().sendMessage("§c[B§fW] §cWorld " + worldName
                            + " was not found, but we found Multiverse-Core, so we will try to load this world.");

                    Core multiverse = (Core) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
                    if (multiverse != null) {
                        MVWorldManager manager = multiverse.getMVWorldManager();
                        if (manager.loadWorld(worldName)) {
                            Bukkit.getConsoleSender().sendMessage("§c[B§fW] §aWorld " + worldName
                                    + " was succesfully loaded with Multiverse-Core, continue in arena loading.");

                            game.world = Bukkit.getWorld(worldName);
                        }
                    } else {
                        Bukkit.getConsoleSender().sendMessage("§c[B§fW] §cArena " + game.name
                                + " can't be loaded, because world " + worldName + " is missing!");
                        return null;
                    }
                } else if (firstAttempt) {
                    Bukkit.getConsoleSender().sendMessage(
                            "§c[B§fW] §eArena " + game.name + " can't be loaded, because world " + worldName + " is missing! We will try it again after all plugins will be loaded!");
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> loadGame(file, false), 10L);
                    return null;
                }
                Bukkit.getConsoleSender().sendMessage(
                        "§c[B§fW] §cArena " + game.name + " can't be loaded, because world " + worldName + " is missing!");
                return null;
            }

            if (Main.getVersionNumber() >= 115) {
                game.world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            }

            game.pos1 = MiscUtils.readLocationFromString(game.world, configMap.getString("pos1"));
            game.pos2 = MiscUtils.readLocationFromString(game.world, configMap.getString("pos2"));
            game.specSpawn = MiscUtils.readLocationFromString(game.world, configMap.getString("specSpawn"));
            String spawnWorld = configMap.getString("lobbySpawnWorld");
            World lobbySpawnWorld = Bukkit.getWorld(spawnWorld);
            if (lobbySpawnWorld == null) {
                if (Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")) {
                    Bukkit.getConsoleSender().sendMessage("§c[B§fW] §cWorld " + spawnWorld
                            + " was not found, but we found Multiverse-Core, so we will try to load this world.");

                    Core multiverse = (Core) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
                    MVWorldManager manager = multiverse.getMVWorldManager();
                    if (manager.loadWorld(spawnWorld)) {
                        Bukkit.getConsoleSender().sendMessage("§c[B§fW] §aWorld " + spawnWorld
                                + " was succesfully loaded with Multiverse-Core, continue in arena loading.");

                        lobbySpawnWorld = Bukkit.getWorld(spawnWorld);
                    } else {
                        Bukkit.getConsoleSender().sendMessage("§c[B§fW] §cArena " + game.name
                                + " can't be loaded, because world " + spawnWorld + " is missing!");
                        return null;
                    }
                } else if (firstAttempt) {
                    Bukkit.getConsoleSender().sendMessage(
                            "§c[B§fW] §eArena " + game.name + " can't be loaded, because world " + worldName + " is missing! We will try it again after all plugins will be loaded!");
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> loadGame(file, false), 10L);
                    return null;
                } else {
                    Bukkit.getConsoleSender().sendMessage(
                            "§c[B§fW] §cArena " + game.name + " can't be loaded, because world " + spawnWorld + " is missing!");
                    return null;
                }
            }
            game.lobbySpawn = MiscUtils.readLocationFromString(lobbySpawnWorld, configMap.getString("lobbySpawn"));
            game.minPlayers = configMap.getInt("minPlayers", 2);
            if (configMap.isSet("teams")) {
                for (String teamN : configMap.getConfigurationSection("teams").getKeys(false)) {
                    ConfigurationSection team = configMap.getConfigurationSection("teams").getConfigurationSection(teamN);
                    Team t = new Team();
                    t.newColor = team.getBoolean("isNewColor", false);
                    t.color = TeamColor.valueOf(MiscUtils.convertColorToNewFormat(team.getString("color"), t));
                    t.name = teamN;
                    t.bed = MiscUtils.readLocationFromString(game.world, team.getString("bed"));
                    t.maxPlayers = team.getInt("maxPlayers");
                    t.spawn = MiscUtils.readLocationFromString(game.world, team.getString("spawn"));
                    t.game = game;

                    t.newColor = true;
                    game.teams.add(t);
                }
            }
            if (configMap.isSet("spawners")) {
                List<Map<String, Object>> spawners = (List<Map<String, Object>>) configMap.getList("spawners");
                for (Map<String, Object> spawner : spawners) {
                    ItemSpawner sa = new ItemSpawner(
                            MiscUtils.readLocationFromString(game.world, (String) spawner.get("location")),
                            Main.getSpawnerType(((String) spawner.get("type")).toLowerCase()),
                            (String) spawner.get("customName"), ((Boolean) spawner.getOrDefault("hologramEnabled", true)),
                            ((Number) spawner.getOrDefault("startLevel", 1)).doubleValue(),
                            game.getTeamFromName((String) spawner.get("team")),
                            (int) spawner.getOrDefault("maxSpawnedResources", -1));
                    game.spawners.add(sa);
                }
            }
            if (configMap.isSet("stores")) {
                List<Object> stores = (List<Object>) configMap.getList("stores");
                for (Object store : stores) {
                    if (store instanceof Map) {
                        Map<String, String> map = (Map<String, String>) store;
                        game.gameStore.add(new GameStore(MiscUtils.readLocationFromString(game.world, map.get("loc")),
                                map.get("shop"), "true".equals(map.getOrDefault("parent", "true")),
                                EntityType.valueOf(map.getOrDefault("type", "VILLAGER").toUpperCase()),
                                map.getOrDefault("name", ""), map.containsKey("name"), "true".equals(map.getOrDefault("isBaby", "false"))));
                    } else if (store instanceof String) {
                        game.gameStore.add(new GameStore(MiscUtils.readLocationFromString(game.world, (String) store), null,
                                true, EntityType.VILLAGER, "", false, false));
                    }
                }
            }

            game.compassEnabled = readBooleanConstant(configMap.getString("constant." + COMPASS_ENABLED, "inherit"));
            game.addWoolToInventoryOnJoin = readBooleanConstant(
                    configMap.getString("constant." + ADD_WOOL_TO_INVENTORY_ON_JOIN, "inherit"));
            game.coloredLeatherByTeamInLobby = readBooleanConstant(
                    configMap.getString("constant." + COLORED_LEATHER_BY_TEAM_IN_LOBBY, "inherit"));
            game.crafting = readBooleanConstant(configMap.getString("constant." + CRAFTING, "inherit"));
            game.friendlyfire = readBooleanConstant(configMap.getString("constant." + FRIENDLY_FIRE, "inherit"));
            game.joinRandomTeamAfterLobby = readBooleanConstant(
                    configMap.getString("constant." + JOIN_RANDOM_TEAM_AFTER_LOBBY, "inherit"));
            game.joinRandomTeamOnJoin = readBooleanConstant(
                    configMap.getString("constant." + JOIN_RANDOM_TEAM_ON_JOIN, "inherit"));
            game.keepInventory = readBooleanConstant(configMap.getString("constant." + KEEP_INVENTORY, "inherit"));
            game.preventKillingVillagers = readBooleanConstant(
                    configMap.getString("constant." + PREVENT_KILLING_VILLAGERS, "inherit"));
            game.playerDrops = readBooleanConstant(configMap.getString("constant." + PLAYER_DROPS, "inherit"));
            game.lobbybossbar = readBooleanConstant(configMap.getString("constant." + LOBBY_BOSSBAR, "inherit"));
            game.gamebossbar = readBooleanConstant(configMap.getString("constant." + GAME_BOSSBAR, "inherit"));
            game.ascoreboard = readBooleanConstant(configMap.getString("constant." + SCOREBOARD, "inherit"));
            game.lobbyscoreboard = readBooleanConstant(configMap.getString("constant." + LOBBY_SCOREBOARD, "inherit"));
            game.preventSpawningMobs = readBooleanConstant(
                    configMap.getString("constant." + PREVENT_SPAWNING_MOBS, "inherit"));
            game.spawnerHolograms = readBooleanConstant(configMap.getString("constant." + SPAWNER_HOLOGRAMS, "inherit"));
            game.spawnerDisableMerge = readBooleanConstant(
                    configMap.getString("constant." + SPAWNER_DISABLE_MERGE, "inherit"));
            game.gameStartItems = readBooleanConstant(configMap.getString("constant." + GAME_START_ITEMS, "inherit"));
            game.playerRespawnItems = readBooleanConstant(
                    configMap.getString("constant." + PLAYER_RESPAWN_ITEMS, "inherit"));
            game.spawnerHologramsCountdown = readBooleanConstant(
                    configMap.getString("constant." + SPAWNER_HOLOGRAMS_COUNTDOWN, "inherit"));
            game.damageWhenPlayerIsNotInArena = readBooleanConstant(
                    configMap.getString("constant." + DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA, "inherit"));
            game.removeUnusedTargetBlocks = readBooleanConstant(
                    configMap.getString("constant." + REMOVE_UNUSED_TARGET_BLOCKS, "inherit"));
            game.allowBlockFalling = readBooleanConstant(
                    configMap.getString("constant." + ALLOW_BLOCK_FALLING, "inherit"));
            game.holoAboveBed = readBooleanConstant(configMap.getString("constant." + HOLO_ABOVE_BED, "inherit"));
            game.spectatorJoin = readBooleanConstant(configMap.getString("constant." + SPECTATOR_JOIN, "inherit"));
            game.anchorAutoFill = readBooleanConstant(configMap.getString("constant." + ANCHOR_AUTO_FILL, "inherit"));
            game.anchorDecreasing = readBooleanConstant(configMap.getString("constant." + ANCHOR_DECREASING, "inherit"));
            game.cakeTargetBlockEating = readBooleanConstant(configMap.getString("constant." + CAKE_TARGET_BLOCK_EATING, "inherit"));
            game.targetBlockExplosions = readBooleanConstant(configMap.getString("constant." + TARGET_BLOCK_EXPLOSIONS, "inherit"));

            game.arenaTime = ArenaTime.valueOf(configMap.getString("arenaTime", ArenaTime.WORLD.name()).toUpperCase());
            game.arenaWeather = loadWeather(configMap.getString("arenaWeather", "default").toUpperCase());

            try {
                game.lobbyBossBarColor = loadBossBarColor(
                        configMap.getString("lobbyBossBarColor", "default").toUpperCase());
                game.gameBossBarColor = loadBossBarColor(configMap.getString("gameBossBarColor", "default").toUpperCase());
            } catch (Throwable t) {
                // We're using 1.8
            }

            Main.addGame(game);
            game.start();
            Bukkit.getConsoleSender().sendMessage("§c[B§fW] §aArena §f" + game.name + "§a loaded!");
            return game;
        } catch (Throwable throwable) {
            Debug.warn("Something went wrong while loading arena file " + file.getName() + ". Please report this to our Discord or GitHub!", true);
            throwable.printStackTrace();
            return null;
        }
    }

    public static WeatherType loadWeather(String weather) {
        try {
            return WeatherType.valueOf(weather);
        } catch (Exception e) {
            return null;
        }
    }

    public static BarColor loadBossBarColor(String color) {
        try {
            return BarColor.valueOf(color);
        } catch (Exception e) {
            return null;
        }
    }

    public static InGameConfigBooleanConstants readBooleanConstant(String s) {
        if ("true".equalsIgnoreCase(s)) {
            return InGameConfigBooleanConstants.TRUE;
        } else if ("false".equalsIgnoreCase(s)) {
            return InGameConfigBooleanConstants.FALSE;
        }

        return InGameConfigBooleanConstants.INHERIT;
    }

    public static String writeBooleanConstant(InGameConfigBooleanConstants constant) {
        switch (constant) {
            case TRUE:
                return "true";
            case FALSE:
                return "false";
            case INHERIT:
            default:
                return "inherit";
        }
    }

    public void saveToConfig() {
        File dir = new File(Main.getInstance().getDataFolder(), "arenas");
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dir, name + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration configMap = new YamlConfiguration();
        configMap.set("name", name);
        configMap.set("pauseCountdown", pauseCountdown);
        configMap.set("gameTime", gameTime);
        configMap.set("world", world.getName());
        configMap.set("pos1", MiscUtils.setLocationToString(pos1));
        configMap.set("pos2", MiscUtils.setLocationToString(pos2));
        configMap.set("specSpawn", MiscUtils.setLocationToString(specSpawn));
        configMap.set("lobbySpawn", MiscUtils.setLocationToString(lobbySpawn));
        configMap.set("lobbySpawnWorld", lobbySpawn.getWorld().getName());
        configMap.set("minPlayers", minPlayers);
        if (!teams.isEmpty()) {
            for (Team t : teams) {
                configMap.set("teams." + t.name + ".isNewColor", t.isNewColor());
                configMap.set("teams." + t.name + ".color", t.color.name());
                configMap.set("teams." + t.name + ".maxPlayers", t.maxPlayers);
                configMap.set("teams." + t.name + ".bed", MiscUtils.setLocationToString(t.bed));
                configMap.set("teams." + t.name + ".spawn", MiscUtils.setLocationToString(t.spawn));
            }
        }
        List<Map<String, Object>> nS = new ArrayList<>();
        for (ItemSpawner spawner : spawners) {
            Map<String, Object> spawnerMap = new HashMap<>();
            spawnerMap.put("location", MiscUtils.setLocationToString(spawner.loc));
            spawnerMap.put("type", spawner.type.getConfigKey());
            spawnerMap.put("customName", spawner.customName);
            spawnerMap.put("startLevel", spawner.startLevel);
            spawnerMap.put("hologramEnabled", spawner.hologramEnabled);
            if (spawner.getTeam() != null) {
                spawnerMap.put("team", spawner.getTeam().getName());
            } else {
                spawnerMap.put("team", null);
            }
            spawnerMap.put("maxSpawnedResources", spawner.maxSpawnedResources);
            nS.add(spawnerMap);
        }
        configMap.set("spawners", nS);
        if (!gameStore.isEmpty()) {
            List<Map<String, String>> nL = new ArrayList<>();
            for (GameStore store : gameStore) {
                Map<String, String> map = new HashMap<>();
                map.put("loc", MiscUtils.setLocationToString(store.getStoreLocation()));
                map.put("shop", store.getShopFile());
                map.put("parent", store.getUseParent() ? "true" : "false");
                map.put("type", store.getEntityType().name());
                if (store.isShopCustomName()) {
                    map.put("name", store.getShopCustomName());
                }
                map.put("isBaby", store.isBaby() ? "true" : "false");
                nL.add(map);
            }
            configMap.set("stores", nL);
        }

        configMap.set("constant." + COMPASS_ENABLED, writeBooleanConstant(compassEnabled));
        configMap.set("constant." + ADD_WOOL_TO_INVENTORY_ON_JOIN, writeBooleanConstant(addWoolToInventoryOnJoin));
        configMap.set("constant." + COLORED_LEATHER_BY_TEAM_IN_LOBBY,
                writeBooleanConstant(coloredLeatherByTeamInLobby));
        configMap.set("constant." + CRAFTING, writeBooleanConstant(crafting));
        configMap.set("constant." + JOIN_RANDOM_TEAM_AFTER_LOBBY, writeBooleanConstant(joinRandomTeamAfterLobby));
        configMap.set("constant." + JOIN_RANDOM_TEAM_ON_JOIN, writeBooleanConstant(joinRandomTeamOnJoin));
        configMap.set("constant." + KEEP_INVENTORY, writeBooleanConstant(keepInventory));
        configMap.set("constant." + PREVENT_KILLING_VILLAGERS, writeBooleanConstant(preventKillingVillagers));
        configMap.set("constant." + PLAYER_DROPS, writeBooleanConstant(playerDrops));
        configMap.set("constant." + FRIENDLY_FIRE, writeBooleanConstant(friendlyfire));
        configMap.set("constant." + LOBBY_BOSSBAR, writeBooleanConstant(lobbybossbar));
        configMap.set("constant." + GAME_BOSSBAR, writeBooleanConstant(gamebossbar));
        configMap.set("constant." + LOBBY_SCOREBOARD, writeBooleanConstant(lobbyscoreboard));
        configMap.set("constant." + SCOREBOARD, writeBooleanConstant(ascoreboard));
        configMap.set("constant." + PREVENT_SPAWNING_MOBS, writeBooleanConstant(preventSpawningMobs));
        configMap.set("constant." + SPAWNER_HOLOGRAMS, writeBooleanConstant(spawnerHolograms));
        configMap.set("constant." + SPAWNER_DISABLE_MERGE, writeBooleanConstant(spawnerDisableMerge));
        configMap.set("constant." + GAME_START_ITEMS, writeBooleanConstant(gameStartItems));
        configMap.set("constant." + PLAYER_RESPAWN_ITEMS, writeBooleanConstant(playerRespawnItems));
        configMap.set("constant." + SPAWNER_HOLOGRAMS_COUNTDOWN, writeBooleanConstant(spawnerHologramsCountdown));
        configMap.set("constant." + DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA,
                writeBooleanConstant(damageWhenPlayerIsNotInArena));
        configMap.set("constant." + REMOVE_UNUSED_TARGET_BLOCKS, writeBooleanConstant(removeUnusedTargetBlocks));
        configMap.set("constant." + ALLOW_BLOCK_FALLING, writeBooleanConstant(allowBlockFalling));
        configMap.set("constant." + HOLO_ABOVE_BED, writeBooleanConstant(holoAboveBed));
        configMap.set("constant." + SPECTATOR_JOIN, writeBooleanConstant(spectatorJoin));
        configMap.set("constant." + ANCHOR_AUTO_FILL, writeBooleanConstant(anchorAutoFill));
        configMap.set("constant." + ANCHOR_DECREASING, writeBooleanConstant(anchorDecreasing));
        configMap.set("constant." + CAKE_TARGET_BLOCK_EATING, writeBooleanConstant(cakeTargetBlockEating));
        configMap.set("constant." + TARGET_BLOCK_EXPLOSIONS, writeBooleanConstant(targetBlockExplosions));

        configMap.set("arenaTime", arenaTime.name());
        configMap.set("arenaWeather", arenaWeather == null ? "default" : arenaWeather.name());

        try {
            configMap.set("lobbyBossBarColor", lobbyBossBarColor == null ? "default" : lobbyBossBarColor.name());
            configMap.set("gameBossBarColor", gameBossBarColor == null ? "default" : gameBossBarColor.name());
        } catch (Throwable t) {
            // We're using 1.8
        }

        try {
            configMap.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Game createGame(String name) {
        Game game = new Game();
        game.name = name;
        game.pauseCountdown = 60;
        game.gameTime = 3600;
        game.minPlayers = 2;

        return game;
    }

    public void start() {
        if (status == GameStatus.DISABLED) {
            status = GameStatus.WAITING;
            countdown = -1;
            calculatedMaxPlayers = 0;
            for (Team team : teams) {
                calculatedMaxPlayers += team.maxPlayers;
            }
            new BukkitRunnable() {
                public void run() {
                    updateSigns();
                }
            }.runTask(Main.getInstance());

            if (Main.getConfigurator().config.getBoolean("bossbar.use-xp-bar", false)) {
                statusbar = new XPBar();
            } else {
                statusbar = BossBarSelector.getBossBar();
            }
        }
    }

    public void stop() {
        if (status == GameStatus.DISABLED) {
            return; // Game is already stopped
        }
        List<GamePlayer> clonedPlayers = (List<GamePlayer>) ((ArrayList<GamePlayer>) players).clone();
        for (GamePlayer p : clonedPlayers)
            p.changeGame(null);
        if (status != GameStatus.REBUILDING) {
            status = GameStatus.DISABLED;
            updateSigns();
        } else {
            afterRebuild = GameStatus.DISABLED;
        }
    }

    public void joinToGame(Player player) {
        if (status == GameStatus.DISABLED) {
            return;
        }

        if (status == GameStatus.REBUILDING) {
            if (isBungeeEnabled()) {
                BungeeUtils.movePlayerToBungeeServer(player, false);
                BungeeUtils.sendPlayerBungeeMessage(player,
                        i18n("game_is_rebuilding").replace("%arena%", Game.this.name));
            } else {
                player.sendMessage(i18n("game_is_rebuilding").replace("%arena%", this.name));
            }
            return;
        }

        if ((status == GameStatus.RUNNING || status == GameStatus.GAME_END_CELEBRATING)
                && !getOriginalOrInheritedSpectatorJoin()) {
            if (isBungeeEnabled()) {
                BungeeUtils.movePlayerToBungeeServer(player, false);
                BungeeUtils.sendPlayerBungeeMessage(player,
                        i18n("game_already_running").replace("%arena%", Game.this.name));
            } else {
                player.sendMessage(i18n("game_already_running").replace("%arena%", this.name));
            }
            return;
        }

        if (players.size() >= calculatedMaxPlayers && status == GameStatus.WAITING) {
            if (Main.getPlayerGameProfile(player).canJoinFullGame()) {
                List<GamePlayer> withoutVIP = getPlayersWithoutVIP();

                if (withoutVIP.size() == 0) {
                    player.sendMessage(i18n("vip_game_is_full"));
                    return;
                }

                GamePlayer kickPlayer;
                if (withoutVIP.size() == 1) {
                    kickPlayer = withoutVIP.get(0);
                } else {
                    kickPlayer = withoutVIP.get(MiscUtils.randInt(0, players.size() - 1));
                }

                if (isBungeeEnabled()) {
                    BungeeUtils.sendPlayerBungeeMessage(kickPlayer.player,
                            i18n("game_kicked_by_vip").replace("%arena%", Game.this.name));
                } else {
                    kickPlayer.player.sendMessage(i18n("game_kicked_by_vip").replace("%arena%", this.name));
                }
                kickPlayer.changeGame(null);
            } else {
                if (isBungeeEnabled()) {
                    BungeeUtils.movePlayerToBungeeServer(player, false);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            BungeeUtils.sendPlayerBungeeMessage(player,
                                    i18n("game_is_full").replace("%arena%", Game.this.name));
                        }
                    }.runTaskLater(Main.getInstance(), 5L);
                } else {
                    player.sendMessage(i18n("game_is_full").replace("%arena%", this.name));
                }
                return;
            }
        }

        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
        gPlayer.changeGame(this);
    }

    public void leaveFromGame(Player player) {
        if (status == GameStatus.DISABLED) {
            return;
        }
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);

            if (gPlayer.getGame() == this) {
                gPlayer.changeGame(null);
                if (status == GameStatus.RUNNING || status == GameStatus.GAME_END_CELEBRATING) {
                    updateScoreboard();
                }
            }
        }
    }

    public CurrentTeam getCurrentTeamByTeam(Team team) {
        for (CurrentTeam current : teamsInGame) {
            if (current.teamInfo == team) {
                return current;
            }
        }
        return null;
    }

    public Team getFirstTeamThatIsntInGame() {
        for (Team team : teams) {
            if (getCurrentTeamByTeam(team) == null) {
                return team;
            }
        }
        return null;
    }

    public CurrentTeam getTeamWithLowestPlayers() {
        CurrentTeam lowest = null;

        for (CurrentTeam team : teamsInGame) {
            if (lowest == null) {
                lowest = team;
            }

            if (lowest.players.size() > team.players.size()) {
                lowest = team;
            }
        }

        return lowest;
    }

    public List<GamePlayer> getPlayersInTeam(Team team) {
        CurrentTeam currentTeam = null;
        for (CurrentTeam cTeam : teamsInGame) {
            if (cTeam.teamInfo == team) {
                currentTeam = cTeam;
            }
        }

        if (currentTeam != null) {
            return currentTeam.players;
        } else {
            return new ArrayList<>();
        }
    }

    private void internalTeamJoin(GamePlayer player, Team teamForJoin) {
        CurrentTeam current = null;
        for (CurrentTeam t : teamsInGame) {
            if (t.teamInfo == teamForJoin) {
                current = t;
                break;
            }
        }

        CurrentTeam cur = getPlayerTeam(player);
        BedwarsPlayerJoinTeamEvent event = new BedwarsPlayerJoinTeamEvent(current, player.player, this, cur);
        Main.getInstance().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (current == null) {
            current = new CurrentTeam(teamForJoin, this);
            org.bukkit.scoreboard.Team scoreboardTeam = gameScoreboard.getTeam(teamForJoin.name);
            if (scoreboardTeam == null) {
                scoreboardTeam = gameScoreboard.registerNewTeam(teamForJoin.name);
            }
            if (!Main.isLegacy()) {
                scoreboardTeam.setColor(teamForJoin.color.chatColor);
            } else {
                scoreboardTeam.setPrefix(teamForJoin.color.chatColor.toString());
            }
            scoreboardTeam.setAllowFriendlyFire(getOriginalOrInheritedFriendlyfire());

            current.setScoreboardTeam(scoreboardTeam);
        }
        if (cur == current) {
            player.player.sendMessage(
                    i18n("team_already_selected").replace("%team%", teamForJoin.color.chatColor + teamForJoin.name)
                            .replace("%players%", Integer.toString(current.players.size()))
                            .replaceAll("%maxplayers%", Integer.toString(current.teamInfo.maxPlayers)));
            return;
        }
        if (current.players.size() >= current.teamInfo.maxPlayers) {
            if (cur != null) {
                player.player.sendMessage(i18n("team_is_full_you_are_staying")
                        .replace("%team%", teamForJoin.color.chatColor + teamForJoin.name)
                        .replace("%oldteam%", cur.teamInfo.color.chatColor + cur.teamInfo.name));
            } else {
                player.player.sendMessage(
                        i18n("team_is_full").replace("%team%", teamForJoin.color.chatColor + teamForJoin.name));
            }
            return;
        }
        if (cur != null) {
            cur.players.remove(player);
            cur.getScoreboardTeam().removeEntry(player.player.getName());
            if (cur.players.isEmpty()) {
                teamsInGame.remove(cur);
                cur.getScoreboardTeam().unregister();
            }
        }
        current.players.add(player);
        current.getScoreboardTeam().addEntry(player.player.getName());
        player.player
                .sendMessage(i18n("team_selected").replace("%team%", teamForJoin.color.chatColor + teamForJoin.name)
                        .replace("%players%", Integer.toString(current.players.size()))
                        .replaceAll("%maxplayers%", Integer.toString(current.teamInfo.maxPlayers)));

        if (getOriginalOrInheritedAddWoolToInventoryOnJoin()) {
            int colorPosition = Main.getConfigurator().config.getInt("hotbar.color", 1);
            if (colorPosition >= 0 && colorPosition <= 8) {
                ItemStack stack = teamForJoin.color.getWool();
                ItemMeta stackMeta = stack.getItemMeta();
                stackMeta.setDisplayName(teamForJoin.color.chatColor + teamForJoin.name);
                stack.setItemMeta(stackMeta);
                player.player.getInventory().setItem(colorPosition, stack);
            }
        }

        if (getOriginalOrInheritedColoredLeatherByTeamInLobby()) {
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
            meta.setColor(teamForJoin.color.leatherColor);
            chestplate.setItemMeta(meta);
            player.player.getInventory().setChestplate(chestplate);
        }

        if (!teamsInGame.contains(current)) {
            teamsInGame.add(current);
        }
    }

    public void joinRandomTeam(GamePlayer player) {
        Team teamForJoin;
        if (teamsInGame.size() < 2) {
            teamForJoin = getFirstTeamThatIsntInGame();
        } else {
            CurrentTeam current = getTeamWithLowestPlayers();
            if (current.players.size() >= current.getMaxPlayers()) {
                teamForJoin = getFirstTeamThatIsntInGame();
            } else {
                teamForJoin = current.teamInfo;
            }
        }

        if (teamForJoin == null) {
            return;
        }

        internalTeamJoin(player, teamForJoin);
    }

    public Location makeSpectator(GamePlayer gamePlayer, boolean leaveItem) {
        Player player = gamePlayer.player;
        gamePlayer.isSpectator = true;
        gamePlayer.teleport(specSpawn, () -> {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setGameMode(GameMode.SPECTATOR);

            if (leaveItem) {
                int leavePosition = Main.getConfigurator().config.getInt("hotbar.leave", 8);
                if (leavePosition >= 0 && leavePosition <= 8) {
                    ItemStack leave = Main.getConfigurator().readDefinedItem("leavegame", "SLIME_BALL");
                    ItemMeta leaveMeta = leave.getItemMeta();
                    leaveMeta.setDisplayName(i18n("leave_from_game_item", false));
                    leave.setItemMeta(leaveMeta);
                    gamePlayer.player.getInventory().setItem(leavePosition, leave);
                }
            }
        });

        return specSpawn;
    }

    @SuppressWarnings("unchecked")
    public void makePlayerFromSpectator(GamePlayer gamePlayer) {
        Player player = gamePlayer.player;
        CurrentTeam currentTeam = getPlayerTeam(gamePlayer);

        if (gamePlayer.getGame() == this && currentTeam != null) {
            gamePlayer.isSpectator = false;
            gamePlayer.teleport(currentTeam.getTeamSpawn(), () -> {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.setGameMode(GameMode.SURVIVAL);

                if (Main.getConfigurator().config.getBoolean("respawn.protection-enabled", true)) {
                    RespawnProtection respawnProtection = addProtectedPlayer(player);
                    respawnProtection.runProtection();
                }

                if (gamePlayer.getGame().getOriginalOrInheritedPlayerRespawnItems()) {
                    List<ItemStack> givedGameStartItems = StackParser.parseAll((Collection<Object>) Main.getConfigurator().config
                            .getList("gived-player-respawn-items"));
                    if (givedGameStartItems != null) {
                        MiscUtils.giveItemsToPlayer(givedGameStartItems, player, currentTeam.getColor());
                    } else {
                        Debug.warn("You have wrongly configured gived-player-respawn-items!", true);
                    }
                }
            });
        }
    }

    public void setBossbarProgress(int count, int max) {
        double progress = (double) count / (double) max;
        statusbar.setProgress(progress);
        if (statusbar instanceof XPBar) {
            XPBar xpbar = (XPBar) statusbar;
            xpbar.setSeconds(count);
        }
    }

    @SuppressWarnings("unchecked")
    public void run() {
        // Phase 1: Check if game is running
        if (status == GameStatus.DISABLED) { // Game is not running, why cycle is still running?
            cancelTask();
            return;
        }
        BedwarsGameChangedStatusEvent statusE = new BedwarsGameChangedStatusEvent(this);
        // Phase 2: If this is first tick, prepare waiting lobby
        if (countdown == -1 && status == GameStatus.WAITING) {
            previousCountdown = countdown = pauseCountdown;
            previousStatus = GameStatus.WAITING;
            String title = i18nonly("bossbar_waiting");
            statusbar.setProgress(0);
            statusbar.setVisible(getOriginalOrInheritedLobbyBossbar());
            for (GamePlayer p : players) {
                statusbar.addPlayer(p.player);
            }
            if (statusbar instanceof BossBar) {
                BossBar bossbar = (BossBar) statusbar;
                bossbar.setMessage(title);
                if (bossbar instanceof BossBar19) {
                    BossBar19 bossbar19 = (BossBar19) bossbar;
                    bossbar19.setColor(lobbyBossBarColor != null ? lobbyBossBarColor
                            : BarColor.valueOf(Main.getConfigurator().config.getString("bossbar.lobby.color")));
                    bossbar19
                            .setStyle(BarStyle.valueOf(Main.getConfigurator().config.getString("bossbar.lobby.style")));
                }
            }
            if (teamSelectorInventory == null) {
                teamSelectorInventory = new TeamSelectorInventory(Main.getInstance(), this);
            }
            updateSigns();
        }

        // Phase 3: Prepare information about next tick for tick event and update
        // bossbar with scoreboard
        int nextCountdown = countdown;
        GameStatus nextStatus = status;

        if (status == GameStatus.WAITING) {
            // Game start item
            if (gameStartItem) {
                if (players.size() >= getMinPlayers()) {
                    for (GamePlayer player : players) {
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
                    && (getOriginalOrInheritedJoinRandomTeamAfterLobby() || teamsInGame.size() > 1)) {
                if (countdown == 0) {
                    nextCountdown = gameTime;
                    nextStatus = GameStatus.RUNNING;
                } else {
                    nextCountdown--;

                    if (countdown <= 10 && countdown >= 1 && countdown != previousCountdown) {
                        for (GamePlayer player : players) {
                            Title.send(player.player, ChatColor.YELLOW + Integer.toString(countdown), "");
                            Sounds.playSound(player.player, player.player.getLocation(),
                                    Main.getConfigurator().config.getString("sounds.on_countdown"), Sounds.UI_BUTTON_CLICK,
                                    1, 1);
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
                nextCountdown = POST_GAME_WAITING;
                nextStatus = GameStatus.GAME_END_CELEBRATING;
            } else {
                nextCountdown--;
            }
            setBossbarProgress(countdown, gameTime);
            updateScoreboardTimer();
        } else if (status == GameStatus.GAME_END_CELEBRATING) {
            if (countdown == 0) {
                nextStatus = GameStatus.REBUILDING;
                nextCountdown = 0;
            } else {
                nextCountdown--;
            }
            setBossbarProgress(countdown, POST_GAME_WAITING);
        }

        // Phase 4: Call Tick Event
        BedwarsGameTickEvent tick = new BedwarsGameTickEvent(this, previousCountdown, previousStatus, countdown, status,
                nextCountdown, nextStatus);
        Bukkit.getPluginManager().callEvent(tick);

        // Phase 5: Update Previous information
        previousCountdown = countdown;
        previousStatus = status;

        // Phase 6: Process tick
        // Phase 6.1: If status changed
        if (status != tick.getNextStatus()) {
            // Phase 6.1.1: Prepare game if next status is RUNNING
            if (tick.getNextStatus() == GameStatus.RUNNING) {
                BedwarsGameStartEvent startE = new BedwarsGameStartEvent(this);
                Main.getInstance().getServer().getPluginManager().callEvent(startE);
                Main.getInstance().getServer().getPluginManager().callEvent(statusE);

                if (startE.isCancelled()) {
                    tick.setNextCountdown(pauseCountdown);
                    tick.setNextStatus(GameStatus.WAITING);
                } else {

                    if (getOriginalOrInheritedJoinRandomTeamAfterLobby()) {
                        for (GamePlayer player : players) {
                            if (getPlayerTeam(player) == null) {
                                joinRandomTeam(player);
                            }
                        }
                    }

                    statusbar.setProgress(0);
                    statusbar.setVisible(getOriginalOrInheritedGameBossbar());
                    if (statusbar instanceof BossBar) {
                        BossBar bossbar = (BossBar) statusbar;
                        bossbar.setMessage(i18n("bossbar_running", false));
                        if (bossbar instanceof BossBar19) {
                            BossBar19 bossbar19 = (BossBar19) bossbar;
                            bossbar19.setColor(gameBossBarColor != null ? gameBossBarColor
                                    : BarColor.valueOf(Main.getConfigurator().config.getString("bossbar.game.color")));
                            bossbar19.setStyle(
                                    BarStyle.valueOf(Main.getConfigurator().config.getString("bossbar.game.style")));
                        }
                    }
                    if (teamSelectorInventory != null)
                        teamSelectorInventory.destroy();
                    teamSelectorInventory = null;
                    if (gameScoreboard.getObjective("lobby") != null) {
                        gameScoreboard.getObjective("lobby").unregister();
                    }
                    gameScoreboard.clearSlot(DisplaySlot.SIDEBAR);
                    updateSigns();
                    for (GameStore store : gameStore) {
                        LivingEntity villager = store.spawn();
                        if (villager != null) {
                            Main.registerGameEntity(villager, this);
                            EntityUtils.disableEntityAI(villager);
                            villager.getLocation().getWorld().getNearbyEntities(villager.getLocation(), 1, 1, 1).forEach(entity -> {
                                if (entity.getType() == villager.getType() && entity.getLocation().getBlock().equals(villager.getLocation().getBlock()) && !villager.equals(entity)) {
                                    entity.remove();
                                }
                            });
                        }
                    }

                    for (ItemSpawner spawner : spawners) {
                        UpgradeStorage storage = UpgradeRegistry.getUpgrade("spawner");
                        if (storage != null) {
                            storage.addUpgrade(this, spawner);
                        }
                    }

                    if (getOriginalOrInheritedSpawnerHolograms()) {
                        for (ItemSpawner spawner : spawners) {
                            if (spawner.getHologramEnabled()) {
                                Location loc = spawner.loc.clone().add(0,
                                        Main.getConfigurator().config.getDouble("spawner-holo-height", 0.25), 0);
                                Hologram holo = Main.getHologramManager().spawnHologram(getConnectedPlayers(), loc,
                                        spawner.type.getItemBoldName());
                                createdHolograms.add(holo);
                                if (getOriginalOrInheritedSpawnerHologramsCountdown()) {
                                    holo.addLine(spawner.type.getInterval() < 2 ? i18nonly("every_second_spawning")
                                            : i18nonly("countdown_spawning").replace("%seconds%",
                                            Integer.toString(spawner.type.getInterval())));
                                    countdownHolograms.put(spawner, holo);
                                }
                            }
                        }
                    }

                    String gameStartTitle = i18nonly("game_start_title");
                    String gameStartSubtitle = i18nonly("game_start_subtitle").replace("%arena%", this.name);
                    for (GamePlayer player : this.players) {
                        CurrentTeam team = getPlayerTeam(player);
                        player.player.getInventory().clear();
                        // Player still had armor on legacy versions
                        player.player.getInventory().setHelmet(null);
                        player.player.getInventory().setChestplate(null);
                        player.player.getInventory().setLeggings(null);
                        player.player.getInventory().setBoots(null);
                        Title.send(player.player, gameStartTitle, gameStartSubtitle);
                        if (team == null) {
                            makeSpectator(player, true);
                        } else {
                            player.teleport(team.teamInfo.spawn, () -> {
                                player.player.setGameMode(GameMode.SURVIVAL);
                                if (getOriginalOrInheritedGameStartItems()) {
                                    List<ItemStack> givedGameStartItems = StackParser.parseAll((Collection<Object>) Main.getConfigurator().config
                                            .getList("gived-game-start-items"));
                                    if (givedGameStartItems != null) {
                                        MiscUtils.giveItemsToPlayer(givedGameStartItems, player.player, team.getColor());
                                    } else {
                                        Debug.warn("You have wrongly configured gived-player-start-items!", true);
                                    }
                                }
                                SpawnEffects.spawnEffect(this, player.player, "game-effects.start");
                            });
                        }
                        Sounds.playSound(player.player, player.player.getLocation(),
                                Main.getConfigurator().config.getString("sounds.on_game_start"),
                                Sounds.ENTITY_PLAYER_LEVELUP, 1, 1);
                    }

                    if (getOriginalOrInheritedRemoveUnusedTargetBlocks()) {
                        for (Team team : teams) {
                            CurrentTeam ct = null;
                            for (CurrentTeam curt : teamsInGame) {
                                if (curt.teamInfo == team) {
                                    ct = curt;
                                    break;
                                }
                            }
                            if (ct == null) {
                                Location loc = team.bed;
                                Block block = team.bed.getBlock();
                                if (region.isBedBlock(block.getState())) {
                                    region.putOriginalBlock(block.getLocation(), block.getState());
                                    Block neighbor = region.getBedNeighbor(block);
                                    region.putOriginalBlock(neighbor.getLocation(), neighbor.getState());
                                    neighbor.setType(Material.AIR);
                                    block.setType(Material.AIR);
                                } else {
                                    region.putOriginalBlock(loc, block.getState());
                                    block.setType(Material.AIR);
                                }
                            }
                        }
                    }

                    for (CurrentTeam team : teamsInGame) {
                        Block block = team.getTargetBlock().getBlock();
                        if (block != null && "RESPAWN_ANCHOR".equals(block.getType().name())) { // don't break the game for older servers
                            new BukkitRunnable() {
                                public void run() {
                                    RespawnAnchor anchor = (RespawnAnchor) block.getBlockData();
                                    anchor.setCharges(0);
                                    block.setBlockData(anchor);
                                    if (getOriginalOrInheritedAnchorAutoFill()) {
                                        new BukkitRunnable() {
                                            public void run() {
                                                anchor.setCharges(anchor.getCharges() + 1);
                                                Sounds.playSound(team.getTargetBlock(), Main.getConfigurator().config.getString("target-block.respawn-anchor.sound.charge"), Sounds.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, 1);
                                                block.setBlockData(anchor);
                                                if (anchor.getCharges() >= anchor.getMaximumCharges()) {
                                                    updateScoreboard();
                                                    this.cancel();
                                                }
                                            }
                                        }.runTaskTimer(Main.getInstance(), 50L, 10L);
                                    }
                                }
                            }.runTask(Main.getInstance());
                        }
                    }

                    if (getOriginalOrInheritedHoloAboveBed()) {
                        for (CurrentTeam team : teamsInGame) {
                            Block bed = team.teamInfo.bed.getBlock();
                            Location loc = team.teamInfo.bed.clone().add(0.5, 1.5, 0.5);
                            boolean isBlockTypeBed = region.isBedBlock(bed.getState());
                            boolean isAnchor = "RESPAWN_ANCHOR".equals(bed.getType().name());
                            boolean isCake = bed.getType().name().contains("CAKE");
                            List<Player> enemies = getConnectedPlayers();
                            enemies.removeAll(team.getConnectedPlayers());
                            Hologram holo = Main.getHologramManager().spawnHologram(enemies, loc,
                                    i18nonly(isBlockTypeBed ? "destroy_this_bed" : (isAnchor ? "destroy_this_anchor" : (isCake ? "destroy_this_cake" : "destroy_this_target")))
                                            .replace("%teamcolor%", team.teamInfo.color.chatColor.toString()));
                            createdHolograms.add(holo);
                            team.setBedHolo(holo);
                            Hologram protectHolo = Main.getHologramManager().spawnHologram(team.getConnectedPlayers(), loc,
                                    i18nonly(isBlockTypeBed ? "protect_your_bed" : (isAnchor ? "protect_your_anchor" : (isCake ? "protect_your_cake" : "protect_your_target")))
                                            .replace("%teamcolor%", team.teamInfo.color.chatColor.toString()));
                            createdHolograms.add(protectHolo);
                            team.setProtectHolo(protectHolo);
                        }
                    }

                    // Check target blocks existence
                    for (CurrentTeam team : teamsInGame) {
                        Location targetLocation = team.getTargetBlock();
                        if (targetLocation.getBlock().getType() == Material.AIR) {
                            ItemStack stack = team.teamInfo.color.getWool();
                            Block placedBlock = targetLocation.getBlock();
                            placedBlock.setType(stack.getType());
                            if (!Main.isLegacy()) {
                                try {
                                    // The method is no longer in API, but in legacy versions exists
                                    Block.class.getMethod("setData", byte.class).invoke(placedBlock, (byte) stack.getDurability());
                                } catch (Exception e) {
                                }
                            }
                        }
                    }

                    if (Main.getVersionNumber() >= 115) {
                        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
                    }

                    BedwarsGameStartedEvent startedEvent = new BedwarsGameStartedEvent(this);
                    Main.getInstance().getServer().getPluginManager().callEvent(startedEvent);
                    Main.getInstance().getServer().getPluginManager().callEvent(statusE);
                    updateScoreboard();
                }
            }
            // Phase 6.2: If status is same as before
        } else {
            // Phase 6.2.1: On game tick (if not interrupted by a change of status)
            if (status == GameStatus.RUNNING && tick.getNextStatus() == GameStatus.RUNNING) {
                int runningTeams = 0;
                for (CurrentTeam t : teamsInGame) {
                    runningTeams += t.isAlive() ? 1 : 0;
                }
                if (runningTeams <= 1) {
                    if (runningTeams == 1) {
                        CurrentTeam winner = null;
                        for (CurrentTeam t : teamsInGame) {
                            if (t.isAlive()) {
                                winner = t;
                                String time = getFormattedTimeLeft(gameTime - countdown);
                                String message = i18n("team_win")
                                        .replace("%team%", TeamColor.fromApiColor(t.getColor()).chatColor + t.getName())
                                        .replace("%time%", time);
                                String subtitle = i18n("team_win", false)
                                        .replace("%team%", TeamColor.fromApiColor(t.getColor()).chatColor + t.getName())
                                        .replace("%time%", time);
                                boolean madeRecord = processRecord(t, gameTime - countdown);
                                for (GamePlayer player : players) {
                                    player.player.sendMessage(message);
                                    if (getPlayerTeam(player) == t) {
                                        Title.send(player.player, i18nonly("you_won"), subtitle);
                                        Main.depositPlayer(player.player, Main.getVaultWinReward());

                                        SpawnEffects.spawnEffect(this, player.player, "game-effects.end");

                                        if (Main.isPlayerStatisticsEnabled()) {
                                            PlayerStatistic statistic = Main.getPlayerStatisticsManager()
                                                    .getStatistic(player.player);
                                            statistic.setCurrentWins(statistic.getCurrentWins() + 1);
                                            statistic.setCurrentScore(statistic.getCurrentScore()
                                                    + Main.getConfigurator().config.getInt("statistics.scores.win", 50));

                                            if (madeRecord) {
                                                statistic.setCurrentScore(
                                                        statistic.getCurrentScore() + Main.getConfigurator().config
                                                                .getInt("statistics.scores.record", 100));
                                            }

                                            if (Main.isHologramsEnabled()) {
                                                Main.getHologramInteraction().updateHolograms(player.player);
                                            }

                                            if (Main.getConfigurator().config
                                                    .getBoolean("statistics.show-on-game-end")) {
                                                StatsCommand.sendStats(player.player, Main.getPlayerStatisticsManager().getStatistic(player.player));
                                            }

                                        }

                                        if (Main.getConfigurator().config.getBoolean("rewards.enabled")) {
                                            final Player pl = player.player;
                                            new BukkitRunnable() {

                                                @Override
                                                public void run() {
                                                    if (Main.isPlayerStatisticsEnabled()) {
                                                        PlayerStatistic statistic = Main.getPlayerStatisticsManager()
                                                                .getStatistic(player.player);
                                                        Game.this.dispatchRewardCommands("player-win", pl,
                                                                statistic.getCurrentScore());
                                                    } else {
                                                        Game.this.dispatchRewardCommands("player-win", pl, 0);
                                                    }
                                                }

                                            }.runTaskLater(Main.getInstance(), (2 + Game.POST_GAME_WAITING) * 20);
                                        }
                                    } else {
                                        Title.send(player.player, i18n("you_lost", false), subtitle);

                                        if (Main.isPlayerStatisticsEnabled() && Main.isHologramsEnabled()) {
                                            Main.getHologramInteraction().updateHolograms(player.player);
                                        }
                                    }
                                }
                                break;
                            }
                        }

                        BedwarsGameEndingEvent endingEvent = new BedwarsGameEndingEvent(this, winner);
                        Bukkit.getPluginManager().callEvent(endingEvent);
                        Main.getInstance().getServer().getPluginManager().callEvent(statusE);

                        tick.setNextCountdown(Game.POST_GAME_WAITING);
                        tick.setNextStatus(GameStatus.GAME_END_CELEBRATING);
                    } else {
                        tick.setNextStatus(GameStatus.REBUILDING);
                        tick.setNextCountdown(0);
                    }
                } else if (countdown != gameTime /* Prevent spawning resources on game start */) {
                    for (ItemSpawner spawner : spawners) {
                        CurrentTeam spawnerTeam = getCurrentTeamFromTeam(spawner.getTeam());
                        ItemSpawnerType type = spawner.type;
                        int cycle = type.getInterval();
                        /*
                         * Calculate resource spawn from elapsedTime, not from remainingTime/countdown
                         */
                        int elapsedTime = gameTime - countdown;

                        if (spawner.getHologramEnabled()) {
                            if (getOriginalOrInheritedSpawnerHolograms()
                                    && getOriginalOrInheritedSpawnerHologramsCountdown()
                                    && !spawner.spawnerIsFullHologram) {
                                if (cycle > 1) {
                                    int modulo = cycle - elapsedTime % cycle;
                                    countdownHolograms.get(spawner).setLine(1,
                                            i18nonly("countdown_spawning").replace("%seconds%", Integer.toString(modulo)));
                                } else if (spawner.rerenderHologram) {
                                    countdownHolograms.get(spawner).setLine(1, i18nonly("every_second_spawning"));
                                    spawner.rerenderHologram = false;
                                }
                            }
                        }

                        if (spawnerTeam != null) {
                            if (getOriginalOrInheritedStopTeamSpawnersOnDie() && (spawnerTeam.isDead())) {
                                continue;
                            }
                        }

                        if ((elapsedTime % cycle) == 0) {
                            int calculatedStack = 1;
                            double currentLevel = spawner.getCurrentLevel();
                            calculatedStack = (int) currentLevel;

                            /* Allow half level */
                            if ((currentLevel % 1) != 0) {
                                int a = elapsedTime / cycle;
                                if ((a % 2) == 0) {
                                    calculatedStack++;
                                }
                            }

                            BedwarsResourceSpawnEvent resourceSpawnEvent = new BedwarsResourceSpawnEvent(this, spawner,
                                    type.getStack(calculatedStack));
                            Main.getInstance().getServer().getPluginManager().callEvent(resourceSpawnEvent);

                            if (resourceSpawnEvent.isCancelled()) {
                                continue;
                            }

                            ItemStack resource = resourceSpawnEvent.getResource();

                            resource.setAmount(spawner.nextMaxSpawn(resource.getAmount(), countdownHolograms.get(spawner)));

                            if (resource.getAmount() > 0) {
                                Location loc = spawner.getLocation().clone().add(0, 0.05, 0);
                                Item item = loc.getWorld().dropItem(loc, resource);
                                double spread = type.getSpread();
                                if (spread != 1.0) {
                                    item.setVelocity(item.getVelocity().multiply(spread));
                                }
                                item.setPickupDelay(0);
                                spawner.add(item);
                            }
                        }
                    }
                }
            }
        }

        // Phase 7: Update status and countdown for next tick
        countdown = tick.getNextCountdown();
        status = tick.getNextStatus();

        // Phase 8: Check if game end celebrating started and remove title on bossbar
        if (status == GameStatus.GAME_END_CELEBRATING && previousStatus != status) {
            if (statusbar instanceof BossBar) {
                BossBar bossbar = (BossBar) statusbar;
                bossbar.setMessage(" ");
            }
        }

        // Phase 9: Check if status is rebuilding and rebuild game
        if (status == GameStatus.REBUILDING) {
            BedwarsGameEndEvent event = new BedwarsGameEndEvent(this);
            Main.getInstance().getServer().getPluginManager().callEvent(event);
            Main.getInstance().getServer().getPluginManager().callEvent(statusE);

            String message = i18n("game_end");
            for (GamePlayer player : (List<GamePlayer>) ((ArrayList<GamePlayer>) players).clone()) {
                player.player.sendMessage(message);
                player.changeGame(null);

                if (Main.getConfigurator().config.getBoolean("rewards.enabled")) {
                    final Player pl = player.player;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (Main.isPlayerStatisticsEnabled()) {
                                PlayerStatistic statistic = Main.getPlayerStatisticsManager()
                                        .getStatistic(player.player);
                                Game.this.dispatchRewardCommands("player-end-game", pl, statistic.getCurrentScore());
                            } else {
                                Game.this.dispatchRewardCommands("player-end-game", pl, 0);
                            }
                        }

                    }.runTaskLater(Main.getInstance(), 40);
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

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (Main.getConfigurator().config.getBoolean("bungee.serverRestart")) {
                            BedWarsServerRestartEvent serverRestartEvent = new BedWarsServerRestartEvent();
                            Main.getInstance().getServer().getPluginManager().callEvent(serverRestartEvent);

                            Main.getInstance().getServer()
                                    .dispatchCommand(Main.getInstance().getServer().getConsoleSender(), "restart");
                        } else if (Main.getConfigurator().config.getBoolean("bungee.serverStop")) {
                            Bukkit.shutdown();
                        }
                    }

                }.runTaskLater(Main.getInstance(), 30L);
            }
        }
    }

    public void rebuild() {
        teamsInGame.clear();
        activeSpecialItems.clear();
        activeDelays.clear();

        BedwarsPreRebuildingEvent preRebuildingEvent = new BedwarsPreRebuildingEvent(this);
        Main.getInstance().getServer().getPluginManager().callEvent(preRebuildingEvent);

        for (ItemSpawner spawner : spawners) {
            spawner.currentLevel = spawner.startLevel;
            spawner.spawnedItems.clear();
        }
        for (GameStore store : gameStore) {
            LivingEntity villager = store.kill();
            if (villager != null) {
                Main.unregisterGameEntity(villager);
            }
        }

        region.regen();
        // Remove items
        for (Entity e : this.world.getEntities()) {
            if (GameCreator.isInArea(e.getLocation(), pos1, pos2)) {
                if (e instanceof Item) {
                    Chunk chunk = e.getLocation().getChunk();
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }
                    e.remove();
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
        for (Entity entity : Main.getGameEntities(this)) {
            Chunk chunk = entity.getLocation().getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            entity.remove();
            Main.unregisterGameEntity(entity);
        }

        // Holograms destroy
        for (Hologram holo : createdHolograms) {
            holo.destroy();
        }
        createdHolograms.clear();
        countdownHolograms.clear();

        UpgradeRegistry.clearAll(this);

        BedwarsPostRebuildingEvent postRebuildingEvent = new BedwarsPostRebuildingEvent(this);
        Main.getInstance().getServer().getPluginManager().callEvent(postRebuildingEvent);

        this.status = this.afterRebuild;
        this.countdown = -1;
        updateSigns();
        cancelTask();

    }

    public boolean processRecord(CurrentTeam t, int wonTime) {
        int time = Main.getConfigurator().recordConfig.getInt("record." + this.getName() + ".time", Integer.MAX_VALUE);
        if (time > wonTime) {
            Main.getConfigurator().recordConfig.set("record." + this.getName() + ".time", wonTime);
            Main.getConfigurator().recordConfig.set("record." + this.getName() + ".team",
                    t.teamInfo.color.chatColor + t.teamInfo.name);
            List<String> winners = new ArrayList<String>();
            for (GamePlayer p : t.players) {
                winners.add(p.player.getName());
            }
            Main.getConfigurator().recordConfig.set("record." + this.getName() + ".winners", winners);
            try {
                Main.getConfigurator().recordConfig.save(Main.getConfigurator().recordFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void runTask() {
        if (task != null) {
            if (Bukkit.getScheduler().isQueued(task.getTaskId())) {
                task.cancel();
            }
            task = null;
        }
        task = (new BukkitRunnable() {

            public void run() {
                Game.this.run();
            }

        }.runTaskTimer(Main.getInstance(), 0, 20));
    }

    private void cancelTask() {
        if (task != null) {
            if (Bukkit.getScheduler().isQueued(task.getTaskId())) {
                task.cancel();
            }
            task = null;
        }
    }

    public void selectTeam(GamePlayer playerGameProfile, String displayName) {
        if (status == GameStatus.WAITING) {
            displayName = ChatColor.stripColor(displayName);
            playerGameProfile.player.closeInventory();
            for (Team team : teams) {
                if (displayName.equals(team.name)) {
                    internalTeamJoin(playerGameProfile, team);
                    break;
                }
            }
        }
    }

    public void updateScoreboard() {
        if (!getOriginalOrInheritedScoreaboard()) {
            return;
        }

        Objective obj = this.gameScoreboard.getObjective("display");
        if (obj == null) {
            obj = this.gameScoreboard.registerNewObjective("display", "dummy");
        }

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(this.formatScoreboardTitle());

        for (CurrentTeam team : teamsInGame) {
            this.gameScoreboard.resetScores(this.formatScoreboardTeam(team, false, false));
            this.gameScoreboard.resetScores(this.formatScoreboardTeam(team, false, true));
            this.gameScoreboard.resetScores(this.formatScoreboardTeam(team, true, false));

            Score score = obj.getScore(this.formatScoreboardTeam(team, !team.isBed, team.isBed && "RESPAWN_ANCHOR".equals(team.teamInfo.bed.getBlock().getType().name()) && Player116ListenerUtils.isAnchorEmpty(team.teamInfo.bed.getBlock())));
            score.setScore(team.players.size());
        }

        for (GamePlayer player : players) {
            player.player.setScoreboard(gameScoreboard);
        }
    }

    private String formatScoreboardTeam(CurrentTeam team, boolean destroy, boolean empty) {
        if (team == null) {
            return "";
        }

        return Main.getConfigurator().config.getString("scoreboard.teamTitle")
                .replace("%color%", team.teamInfo.color.chatColor.toString()).replace("%team%", team.teamInfo.name)
                .replace("%bed%", destroy ? bedLostString() : (empty ? anchorEmptyString() : bedExistString()));
    }

    public static String bedExistString() {
        return Main.getConfigurator().config.getString("scoreboard.bedExists");
    }

    public static String bedLostString() {
        return Main.getConfigurator().config.getString("scoreboard.bedLost");
    }

    public static String anchorEmptyString() {
        return Main.getConfigurator().config.getString("scoreboard.anchorEmpty");
    }

    private void updateScoreboardTimer() {
        if (this.status != GameStatus.RUNNING || !getOriginalOrInheritedScoreaboard()) {
            return;
        }

        Objective obj = this.gameScoreboard.getObjective("display");
        if (obj == null) {
            obj = this.gameScoreboard.registerNewObjective("display", "dummy");
        }

        obj.setDisplayName(this.formatScoreboardTitle());

        for (GamePlayer player : players) {
            player.player.setScoreboard(gameScoreboard);
        }
    }

    private String formatScoreboardTitle() {
        return Main.getConfigurator().config.getString("scoreboard.title").replace("%game%", this.name)
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

        min = (int) Math.floor(countdown / 60);
        sec = countdown % 60;

        minStr = (min < 10) ? "0" + min : String.valueOf(min);
        secStr = (sec < 10) ? "0" + sec : String.valueOf(sec);

        return minStr + ":" + secStr;
    }

    public void updateSigns() {
        final FileConfiguration config = Main.getConfigurator().config;
        final List<SignBlock> gameSigns = Main.getSignManager().getSignsForName(this.name);

        if (gameSigns.isEmpty()) {
            return;
        }

        String statusLine = "";
        String playersLine = "";
        Material blockBehindMaterial = Material.RED_STAINED_GLASS;

        switch (status) {
            case DISABLED:
                statusLine = i18nonly("sign_status_disabled");
                playersLine = i18nonly("sign_status_disabled_players");
                blockBehindMaterial = MiscUtils.getMaterialFromString(config.getString("sign.block-behind.game-disabled"), "RED_STAINED_GLASS");
                break;
            case REBUILDING:
                statusLine = i18nonly("sign_status_rebuilding");
                playersLine = i18nonly("sign_status_rebuilding_players");
                blockBehindMaterial = MiscUtils.getMaterialFromString(config.getString("sign.block-behind.game-disabled"), "RED_STAINED_GLASS");
                break;
            case RUNNING:
            case GAME_END_CELEBRATING:
                statusLine = i18nonly("sign_status_running");
                playersLine = i18nonly("sign_status_running_players");
                blockBehindMaterial = MiscUtils.getMaterialFromString(config.getString("sign.block-behind.in-game"), "GREEN_STAINED_GLASS");
                break;
            case WAITING:
                statusLine = i18nonly("sign_status_waiting");
                playersLine = i18nonly("sign_status_waiting_players");
                blockBehindMaterial = MiscUtils.getMaterialFromString(config.getString("sign.block-behind.waiting"), "ORANGE_STAINED_GLASS");
                break;
        }

        playersLine = playersLine.replace("%players%", Integer.toString(players.size()));
        playersLine = playersLine.replace("%maxplayers%", Integer.toString(calculatedMaxPlayers));

        final List<String> texts = new ArrayList<>(Main.getConfigurator().config.getStringList("sign"));

        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            texts.set(i, text.replaceAll("%arena%", this.getName()).replaceAll("%status%", statusLine)
                    .replaceAll("%players%", playersLine));
        }

        for (SignBlock sign : gameSigns) {
            if (sign.getLocation().getChunk().isLoaded()) {
                final Block block = sign.getLocation().getBlock();
                if (block.getState() instanceof Sign) {
                    Sign state = (Sign) block.getState();
                    for (int i = 0; i < texts.size() && i < 4; i++) {
                        state.setLine(i, texts.get(i));
                    }
                    state.update();
                }

                if (config.getBoolean("sign.block-behind.enabled", false)) {
                    final Optional<Block> optionalBlock = sign.getBlockBehindSign();
                    if (optionalBlock.isPresent()) {
                        final Block glassBlock = optionalBlock.get();
                        glassBlock.setType(blockBehindMaterial);
                    }
                }
            }
        }
    }

    private void updateLobbyScoreboard() {
        if (status != GameStatus.WAITING || !getOriginalOrInheritedLobbyScoreaboard()) {
            return;
        }
        Objective obj = gameScoreboard.getObjective("lobby");
        if (obj == null) {
            obj = gameScoreboard.registerNewObjective("lobby", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.setDisplayName(this.formatLobbyScoreboardString(
                    Main.getConfigurator().config.getString("lobby-scoreboard.title", "§eBEDWARS")));
        }

        gameScoreboard.getEntries().forEach(gameScoreboard::resetScores);

        List<String> rows = Main.getConfigurator().config.getStringList("lobby-scoreboard.content");
        int rowMax = rows.size();
        if (rows.isEmpty()) {
            return;
        }

        for (String row : rows) {
            if (row.trim().equals("")) {
                for (int i = 0; i <= rowMax; i++) {
                    row += " ";
                }
            }

            Score score = obj.getScore(this.formatLobbyScoreboardString(row));
            score.setScore(rowMax);
            rowMax--;
        }

        players.forEach(player -> player.player.setScoreboard(gameScoreboard));
    }

    private String formatLobbyScoreboardString(String str) {
        String finalStr = str;

        finalStr = finalStr.replace("%arena%", name);
        finalStr = finalStr.replace("%players%", String.valueOf(players.size()));
        finalStr = finalStr.replace("%maxplayers%", String.valueOf(calculatedMaxPlayers));

        return finalStr;
    }

    @Override
    public void selectPlayerTeam(Player player, org.screamingsandals.bedwars.api.Team team) {
        if (!Main.isPlayerInGame(player)) {
            return;
        }
        GamePlayer profile = Main.getPlayerGameProfile(player);
        if (profile.getGame() != this) {
            return;
        }

        selectTeam(profile, team.getName());
    }

    @Override
    public World getGameWorld() {
        return world;
    }

    @Override
    public Location getSpectatorSpawn() {
        return specSpawn;
    }

    @Override
    public int countConnectedPlayers() {
        return players.size();
    }

    @Override
    public List<Player> getConnectedPlayers() {
        List<Player> playerList = new ArrayList<>();
        for (GamePlayer player : players) {
            playerList.add(player.player);
        }
        return playerList;
    }

    @Override
    public List<org.screamingsandals.bedwars.api.Team> getAvailableTeams() {
        return new ArrayList<>(teams);
    }

    @Override
    public List<RunningTeam> getRunningTeams() {
        return new ArrayList<>(teamsInGame);
    }

    @Override
    public RunningTeam getTeamOfPlayer(Player player) {
        if (!Main.isPlayerInGame(player)) {
            return null;
        }
        return getPlayerTeam(Main.getPlayerGameProfile(player));
    }

    @Override
    public boolean isLocationInArena(Location location) {
        return GameCreator.isInArea(location, pos1, pos2);
    }

    @Override
    public World getLobbyWorld() {
        return lobbySpawn.getWorld();
    }

    @Override
    public int getLobbyCountdown() {
        return pauseCountdown;
    }

    @Override
    public CurrentTeam getTeamOfChest(Location location) {
        for (CurrentTeam team : teamsInGame) {
            if (team.isTeamChestRegistered(location)) {
                return team;
            }
        }
        return null;
    }

    @Override
    public CurrentTeam getTeamOfChest(Block block) {
        for (CurrentTeam team : teamsInGame) {
            if (team.isTeamChestRegistered(block)) {
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
    public int countRunningTeams() {
        return teamsInGame.size();
    }

    @Override
    public boolean isPlayerInAnyTeam(Player player) {
        return getTeamOfPlayer(player) != null;
    }

    @Override
    public boolean isPlayerInTeam(Player player, RunningTeam team) {
        return getTeamOfPlayer(player) == team;
    }

    @Override
    public int countTeamChests() {
        int total = 0;
        for (CurrentTeam team : teamsInGame) {
            total += team.countTeamChests();
        }
        return total;
    }

    @Override
    public int countTeamChests(RunningTeam team) {
        return team.countTeamChests();
    }

    @Override
    public List<SpecialItem> getActivedSpecialItems() {
        return new ArrayList<>(activeSpecialItems);
    }

    @Override
    public List<SpecialItem> getActivedSpecialItems(Class<? extends SpecialItem> type) {
        List<SpecialItem> items = new ArrayList<>();
        for (SpecialItem item : activeSpecialItems) {
            if (type.isInstance(item)) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public List<SpecialItem> getActivedSpecialItemsOfTeam(org.screamingsandals.bedwars.api.Team team) {
        List<SpecialItem> items = new ArrayList<>();
        for (SpecialItem item : activeSpecialItems) {
            if (item.getTeam() == team) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public List<SpecialItem> getActivedSpecialItemsOfTeam(org.screamingsandals.bedwars.api.Team team,
                                                          Class<? extends SpecialItem> type) {
        List<SpecialItem> items = new ArrayList<>();
        for (SpecialItem item : activeSpecialItems) {
            if (type.isInstance(item) && item.getTeam() == team) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public SpecialItem getFirstActivedSpecialItemOfTeam(org.screamingsandals.bedwars.api.Team team) {
        for (SpecialItem item : activeSpecialItems) {
            if (item.getTeam() == team) {
                return item;
            }
        }
        return null;
    }

    @Override
    public SpecialItem getFirstActivedSpecialItemOfTeam(org.screamingsandals.bedwars.api.Team team,
                                                        Class<? extends SpecialItem> type) {
        for (SpecialItem item : activeSpecialItems) {
            if (item.getTeam() == team && type.isInstance(item)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public List<SpecialItem> getActivedSpecialItemsOfPlayer(Player player) {
        List<SpecialItem> items = new ArrayList<>();
        for (SpecialItem item : activeSpecialItems) {
            if (item.getPlayer() == player) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public List<SpecialItem> getActivedSpecialItemsOfPlayer(Player player, Class<? extends SpecialItem> type) {
        List<SpecialItem> items = new ArrayList<>();
        for (SpecialItem item : activeSpecialItems) {
            if (item.getPlayer() == player && type.isInstance(item)) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public SpecialItem getFirstActivedSpecialItemOfPlayer(Player player) {
        for (SpecialItem item : activeSpecialItems) {
            if (item.getPlayer() == player) {
                return item;
            }
        }
        return null;
    }

    @Override
    public SpecialItem getFirstActivedSpecialItemOfPlayer(Player player, Class<? extends SpecialItem> type) {
        for (SpecialItem item : activeSpecialItems) {
            if (item.getPlayer() == player && type.isInstance(item)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void registerSpecialItem(SpecialItem item) {
        if (!activeSpecialItems.contains(item)) {
            activeSpecialItems.add(item);
        }
    }

    @Override
    public void unregisterSpecialItem(SpecialItem item) {
        if (activeSpecialItems.contains(item)) {
            activeSpecialItems.remove(item);
        }
    }

    @Override
    public boolean isRegisteredSpecialItem(SpecialItem item) {
        return activeSpecialItems.contains(item);
    }

    @Override
    public List<DelayFactory> getActiveDelays() {
        return new ArrayList<>(activeDelays);
    }

    @Override
    public List<DelayFactory> getActiveDelaysOfPlayer(Player player) {
        List<DelayFactory> delays = new ArrayList<>();
        for (DelayFactory delay : activeDelays) {
            if (delay.getPlayer() == player) {
                delays.add(delay);
            }
        }
        return delays;
    }

    @Override
    public DelayFactory getActiveDelay(Player player, Class<? extends SpecialItem> specialItem) {
        for (DelayFactory delayFactory : getActiveDelaysOfPlayer(player)) {
            if (specialItem.isInstance(delayFactory.getSpecialItem())) {
                return delayFactory;
            }
        }
        return null;
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
    public boolean isDelayActive(Player player, Class<? extends SpecialItem> specialItem) {
        for (DelayFactory delayFactory : getActiveDelaysOfPlayer(player)) {
            if (specialItem.isInstance(delayFactory.getSpecialItem())) {
                return delayFactory.getDelayActive();
            }
        }
        return false;
    }

    public InGameConfigBooleanConstants getCompassEnabled() {
        return compassEnabled;
    }

    public void setCompassEnabled(InGameConfigBooleanConstants compassEnabled) {
        this.compassEnabled = compassEnabled;
    }

    public InGameConfigBooleanConstants getJoinRandomTeamAfterLobby() {
        return joinRandomTeamAfterLobby;
    }

    public void setJoinRandomTeamAfterLobby(InGameConfigBooleanConstants joinRandomTeamAfterLobby) {
        this.joinRandomTeamAfterLobby = joinRandomTeamAfterLobby;
    }

    public InGameConfigBooleanConstants getJoinRandomTeamOnJoin() {
        return joinRandomTeamOnJoin;
    }

    public void setJoinRandomTeamOnJoin(InGameConfigBooleanConstants joinRandomTeamOnJoin) {
        this.joinRandomTeamOnJoin = joinRandomTeamOnJoin;
    }

    public InGameConfigBooleanConstants getAddWoolToInventoryOnJoin() {
        return addWoolToInventoryOnJoin;
    }

    public void setAddWoolToInventoryOnJoin(InGameConfigBooleanConstants addWoolToInventoryOnJoin) {
        this.addWoolToInventoryOnJoin = addWoolToInventoryOnJoin;
    }

    public InGameConfigBooleanConstants getPreventKillingVillagers() {
        return preventKillingVillagers;
    }

    public void setPreventKillingVillagers(InGameConfigBooleanConstants preventKillingVillagers) {
        this.preventKillingVillagers = preventKillingVillagers;
    }

    public InGameConfigBooleanConstants getPlayerDrops() {
        return playerDrops;
    }

    public void setPlayerDrops(InGameConfigBooleanConstants playerDrops) {
        this.playerDrops = playerDrops;
    }

    public InGameConfigBooleanConstants getFriendlyfire() {
        return friendlyfire;
    }

    public void setFriendlyfire(InGameConfigBooleanConstants friendlyfire) {
        this.friendlyfire = friendlyfire;
    }

    public InGameConfigBooleanConstants getColoredLeatherByTeamInLobby() {
        return coloredLeatherByTeamInLobby;
    }

    public void setColoredLeatherByTeamInLobby(InGameConfigBooleanConstants coloredLeatherByTeamInLobby) {
        this.coloredLeatherByTeamInLobby = coloredLeatherByTeamInLobby;
    }

    public InGameConfigBooleanConstants getKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(InGameConfigBooleanConstants keepInventory) {
        this.keepInventory = keepInventory;
    }

    public InGameConfigBooleanConstants getCrafting() {
        return crafting;
    }

    public void setCrafting(InGameConfigBooleanConstants crafting) {
        this.crafting = crafting;
    }

    @Override
    public boolean getOriginalOrInheritedCompassEnabled() {
        return compassEnabled.isOriginal() ? compassEnabled.getValue()
                : Main.getConfigurator().config.getBoolean(COMPASS_ENABLED);
    }

    @Override
    public boolean getOriginalOrInheritedJoinRandomTeamAfterLobby() {
        return joinRandomTeamAfterLobby.isOriginal() ? joinRandomTeamAfterLobby.getValue()
                : Main.getConfigurator().config.getBoolean(JOIN_RANDOM_TEAM_AFTER_LOBBY);
    }

    @Override
    public boolean getOriginalOrInheritedJoinRandomTeamOnJoin() {
        return joinRandomTeamOnJoin.isOriginal() ? joinRandomTeamOnJoin.getValue()
                : Main.getConfigurator().config.getBoolean(JOIN_RANDOM_TEAM_ON_JOIN);
    }

    @Override
    public boolean getOriginalOrInheritedAddWoolToInventoryOnJoin() {
        return addWoolToInventoryOnJoin.isOriginal() ? addWoolToInventoryOnJoin.getValue()
                : Main.getConfigurator().config.getBoolean(ADD_WOOL_TO_INVENTORY_ON_JOIN);
    }

    @Override
    public boolean getOriginalOrInheritedPreventKillingVillagers() {
        return preventKillingVillagers.isOriginal() ? preventKillingVillagers.getValue()
                : Main.getConfigurator().config.getBoolean(PREVENT_KILLING_VILLAGERS);
    }

    @Override
    public boolean getOriginalOrInheritedPlayerDrops() {
        return playerDrops.isOriginal() ? playerDrops.getValue()
                : Main.getConfigurator().config.getBoolean(PLAYER_DROPS);
    }

    @Override
    public boolean getOriginalOrInheritedFriendlyfire() {
        return friendlyfire.isOriginal() ? friendlyfire.getValue()
                : Main.getConfigurator().config.getBoolean(FRIENDLY_FIRE);
    }

    @Override
    public boolean getOriginalOrInheritedColoredLeatherByTeamInLobby() {
        return coloredLeatherByTeamInLobby.isOriginal() ? coloredLeatherByTeamInLobby.getValue()
                : Main.getConfigurator().config.getBoolean(COLORED_LEATHER_BY_TEAM_IN_LOBBY);
    }

    @Override
    public boolean getOriginalOrInheritedKeepInventory() {
        return keepInventory.isOriginal() ? keepInventory.getValue()
                : Main.getConfigurator().config.getBoolean(KEEP_INVENTORY);
    }

    @Override
    public boolean getOriginalOrInheritedCrafting() {
        return crafting.isOriginal() ? crafting.getValue() : Main.getConfigurator().config.getBoolean(CRAFTING);
    }

    @Override
    public InGameConfigBooleanConstants getLobbyBossbar() {
        return lobbybossbar;
    }

    @Override
    public boolean getOriginalOrInheritedLobbyBossbar() {
        return lobbybossbar.isOriginal() ? lobbybossbar.getValue()
                : Main.getConfigurator().config.getBoolean(GLOBAL_LOBBY_BOSSBAR);
    }

    @Override
    public InGameConfigBooleanConstants getGameBossbar() {
        return gamebossbar;
    }

    @Override
    public boolean getOriginalOrInheritedGameBossbar() {
        return gamebossbar.isOriginal() ? gamebossbar.getValue()
                : Main.getConfigurator().config.getBoolean(GLOBAL_GAME_BOSSBAR);
    }

    @Override
    public InGameConfigBooleanConstants getScoreboard() {
        return ascoreboard;
    }

    @Override
    public boolean getOriginalOrInheritedScoreaboard() {
        return ascoreboard.isOriginal() ? ascoreboard.getValue()
                : Main.getConfigurator().config.getBoolean(GLOBAL_SCOREBOARD);
    }

    @Override
    public InGameConfigBooleanConstants getLobbyScoreboard() {
        return lobbyscoreboard;
    }

    @Override
    public boolean getOriginalOrInheritedLobbyScoreaboard() {
        return lobbyscoreboard.isOriginal() ? lobbyscoreboard.getValue()
                : Main.getConfigurator().config.getBoolean(GLOBAL_LOBBY_SCOREBOARD);
    }

    public void setLobbyBossbar(InGameConfigBooleanConstants lobbybossbar) {
        this.lobbybossbar = lobbybossbar;
    }

    public void setGameBossbar(InGameConfigBooleanConstants gamebossbar) {
        this.gamebossbar = gamebossbar;
    }

    public void setAscoreboard(InGameConfigBooleanConstants ascoreboard) {
        this.ascoreboard = ascoreboard;
    }

    public void setLobbyScoreboard(InGameConfigBooleanConstants lobbyscoreboard) {
        this.lobbyscoreboard = lobbyscoreboard;
    }

    public void setPreventSpawningMobs(InGameConfigBooleanConstants preventSpawningMobs) {
        this.preventSpawningMobs = preventSpawningMobs;
    }

    @Override
    public InGameConfigBooleanConstants getPreventSpawningMobs() {
        return preventSpawningMobs;
    }

    @Override
    public boolean getOriginalOrInheritedPreventSpawningMobs() {
        return preventSpawningMobs.isOriginal() ? preventSpawningMobs.getValue()
                : Main.getConfigurator().config.getBoolean(PREVENT_SPAWNING_MOBS);
    }

    @Override
    public ArenaTime getArenaTime() {
        return arenaTime;
    }

    public void setArenaTime(ArenaTime arenaTime) {
        this.arenaTime = arenaTime;
    }

    @Override
    public WeatherType getArenaWeather() {
        return arenaWeather;
    }

    public void setArenaWeather(WeatherType arenaWeather) {
        this.arenaWeather = arenaWeather;
    }

    @Override
    public BarColor getLobbyBossBarColor() {
        return this.lobbyBossBarColor;
    }

    public void setLobbyBossBarColor(BarColor color) {
        this.lobbyBossBarColor = color;
    }

    @Override
    public BarColor getGameBossBarColor() {
        return this.gameBossBarColor;
    }

    public void setGameBossBarColor(BarColor color) {
        this.gameBossBarColor = color;
    }

    @Override
    public InGameConfigBooleanConstants getSpawnerHolograms() {
        return spawnerHolograms;
    }

    @Override
    public boolean getOriginalOrInheritedSpawnerHolograms() {
        return spawnerHolograms.isOriginal() ? spawnerHolograms.getValue()
                : Main.getConfigurator().config.getBoolean(SPAWNER_HOLOGRAMS);
    }

    public void setSpawnerHolograms(InGameConfigBooleanConstants spawnerHolograms) {
        this.spawnerHolograms = spawnerHolograms;
    }

    @Override
    public List<org.screamingsandals.bedwars.api.game.ItemSpawner> getItemSpawners() {
        return new ArrayList<>(spawners);
    }

    @Override
    public InGameConfigBooleanConstants getSpawnerDisableMerge() {
        return spawnerDisableMerge;
    }

    @Override
    public boolean getOriginalOrInheritedSpawnerDisableMerge() {
        return spawnerDisableMerge.isOriginal() ? spawnerDisableMerge.getValue()
                : Main.getConfigurator().config.getBoolean(SPAWNER_DISABLE_MERGE);
    }

    public void setSpawnerDisableMerge(InGameConfigBooleanConstants spawnerDisableMerge) {
        this.spawnerDisableMerge = spawnerDisableMerge;
    }

    public void dispatchRewardCommands(String type, Player player, int score) {
        if (!Main.getConfigurator().config.getBoolean("rewards.enabled")) {
            return;
        }

        List<String> list = Main.getConfigurator().config.getStringList("rewards." + type);
        for (String command : list) {
            command = command.replaceAll("\\{player}", player.getName());
            command = command.replaceAll("\\{score}", Integer.toString(score));
            command = command.startsWith("/") ? command.substring(1) : command;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    @Override
    public InGameConfigBooleanConstants getGameStartItems() {
        return gameStartItems;
    }

    @Override
    public boolean getOriginalOrInheritedGameStartItems() {
        return gameStartItems.isOriginal() ? gameStartItems.getValue()
                : Main.getConfigurator().config.getBoolean(GAME_START_ITEMS);
    }

    @Override
    public InGameConfigBooleanConstants getPlayerRespawnItems() {
        return playerRespawnItems;
    }

    @Override
    public boolean getOriginalOrInheritedPlayerRespawnItems() {
        return playerRespawnItems.isOriginal() ? playerRespawnItems.getValue()
                : Main.getConfigurator().config.getBoolean(PLAYER_RESPAWN_ITEMS);
    }

    public void setGameStartItems(InGameConfigBooleanConstants gameStartItems) {
        this.gameStartItems = gameStartItems;
    }

    public void setPlayerRespawnItems(InGameConfigBooleanConstants playerRespawnItems) {
        this.playerRespawnItems = playerRespawnItems;
    }

    @Override
    public InGameConfigBooleanConstants getSpawnerHologramsCountdown() {
        return spawnerHologramsCountdown;
    }

    @Override
    public boolean getOriginalOrInheritedSpawnerHologramsCountdown() {
        return spawnerHologramsCountdown.isOriginal() ? spawnerHologramsCountdown.getValue()
                : Main.getConfigurator().config.getBoolean(SPAWNER_HOLOGRAMS_COUNTDOWN);
    }

    public void setSpawnerHologramsCountdown(InGameConfigBooleanConstants spawnerHologramsCountdown) {
        this.spawnerHologramsCountdown = spawnerHologramsCountdown;
    }

    @Override
    public InGameConfigBooleanConstants getDamageWhenPlayerIsNotInArena() {
        return damageWhenPlayerIsNotInArena;
    }

    @Override
    public boolean getOriginalOrInheritedDamageWhenPlayerIsNotInArena() {
        return damageWhenPlayerIsNotInArena.isOriginal() ? damageWhenPlayerIsNotInArena.getValue()
                : Main.getConfigurator().config.getBoolean(DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA);
    }

    public void setDamageWhenPlayerIsNotInArena(InGameConfigBooleanConstants damageWhenPlayerIsNotInArena) {
        this.damageWhenPlayerIsNotInArena = damageWhenPlayerIsNotInArena;
    }

    @Override
    public InGameConfigBooleanConstants getRemoveUnusedTargetBlocks() {
        return removeUnusedTargetBlocks;
    }

    @Override
    public boolean getOriginalOrInheritedRemoveUnusedTargetBlocks() {
        return removeUnusedTargetBlocks.isOriginal() ? removeUnusedTargetBlocks.getValue()
                : Main.getConfigurator().config.getBoolean(REMOVE_UNUSED_TARGET_BLOCKS);
    }

    public void setRemoveUnusedTargetBlocks(InGameConfigBooleanConstants removeUnusedTargetBlocks) {
        this.removeUnusedTargetBlocks = removeUnusedTargetBlocks;
    }

    @Override
    public InGameConfigBooleanConstants getAllowBlockFalling() {
        return allowBlockFalling;
    }

    @Override
    public boolean getOriginalOrInheritedAllowBlockFalling() {
        return allowBlockFalling.isOriginal() ? allowBlockFalling.getValue()
                : Main.getConfigurator().config.getBoolean(ALLOW_BLOCK_FALLING);
    }

    public void setAllowBlockFalling(InGameConfigBooleanConstants allowBlockFalling) {
        this.allowBlockFalling = allowBlockFalling;
    }

    @Override
    public void selectPlayerRandomTeam(Player player) {
        joinRandomTeam(Main.getPlayerGameProfile(player));
    }

    @Override
    public StatusBar getStatusBar() {
        return statusbar;
    }

    public void kickAllPlayers() {
        for (Player player : getConnectedPlayers()) {
            leaveFromGame(player);
        }
    }

    public static boolean isBungeeEnabled() {
        return Main.getConfigurator().config.getBoolean("bungee.enabled");
    }

    @Override
    public boolean getBungeeEnabled() {
        return Main.getConfigurator().config.getBoolean("bungee.enabled");
    }

    @Override
    public boolean isEntityShop(Entity entity) {
        if (Main.getConfigurator().config.getBoolean("shop.citizens-enabled", false))
            return false;

        for (GameStore store : gameStore) {
            if (store.getEntity().equals(entity)) {
                return true;
            }
        }
        return false;
    }

    public RespawnProtection addProtectedPlayer(Player player) {
        int time = Main.getConfigurator().config.getInt("respawn.protection-time", 10);

        RespawnProtection respawnProtection = new RespawnProtection(this, player, time);
        respawnProtectionMap.put(player, respawnProtection);

        return respawnProtection;
    }

    public void removeProtectedPlayer(Player player) {
        RespawnProtection respawnProtection = respawnProtectionMap.get(player);
        if (respawnProtection == null) {
            return;
        }

        try {
            respawnProtection.cancel();
        } catch (Exception ignored) {
        }

        respawnProtectionMap.remove(player);
    }

    @Override
    public boolean isProtectionActive(Player player) {
        return (respawnProtectionMap.containsKey(player));
    }

    @Override
    public InGameConfigBooleanConstants getAnchorAutoFill() {
        return anchorAutoFill;
    }

    @Override
    public boolean getOriginalOrInheritedAnchorAutoFill() {
        return anchorAutoFill.isOriginal() ? anchorAutoFill.getValue()
                : Main.getConfigurator().config.getBoolean(GLOBAL_ANCHOR_AUTO_FILL);
    }

    @Override
    public InGameConfigBooleanConstants getAnchorDecreasing() {
        return anchorDecreasing;
    }

    @Override
    public boolean getOriginalOrInheritedAnchorDecreasing() {
        return anchorDecreasing.isOriginal() ? anchorDecreasing.getValue()
                : Main.getConfigurator().config.getBoolean(GLOBAL_ANCHOR_DECREASING);
    }

    @Override
    public InGameConfigBooleanConstants getCakeTargetBlockEating() {
        return cakeTargetBlockEating;
    }

    @Override
    public boolean getOriginalOrInheritedCakeTargetBlockEating() {
        return cakeTargetBlockEating.isOriginal() ? cakeTargetBlockEating.getValue()
                : Main.getConfigurator().config.getBoolean(GLOBAL_CAKE_TARGET_BLOCK_EATING);
    }

    @Override
    public InGameConfigBooleanConstants getTargetBlockExplosions() {
        return targetBlockExplosions;
    }

    @Override
    public boolean getOriginalOrInheritedTargetBlockExplosions() {
        return targetBlockExplosions.isOriginal() ? targetBlockExplosions.getValue()
                : Main.getConfigurator().config.getBoolean(GLOBAL_TARGET_BLOCK_EXPLOSIONS);
    }

    public void setAnchorAutoFill(InGameConfigBooleanConstants anchorAutoFill) {
        this.anchorAutoFill = anchorAutoFill;
    }

    public void setAnchorDecreasing(InGameConfigBooleanConstants anchorDecreasing) {
        this.anchorDecreasing = anchorDecreasing;
    }

    public void setCakeTargetBlockEating(InGameConfigBooleanConstants cakeTargetBlockEating) {
        this.cakeTargetBlockEating = cakeTargetBlockEating;
    }

    public void setTargetBlockExplosions(InGameConfigBooleanConstants targetBlockExplosions) {
        this.targetBlockExplosions = targetBlockExplosions;
    }

    public List<GamePlayer> getPlayersWithoutVIP() {
        List<GamePlayer> gamePlayerList = new ArrayList<>(this.players);
        gamePlayerList.removeIf(GamePlayer::canJoinFullGame);

        return gamePlayerList;
    }

    @Override
    public InGameConfigBooleanConstants getHoloAboveBed() {
        return holoAboveBed;
    }

    @Override
    public boolean getOriginalOrInheritedHoloAboveBed() {
        return holoAboveBed.isOriginal() ? holoAboveBed.getValue()
                : Main.getConfigurator().config.getBoolean(HOLO_ABOVE_BED);
    }

    public void setHoloAboveBed(InGameConfigBooleanConstants holoAboveBed) {
        this.holoAboveBed = holoAboveBed;
    }

    @Override
    public InGameConfigBooleanConstants getSpectatorJoin() {
        return spectatorJoin;
    }

    @Override
    public boolean getOriginalOrInheritedSpectatorJoin() {
        return spectatorJoin.isOriginal() ? spectatorJoin.getValue()
                : Main.getConfigurator().config.getBoolean(SPECTATOR_JOIN);
    }

    public void setSpectatorJoin(InGameConfigBooleanConstants spectatorJoin) {
        this.spectatorJoin = spectatorJoin;
    }

    @Override
    public InGameConfigBooleanConstants getStopTeamSpawnersOnDie() {
        return stopTeamSpawnersOnDie;
    }

    @Override
    public boolean getOriginalOrInheritedStopTeamSpawnersOnDie() {
        return stopTeamSpawnersOnDie.isOriginal() ? stopTeamSpawnersOnDie.getValue()
                : Main.getConfigurator().config.getBoolean(STOP_TEAM_SPAWNERS_ON_DIE);
    }

    public void setStopTeamSpawnersOnDie(InGameConfigBooleanConstants stopTeamSpawnersOnDie) {
        this.stopTeamSpawnersOnDie = stopTeamSpawnersOnDie;
    }

    public Inventory getFakeEnderChest(GamePlayer player) {
        if (!fakeEnderChests.containsKey(player)) {
            fakeEnderChests.put(player, Bukkit.createInventory(player.player, InventoryType.ENDER_CHEST));
        }
        return fakeEnderChests.get(player);
    }
}
