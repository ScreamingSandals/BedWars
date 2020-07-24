package org.screamingsandals.bedwars;

import misat11.lib.lang.I18n;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bstats.bukkit.Metrics;
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
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.api.utils.ColorChanger;
import org.screamingsandals.bedwars.commands.*;
import org.screamingsandals.bedwars.config.Configurator;
import org.screamingsandals.bedwars.database.DatabaseManager;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.game.ItemSpawnerType;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.holograms.IHologramInteraction;
import org.screamingsandals.bedwars.holograms.NMSUtilsHologramInteraction;
import org.screamingsandals.bedwars.inventories.ShopInventory;
import org.screamingsandals.bedwars.listener.*;
import org.screamingsandals.bedwars.placeholderapi.BedwarsExpansion;
import org.screamingsandals.bedwars.special.SpecialRegister;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.BedWarsSignOwner;
import org.screamingsandals.bedwars.utils.UpdateChecker;
import org.screamingsandals.lib.debug.Debug;
import org.screamingsandals.lib.nms.holograms.HologramManager;
import org.screamingsandals.lib.nms.utils.ClassStorage;
import org.screamingsandals.lib.signmanager.SignListener;
import org.screamingsandals.lib.signmanager.SignManager;
import org.screamingsandals.simpleinventories.listeners.InventoryListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static misat11.lib.lang.I18n.i18n;

public class Main extends JavaPlugin implements BedwarsAPI {
    private static Main instance;
    private String version, nmsVersion;
    private boolean isPaper;
    private boolean isDisabling = false;
    private boolean isSpigot, isLegacy;
    private boolean snapshot, isVault, isNMS;
    private int versionNumber = 0;
    private Economy econ = null;
    private HashMap<String, Game> games = new HashMap<>();
    private HashMap<Player, GamePlayer> playersInGame = new HashMap<>();
    private HashMap<Entity, Game> entitiesInGame = new HashMap<>();
    private Configurator configurator;
    private ShopInventory menu;
    private HashMap<String, ItemSpawnerType> spawnerTypes = new HashMap<>();
    private DatabaseManager databaseManager;
    private PlayerStatisticManager playerStatisticsManager;
    private IHologramInteraction hologramInteraction;
    private HashMap<String, BaseCommand> commands;
    private ColorChanger colorChanger;
    private SignManager signManager;
    private HologramManager manager;
    public static List<String> autoColoredMaterials = new ArrayList<>();
    private Metrics metrics;

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

    public static Configurator getConfigurator() {
        return instance.configurator;
    }

    public static String getVersion() {
        return instance.version;
    }

    public static boolean isSnapshot() {
        return instance.snapshot;
    }

