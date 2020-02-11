package org.screamingsandals.bedwars;

import io.papermc.lib.PaperLib;
import misat11.lib.lang.I18n;
import misat11.lib.sgui.InventoryListener;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.api.utils.ColorChanger;
import org.screamingsandals.bedwars.config.BaseConfig;
import org.screamingsandals.bedwars.config.CustomConfig;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.database.DatabaseManager;
import org.screamingsandals.bedwars.game.*;
import org.screamingsandals.bedwars.holograms.HologramManager;
import org.screamingsandals.bedwars.inventories.ShopInventory;
import org.screamingsandals.bedwars.listener.*;
import org.screamingsandals.bedwars.placeholderapi.BedwarsExpansion;
import org.screamingsandals.bedwars.special.SpecialRegister;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.BedWarsSignOwner;
import org.screamingsandals.lib.debug.Debug;
import org.screamingsandals.lib.nms.utils.ClassStorage;
import org.screamingsandals.lib.screamingcommands.ScreamingCommands;
import org.screamingsandals.lib.signmanager.SignListener;
import org.screamingsandals.lib.signmanager.SignManager;

import java.io.File;
import java.util.*;

import static misat11.lib.lang.I.mpr;

public class Main extends JavaPlugin implements BedwarsAPI {
    private static Main instance;
    private String version, nmsVersion;
    private boolean isDisabling = false;
    private boolean isLegacy;
    private boolean snapshot, isVault, isNMS;
    private Economy econ = null;
    private HashMap<String, Game> games = new HashMap<>();
    private HashMap<Player, GamePlayer> playersInGame = new HashMap<>();
    private HashMap<Entity, Game> entitiesInGame = new HashMap<>();
    private HashMap<String, GameCreator> gamesInCreator = new HashMap<>();
    private MainConfig mainConfig;
    private CustomConfig signsConfig, recordsConfig;
    private ShopInventory menu;
    private HashMap<String, ItemSpawnerType> spawnerTypes = new HashMap<>();
    private DatabaseManager databaseManager;
    private PlayerStatisticManager playerStatisticsManager;
    private HologramManager hologramInteraction;
    private SpigetUpdate spigetUpdate;
    private ColorChanger colorChanger;
    private SignManager signManager;
    private ScreamingCommands screamingCommands;
    private org.screamingsandals.lib.nms.holograms.HologramManager superHologramManager;
    public static List<String> autoColoredMaterials = new ArrayList<>();

    static {
        // ColorChanger list of materials
        autoColoredMaterials.add("WOOL");
        autoColoredMaterials.add("CARPET");
        autoColoredMaterials.add("CONCRETE");
        autoColoredMaterials.add("CONCRETE_POWDER");
        autoColoredMaterials.add("STAINED_CLAY"); // LEGACY ONLY
        autoColoredMaterials.add("TERRACOTTA"); // FLATTENING ONLY
        autoColoredMaterials.add("STAINED_GLASS");
        autoColoredMaterials.add("STAINED_GLASS_PANE");
    }

    public static Main getInstance() {
        return instance;
    }

    public static MainConfig getMainConfig() {
        return instance.mainConfig;
    }

    public static CustomConfig getRecordsConfig() {
        return instance.recordsConfig;
    }

    public static String getVersion() {
        return instance.version;
    }

    public static boolean isSnapshot() {
        return instance.snapshot;
    }

    public static boolean isSpigot() {
        return PaperLib.isSpigot();
    }

    public static boolean isPaper() {
        return PaperLib.isPaper();
    }

    public static boolean isVault() {
        return instance.isVault;
    }

    public static boolean isLegacy() {
        return instance.isLegacy;
    }

    public static boolean isNMS() {
        return instance.isNMS;
    }

    public static String getNMSVersion() {
        return isNMS() ? instance.nmsVersion : null;
    }