    public static boolean isSpigot() {
        return instance.isSpigot;
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
            if (isVault() && instance.configurator.config.getBoolean("vault.enable")) {
                EconomyResponse response = instance.econ.depositPlayer(player, coins);
                if (response.transactionSuccess()) {
                    player.sendMessage(i18n("vault_deposite").replace("%coins%", Double.toString(coins)).replace(
                            "%currency%",
                            (coins == 1 ? instance.econ.currencyNameSingular() : instance.econ.currencyNamePlural())));
                }
            }
        } catch (Throwable ignored) {
        }
    }

    public static int getVaultKillReward() {
        return instance.configurator.config.getInt("vault.reward.kill");
    }

    public static int getVaultWinReward() {
        return instance.configurator.config.getInt("vault.reward.win");
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
        if (instance.configurator.config.getBoolean("farmBlocks.enable")) {
            List<String> list = (List<String>) instance.configurator.config.getList("farmBlocks.blocks");
            return list.contains(mat.name());
        }
        return false;
    }

    public static boolean isBreakableBlock(Material mat) {
        if (instance.configurator.config.getBoolean("breakable.enabled")) {
            List<String> list = (List<String>) instance.configurator.config.getList("breakable.blocks");
            boolean asblacklist = instance.configurator.config.getBoolean("breakable.asblacklist", false);
            return list.contains(mat.name()) ? !asblacklist : asblacklist;
        }
        return false;
    }

    public static boolean isCommandLeaveShortcut(String command) {
        if (instance.configurator.config.getBoolean("leaveshortcuts.enabled")) {
            List<String> commands = (List<String>) instance.configurator.config.getList("leaveshortcuts.list");
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
        List<String> commands = instance.configurator.config.getStringList("allowed-commands");
        for (String comm : commands) {
            if (!comm.startsWith("/")) {
                comm = "/" + comm;
            }
            if (comm.equals(commandPref)) {
                return !instance.configurator.config.getBoolean("change-allowed-commands-to-blacklist", false);
            }
        }
        return instance.configurator.config.getBoolean("change-allowed-commands-to-blacklist", false);
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
        return instance.configurator.config.getBoolean("statistics.enabled");
    }

    public static boolean isHologramsEnabled() {
        return instance.configurator.config.getBoolean("holograms.enabled") && instance.hologramInteraction != null;
    }

    public static IHologramInteraction getHologramInteraction() {
        return instance.hologramInteraction;
    }

    public static HashMap<String, BaseCommand> getCommands() {
        return instance.commands;
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

    public static int getVersionNumber() {
        return instance.versionNumber;
    }

    public static SignManager getSignManager() {
        return instance.signManager;
    }

    public static HologramManager getHologramManager() {
        return instance.manager;
    }

    public void onEnable() {
        instance = this;
        version = this.getDescription().getVersion();
        snapshot = version.toLowerCase().contains("pre") || version.toLowerCase().contains("snapshot");
        isNMS = ClassStorage.NMS_BASED_SERVER;
        nmsVersion = ClassStorage.NMS_VERSION;
        isSpigot = ClassStorage.IS_SPIGOT_SERVER;
        colorChanger = new org.screamingsandals.bedwars.utils.ColorChanger();

        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            isPaper = true;
        } catch (ClassNotFoundException ignored) {
            isPaper = false;
        }

        if (!getServer().getPluginManager().isPluginEnabled("Vault")) {
            isVault = false;
        } else {
            isVault = setupEconomy();
        }

        String[] bukkitVersion = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        versionNumber = 0;

        for (int i = 0; i < 2; i++) {
            versionNumber += Integer.parseInt(bukkitVersion[i]) * (i == 0 ? 100 : 1);
        }

        isLegacy = versionNumber < 113;

        configurator = new Configurator(this);

        configurator.createFiles();

        Debug.init(getName());
        Debug.setDebug(configurator.config.getBoolean("debug"));

        I18n.load(this, configurator.config.getString("locale"));

        databaseManager = new DatabaseManager(configurator.config.getString("database.host"),
                configurator.config.getInt("database.port"), configurator.config.getString("database.user"),
                configurator.config.getString("database.password"), configurator.config.getString("database.db"),
                configurator.config.getString("database.table-prefix", "bw_"), configurator.config.getBoolean("database.useSSL"));

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

        try {
            if (configurator.config.getBoolean("holograms.enabled")) {
                hologramInteraction = new NMSUtilsHologramInteraction();
                hologramInteraction.loadHolograms();
            }
        } catch (Throwable ignored) {
        }

        commands = new HashMap<>();
        new AddholoCommand();
        new AdminCommand();
        new AutojoinCommand();
        new AllJoinCommand();
        new HelpCommand();
        new JoinCommand();
        new LeaveCommand();
        new ListCommand();
        new RejoinCommand();
        new ReloadCommand();
        new RemoveholoCommand();
        new StatsCommand();
        new MainlobbyCommand();

        BwCommandsExecutor cmd = new BwCommandsExecutor();
        getCommand("bw").setExecutor(cmd);
        getCommand("bw").setTabCompleter(cmd);

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        if (versionNumber >= 109) {
            getServer().getPluginManager().registerEvents(new Player19Listener(), this);
        }
        if (versionNumber >= 112) {
            getServer().getPluginManager().registerEvents(new Player112Listener(), this);
        } else {
            getServer().getPluginManager().registerEvents(new PlayerBefore112Listener(), this);
        }
        getServer().getPluginManager().registerEvents(new VillagerListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);

        InventoryListener.init(this);

        this.manager = new HologramManager(this);

        SpecialRegister.onEnable(this);

        getServer().getServicesManager().register(BedwarsAPI.class, this, this, ServicePriority.Normal);

        for (String spawnerN : configurator.config.getConfigurationSection("resources").getKeys(false)) {

            String name = Main.getConfigurator().config.getString("resources." + spawnerN + ".name");
            String translate = Main.getConfigurator().config.getString("resources." + spawnerN + ".translate");
            int interval = Main.getConfigurator().config.getInt("resources." + spawnerN + ".interval", 1);
            double spread = Main.getConfigurator().config.getDouble("resources." + spawnerN + ".spread");
            int damage = Main.getConfigurator().config.getInt("resources." + spawnerN + ".damage");
            String materialName = Main.getConfigurator().config.getString("resources." + spawnerN + ".material", "AIR");
            String colorName = Main.getConfigurator().config.getString("resources." + spawnerN + ".color", "WHITE");

            Material material = Material.valueOf(materialName);
            if (material == Material.AIR) {
                continue;
            }

            ChatColor color = ChatColor.valueOf(colorName);
            spawnerTypes.put(spawnerN.toLowerCase(), new ItemSpawnerType(spawnerN.toLowerCase(), name, translate,
                    spread, material, color, interval, damage));
        }

        try {
            menu = new ShopInventory();
        } catch (Exception e) {
            Debug.warn("Your shop.yml/shop.groovy is wrong!", true);
            Debug.warn("Disabling plugin..", true);

            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (getConfigurator().config.getBoolean("bungee.enabled")) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        }

        Bukkit.getConsoleSender().sendMessage("§c=====§f===========  by ScreamingSandals <Misat11, Ceph>");
        Bukkit.getConsoleSender()
                .sendMessage("§c+ Bed§fWars Zero +   §6Version: " + version);
        Bukkit.getConsoleSender()
                .sendMessage("§c=====§f===========  " + (snapshot ? "§cSNAPSHOT VERSION" : "§aSTABLE VERSION"));
        if (isVault) {
            Bukkit.getConsoleSender().sendMessage("§c[B§fW] §6Found Vault");
        }
        if (!isSpigot) {
            Bukkit.getConsoleSender()
                    .sendMessage("§c[B§fW] §cWARNING: You are not using Spigot. Some features may not work properly.");
        }

        if (versionNumber < 109) {
            Bukkit.getConsoleSender().sendMessage(
                    "§c[B§fW] §cIMPORTANT WARNING: You are using version older than 1.9! This version is not officially supported, and some features may not work at all!");
        }
        try {
            float javaVer = Float.parseFloat(System.getProperty("java.class.version"));
            if (javaVer < 55) {
                getLogger().warning("Future versions of plugins from ScreamingSandals will require at least Java 11. "
                        + "Your server is not prepared for it. Update your Java or contact your hosting. "
                        + "Java 8 for commercial usage is already out of casual support, for personal usage it's supported until December 2020! "
                        + "Java 9 and Java 10 were short-term support versions, these versions are already not supported.");
                getLogger().warning("Future versions of Java will require Minecraft version at least 1.12");

            }
        } catch (Throwable t) { // What if it fails? Why it should fail I don't know :D
        }


        final File arenasFolder = new File(getDataFolder(), "arenas");
        if (arenasFolder.exists()) {
            try (Stream<Path> stream = Files.walk(Paths.get(arenasFolder.getAbsolutePath()))) {
                final List<String> results = stream.filter(Files::isRegularFile)
                        .map(Path::toString)
                        .collect(Collectors.toList());

                if (results.isEmpty()) {
                    Debug.info("No arenas have been found!", true);
                } else {
                    for (String result : results) {
                        File file = new File(result);
                        if (file.exists() && file.isFile()) {
                            Game.loadGame(file);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(); // maybe remove after testing
            }
        }

        BedWarsSignOwner signOwner = new BedWarsSignOwner();
        signManager = new SignManager(signOwner, configurator.signsFile);
        getServer().getPluginManager().registerEvents(new SignListener(signOwner, signManager), this);

        try {
            // Fixing bugs created by third party plugin

            // PerWorldInventory
            if (Bukkit.getPluginManager().isPluginEnabled("PerWorldInventory")) {
                Plugin pwi = Bukkit.getPluginManager().getPlugin("PerWorldInventory");
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

        if (Main.getConfigurator().config.getBoolean("update-checker.zero.console")
                || Main.getConfigurator().config.getBoolean("update-checker.zero.oped-players")
        || Main.getConfigurator().config.getBoolean("update-checker.one.console")
                || Main.getConfigurator().config.getBoolean("update-checker.one.oped-players")) {
            UpdateChecker.run();
        }

        final int pluginId = 7147;
        metrics = new Metrics(this, pluginId);

        Bukkit.getConsoleSender().sendMessage("§fEverything is loaded! If you like our work, consider visiting our Patreon! <3");
        Bukkit.getConsoleSender().sendMessage("§fhttps://www.patreon.com/screamingsandals");
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

        metrics = null;
    }

    public static boolean isPaper() {
        return instance.isPaper;
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
        if (entitiesInGame.containsKey(entity)) {
            entitiesInGame.remove(entity);
        }
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
        final TreeMap<Integer, Game> availableGames = new TreeMap<>();
        games.values().forEach(game -> {
            if (game.getStatus() != GameStatus.WAITING) {
                return;
            }

            availableGames.put(game.getConnectedPlayers().size(), game);
        });

        if (availableGames.isEmpty()) {
            return null;
        }

        return availableGames.lastEntry().getValue();
    }

    @Override
    public String getHubServerName() {
        return configurator.config.getString("bungee.server");
    }

    public static boolean isDisabling() {
        return instance.isDisabling;
    }
}