    public static void depositPlayer(Player player, double coins) {
        try {
            if (isVault() && instance.mainConfig.getBoolean("vault.enable")) {
                EconomyResponse response = instance.econ.depositPlayer(player, coins);
                if (response.transactionSuccess()) {
                    mpr("game.info.player.vault_deposite")
                            .replace("%coins%", Double.toString(coins))
                            .replace("%currency%", (coins == 1
                                    ? instance.econ.currencyNameSingular()
                                    : instance.econ.currencyNamePlural())).send(player);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    public static int getVaultKillReward() {
        return instance.mainConfig.getInt("vault.reward.kill");
    }

    public static int getVaultWinReward() {
        return instance.mainConfig.getInt("vault.reward.win");
    }

    public static Game getGame(String string) {
        return instance.games.get(string);
    }

    public static boolean isGameExists(String string) {
        return instance.games.containsKey(string);
    }

    public static void addGame(Game game) {
        instance.games.put(game.getName(), game);
    }

    public static void removeGame(Game game) {
        instance.games.remove(game.getName());
    }

    public static Game getInGameEntity(Entity entity) {
        return instance.entitiesInGame.getOrDefault(entity, null);
    }

    public HashMap<String, GameCreator> getGamesInCreator() {
        return gamesInCreator;
    }

    public void addCreator(String arenaName, GameCreator gameCreator) {
        gamesInCreator.put(arenaName, gameCreator);
    }

    public void removeCreator(String arenaName) {
        gamesInCreator.remove(arenaName);
    }

    public static void registerGameEntity(Entity entity, Game game) {
        instance.entitiesInGame.put(entity, game);
    }

    public static void unregisterGameEntity(Entity entity) {
        instance.entitiesInGame.remove(entity);
    }

    public static boolean isPlayerInGame(Player player) {
        if (instance.playersInGame.containsKey(player))
            return instance.playersInGame.get(player).isInGame();
        return false;
    }

    public static GamePlayer getPlayerGameProfile(Player player) {
        if (instance.playersInGame.containsKey(player))
            return instance.playersInGame.get(player);
        GamePlayer gPlayer = new GamePlayer(player);
        instance.playersInGame.put(player, gPlayer);
        return gPlayer;
    }

    public static void unloadPlayerGameProfile(Player player) {
        if (instance.playersInGame.containsKey(player)) {
            instance.playersInGame.get(player).changeGame(null);
            instance.playersInGame.remove(player);
        }
    }

    public static boolean isPlayerGameProfileRegistered(Player player) {
        return instance.playersInGame.containsKey(player);
    }

    public static void sendGameListInfo(CommandSender player) {
        for (Game game : instance.games.values()) {
            player.sendMessage((game.getStatus() == GameStatus.DISABLED ? "§c" : "§a") + game.getName() + "§f "
                    + game.countPlayers());
        }
    }

    public static void openStore(Player player, GameStore store) {
        instance.menu.show(player, store);
    }

    public static boolean isFarmBlock(Material mat) {
        if (instance.mainConfig.getBoolean("farmBlocks.enable")) {
            List<String> list = (List<String>) instance.mainConfig.getList("farmBlocks.blocks");
            assert list != null;
            return list.contains(mat.name());
        }
        return false;
    }

    public static boolean isBreakableBlock(Material mat) {
        if (instance.mainConfig.getBoolean("breakable.enabled")) {
            List<String> list = (List<String>) instance.mainConfig.getList("breakable.blocks");
            boolean asblacklist = instance.mainConfig.getBoolean("breakable.asblacklist", false);
            assert list != null;
            return list.contains(mat.name()) != asblacklist;
        }
        return false;
    }

    public static boolean isCommandLeaveShortcut(String command) {
        if (instance.mainConfig.getBoolean("leaveshortcuts.enabled")) {
            List<String> commands = (List<String>) instance.mainConfig.getList("leaveshortcuts.list");
            assert commands != null;
            for (String comm : commands) {
                if (!comm.startsWith("/")) {
                    comm = "/" + comm;
                }
                if (comm.equals(command)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isCommandAllowedInGame(String commandPref) {
        if ("/bw".equals(commandPref) || "/bedwars".equals(commandPref)) {
            return true;
        }
        List<String> commands = instance.mainConfig.getStringList("allowed-commands");
        for (String comm : commands) {
            if (!comm.startsWith("/")) {
                comm = "/" + comm;
            }
            if (comm.equals(commandPref)) {
                return !instance.mainConfig.getBoolean("change-allowed-commands-to-blacklist", false);
            }
        }
        return instance.mainConfig.getBoolean("change-allowed-commands-to-blacklist", false);
    }

    public static ItemSpawnerType getSpawnerType(String key) {
        return instance.spawnerTypes.get(key);
    }

    public static List<String> getAllSpawnerTypes() {
        return new ArrayList<>(instance.spawnerTypes.keySet());
    }

    public static List<String> getGameNames() {
        List<String> list = new ArrayList<>();
        for (Game game : instance.games.values()) {
            list.add(game.getName());
        }
        return list;
    }

    public static DatabaseManager getDatabaseManager() {
        return instance.databaseManager;
    }

    public static PlayerStatisticManager getPlayerStatisticsManager() {
        return instance.playerStatisticsManager;
    }

    public static boolean isPlayerStatisticsEnabled() {
        return instance.mainConfig.getBoolean("statistics.enabled");
    }

    public static boolean isHologramsEnabled() {
        return instance.mainConfig.getBoolean("holograms.enabled") && instance.hologramInteraction != null;
    }

    public static HologramManager getHologramInteraction() {
        return instance.hologramInteraction;
    }

    public static org.screamingsandals.lib.nms.holograms.HologramManager getSuperHologramManager() {
        return instance.superHologramManager;
    }

    public static List<Entity> getGameEntities(Game game) {
        List<Entity> entityList = new ArrayList<>();
        for (Map.Entry<Entity, Game> entry : instance.entitiesInGame.entrySet()) {
            if (entry.getValue() == game) {
                entityList.add(entry.getKey());
            }
        }
        return entityList;
    }

    public static SignManager getSignManager() {
        return instance.signManager;
    }

    public void onEnable() {
        instance = this;
        version = this.getDescription().getVersion();
        snapshot = version.toLowerCase().contains("pre") || version.toLowerCase().contains("snapshot");
        isNMS = ClassStorage.NMS_BASED_SERVER;
        nmsVersion = ClassStorage.NMS_VERSION;
        colorChanger = new org.screamingsandals.bedwars.utils.ColorChanger();

        if (!getServer().getPluginManager().isPluginEnabled("Vault")) {
            isVault = false;
        } else {
            isVault = setupEconomy();
        }

        isLegacy = PaperLib.getMinecraftVersion() < 13;

        mainConfig = new MainConfig(BaseConfig.createConfigFile(getDataFolder(), "config.yml"));
        mainConfig.initialize();

        signsConfig = new CustomConfig(BaseConfig.createConfigFile(getDataFolder(), "signs.yml"));
        signsConfig.initialize();

        recordsConfig = new CustomConfig(BaseConfig.createConfigFile(getDataFolder(), "records.yml"));

        Debug.init(getName());
        Debug.setDebug(mainConfig.getBoolean("debug"));

        I18n.load(this, mainConfig.getString("language_code"));


        screamingCommands = new ScreamingCommands(this, Main.class, "org/screamingsandals/bedwars");
        screamingCommands.loadAllCommands();

        databaseManager = new DatabaseManager(mainConfig.getString("database.host"),
                mainConfig.getInt("database.port"), mainConfig.getString("database.user"),
                mainConfig.getString("database.password"), mainConfig.getString("database.db"),
                mainConfig.getString("database.table-prefix", "bw_"));

        if (isPlayerStatisticsEnabled()) {
            playerStatisticsManager = new PlayerStatisticManager();
            playerStatisticsManager.initialize();
        }

        try {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new BedwarsExpansion().register();
            }
        } catch (Throwable ignored) {
        }

        superHologramManager = new org.screamingsandals.lib.nms.holograms.HologramManager(this);

        try {
            if (mainConfig.getBoolean("holograms.enabled")) {
                hologramInteraction = new HologramManager();
                hologramInteraction.loadHolograms();
            }
        } catch (Throwable ignored) {
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        if (PaperLib.getMinecraftVersion() >= 9) {
            getServer().getPluginManager().registerEvents(new Player19Listener(), this);
        }
        if (PaperLib.getMinecraftVersion() >= 12) {
            getServer().getPluginManager().registerEvents(new Player112Listener(), this);
        } else {
            getServer().getPluginManager().registerEvents(new PlayerBefore112Listener(), this);
        }
        getServer().getPluginManager().registerEvents(new VillagerListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        SpecialRegister.onEnable(this);

        getServer().getServicesManager().register(BedwarsAPI.class, this, this, ServicePriority.Normal);

        for (String spawnerN : mainConfig.getConfigurationSection("resources").getKeys(false)) {

            String name = Main.getMainConfig().getString("resources." + spawnerN + ".name");
            String translate = Main.getMainConfig().getString("resources." + spawnerN + ".translate");
            int interval = Main.getMainConfig().getInt("resources." + spawnerN + ".interval", 1);
            double spread = Main.getMainConfig().getDouble("resources." + spawnerN + ".spread");
            int damage = Main.getMainConfig().getInt("resources." + spawnerN + ".damage");
            String materialName = Main.getMainConfig().getString("resources." + spawnerN + ".material", "AIR");
            String colorName = Main.getMainConfig().getString("resources." + spawnerN + ".color", "WHITE");

            Material material = Material.valueOf(materialName);
            if (material == Material.AIR) {
                continue;
            }

            ChatColor color = ChatColor.valueOf(colorName);
            spawnerTypes.put(spawnerN.toLowerCase(), new ItemSpawnerType(spawnerN.toLowerCase(), name, translate,
                    spread, material, color, interval, damage));
        }

        menu = new ShopInventory();

        if (getMainConfig().getBoolean("bungee.enabled")) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        }

        Bukkit.getConsoleSender().sendMessage("§c=====§f======  by ScreamingSandals <Misat11, Ceph>");
        Bukkit.getConsoleSender()
                .sendMessage("§c+ Bed§fWars +  §6Version: " + version);
        Bukkit.getConsoleSender()
                .sendMessage("§c=====§f======  " + (snapshot ? "§cSNAPSHOT VERSION" : "§aSTABLE VERSION"));
        if (isVault) {
            Bukkit.getConsoleSender().sendMessage("§c[B§fW] §6Found Vault");
        }
        if (!isSpigot()) {
            Bukkit.getConsoleSender()
                    .sendMessage("§c[B§fW] §cWARNING: You are not using Spigot. Some features may not work properly.");
        }

        if (PaperLib.getMinecraftVersion() < 9) {
            Bukkit.getConsoleSender().sendMessage(
                    "§c[B§fW] §cIMPORTANT WARNING: You are using version older than 1.9! This version is not officially supported and some features may not work at all!");
        }

        File folder = new File(getDataFolder().toString(), "arenas");
        if (folder.exists()) {
            File[] listOfFiles = folder.listFiles();
            assert listOfFiles != null;
            if (listOfFiles.length > 0) {
                for (File listOfFile : listOfFiles) {
                    if (listOfFile.isFile()) {
                        Game.loadGame(listOfFile);
                    }
                }
            }
        }

        BedWarsSignOwner signOwner = new BedWarsSignOwner();
        signManager = new SignManager(signOwner, signsConfig.getYamlConfiguration(), signsConfig.getConfigFile()); //TODO
        getServer().getPluginManager().registerEvents(new SignListener(signOwner, signManager), this);

        try {
            // Fixing bugs created by third party plugin

            // PerWorldInventory
            if (Bukkit.getPluginManager().isPluginEnabled("PerWorldInventory")) {
                Plugin pwi = Bukkit.getPluginManager().getPlugin("PerWorldInventory");
                assert pwi != null;
                if (pwi.getClass().getName().equals("me.ebonjaeger.perworldinventory.PerWorldInventory")) {
                    // Kotlin version
                    getServer().getPluginManager().registerEvents(new PerWorldInventoryKotlinListener(), this);
                } else {
                    // Legacy version
                    getServer().getPluginManager().registerEvents(new PerWorldInventoryLegacyListener(), this);
                }
            }

        } catch (Throwable ignored) {
            // maybe something here can cause exception

        }

        spigetUpdate = new SpigetUpdate(this, 63714);

        spigetUpdate.setVersionComparator(VersionComparator.SEM_VER_SNAPSHOT);

        spigetUpdate.checkForUpdate(new UpdateCallback() {
            @Override
            public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
                Bukkit.getConsoleSender().sendMessage("§c[B§fW] §aNew RELEASE version " + newVersion
                        + " of BedWars is available! Download it from " + downloadUrl);
            }

            @Override
            public void upToDate() {

            }
        });
    }

    public void onDisable() {
        isDisabling = true;
        if (signManager != null) {
            signManager.save();
        }
        for (Game game : games.values()) {
            game.stop();
        }
        this.getServer().getServicesManager().unregisterAll(this);

        if (isHologramsEnabled() && hologramInteraction != null) {
            hologramInteraction.unloadHolograms();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        econ = rsp.getProvider();
        return true;
    }

    @Override
    public List<org.screamingsandals.bedwars.api.game.Game> getGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public org.screamingsandals.bedwars.api.game.Game getGameOfPlayer(Player player) {
        return isPlayerInGame(player) ? getPlayerGameProfile(player).getGame() : null;
    }

    @Override
    public boolean isGameWithNameExists(String name) {
        return games.containsKey(name);
    }

    @Override
    public org.screamingsandals.bedwars.api.game.Game getGameByName(String name) {
        return games.get(name);
    }

    @Override
    public org.screamingsandals.bedwars.api.game.Game getGameWithHighestPlayers() {
        TreeMap<Integer, org.screamingsandals.bedwars.api.game.Game> gameList = new TreeMap<>();
        for (org.screamingsandals.bedwars.api.game.Game game : getGames()) {
            if (game.getStatus() != GameStatus.WAITING) {
                continue;
            }
            if (game.getConnectedPlayers().size() >= game.getMaxPlayers()) {
                continue;
            }
            gameList.put(game.countConnectedPlayers(), game);
        }

        Map.Entry<Integer, org.screamingsandals.bedwars.api.game.Game> lastEntry = gameList.lastEntry();
        return lastEntry.getValue();
    }

    @Override
    public org.screamingsandals.bedwars.api.game.Game getGameWithLowestPlayers() {
        TreeMap<Integer, org.screamingsandals.bedwars.api.game.Game> gameList = new TreeMap<>();
        for (org.screamingsandals.bedwars.api.game.Game game : getGames()) {
            if (game.getStatus() != GameStatus.WAITING) {
                continue;
            }
            if (game.getConnectedPlayers().size() >= game.getMaxPlayers()) {
                continue;
            }
            gameList.put(game.countConnectedPlayers(), game);
        }

        Map.Entry<Integer, org.screamingsandals.bedwars.api.game.Game> lastEntry = gameList.firstEntry();
        return lastEntry.getValue();
    }

    @Override
    public List<org.screamingsandals.bedwars.api.game.ItemSpawnerType> getItemSpawnerTypes() {
        return new ArrayList<>(spawnerTypes.values());
    }

    @Override
    public org.screamingsandals.bedwars.api.game.ItemSpawnerType getItemSpawnerTypeByName(String name) {
        return spawnerTypes.get(name);
    }

    @Override
    public boolean isItemSpawnerTypeRegistered(String name) {
        return spawnerTypes.containsKey(name);
    }

    @Override
    public boolean isEntityInGame(Entity entity) {
        return entitiesInGame.containsKey(entity);
    }

    @Override
    public org.screamingsandals.bedwars.api.game.Game getGameOfEntity(Entity entity) {
        return entitiesInGame.get(entity);
    }

    @Override
    public void registerEntityToGame(Entity entity, org.screamingsandals.bedwars.api.game.Game game) {
        if (!(game instanceof Game)) {
            return;
        }
        entitiesInGame.put(entity, (Game) game);
    }

    @Override
    public void unregisterEntityFromGame(Entity entity) {
        entitiesInGame.remove(entity);
    }

    @Override
    public boolean isPlayerPlayingAnyGame(Player player) {
        return isPlayerInGame(player);
    }

    @Override
    public String getPluginVersion() {
        return version;
    }

    @Override
    public ColorChanger getColorChanger() {
        return colorChanger;
    }

    public static ItemStack applyColor(TeamColor color, ItemStack itemStack) {
        return applyColor(color, itemStack, false);
    }

    public static ItemStack applyColor(TeamColor color, ItemStack itemStack, boolean clone) {
        org.screamingsandals.bedwars.api.TeamColor teamColor = color.toApiColor();
        if (clone) {
            itemStack = itemStack.clone();
        }
        return instance.getColorChanger().applyColor(teamColor, itemStack);
    }

    public static ItemStack applyColor(org.screamingsandals.bedwars.api.TeamColor teamColor, ItemStack itemStack) {
        return instance.getColorChanger().applyColor(teamColor, itemStack);
    }

    @Override
    public org.screamingsandals.bedwars.api.game.Game getFirstWaitingGame() {
        for (Game game : games.values()) {
            if (game.getStatus() == GameStatus.WAITING) {
                return game;
            }
        }
        return null;
    }

    public static boolean isDisabling() {
        return instance.isDisabling;
    }
}
