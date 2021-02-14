package org.screamingsandals.bedwars;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.RecordSave;
import org.screamingsandals.bedwars.lib.lang.I18n;
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
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameStore;
import org.screamingsandals.bedwars.api.statistics.PlayerStatisticsManager;
import org.screamingsandals.bedwars.api.utils.ColorChanger;
import org.screamingsandals.bedwars.commands.*;
import org.screamingsandals.bedwars.database.DatabaseManager;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.game.ItemSpawnerType;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.holograms.LeaderboardHolograms;
import org.screamingsandals.bedwars.holograms.StatisticsHolograms;
import org.screamingsandals.bedwars.inventories.ShopInventory;
import org.screamingsandals.bedwars.listener.*;
import org.screamingsandals.bedwars.placeholderapi.BedwarsExpansion;
import org.screamingsandals.bedwars.premium.PremiumBedwars;
import org.screamingsandals.bedwars.special.SpecialRegister;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.tab.TabManager;
import org.screamingsandals.bedwars.utils.BedWarsSignOwner;
import org.screamingsandals.bedwars.utils.UpdateChecker;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.lib.nms.holograms.HologramManager;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;
import org.screamingsandals.bedwars.lib.signmanager.SignListener;
import org.screamingsandals.bedwars.lib.signmanager.SignManager;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.material.MaterialMapping;
import org.screamingsandals.lib.plugin.PluginContainer;
import org.screamingsandals.lib.plugin.PluginManager;
import org.screamingsandals.lib.utils.ControllableImpl;
import org.screamingsandals.lib.utils.InitUtils;
import org.screamingsandals.lib.utils.PlatformType;
import org.screamingsandals.lib.utils.annotations.Init;
import org.screamingsandals.lib.utils.annotations.Plugin;
import org.screamingsandals.lib.utils.annotations.PluginDependencies;
import org.screamingsandals.simpleinventories.SimpleInventoriesCore;
import org.screamingsandals.simpleinventories.bukkit.SimpleInventoriesBukkit;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import pronze.lib.scoreboards.ScoreboardManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

@Plugin(
        id = "BedWars",
        authors = {"ScreamingSandals <Misat11, Ceph, Pronze>"},
        version = VersionInfo.VERSION,
        loadTime = Plugin.LoadTime.POSTWORLD
)
@PluginDependencies(platform = PlatformType.BUKKIT, softDependencies = {
        "Vault",
        "Multiverse-Core",
        "Multiworld",
        "MultiWorld",
        "UltimateCore",
        "PlaceholderAPI",
        "BarAPI",
        "PerWorldInventory",
        "SlimeWorldManager",
        "My_Worlds",
        "Citizens",
        "Parties"
})
@Init(services = {
        SimpleInventoriesCore.class,
        EventManager.class
})
public class Main extends PluginContainer implements BedwarsAPI {
    @Getter
    private static final String buildInfo = VersionInfo.BUILD_NUMBER;

    private static Main instance;
    private String version;
    private boolean isDisabling = false;
    private boolean isSpigot, isLegacy;
    private boolean isVault;
    private boolean isNMS;
    private int versionNumber = 0;
    private Economy econ = null;
    private HashMap<String, Game> games = new HashMap<>();
    private HashMap<Player, GamePlayer> playersInGame = new HashMap<>();
    private HashMap<Entity, Game> entitiesInGame = new HashMap<>();
    private MainConfig configurator;
    private ShopInventory menu;
    private HashMap<String, ItemSpawnerType> spawnerTypes = new HashMap<>();
    private DatabaseManager databaseManager;
    private PlayerStatisticManager playerStatisticsManager;
    private StatisticsHolograms hologramInteraction;
    private HashMap<String, BaseCommand> commands;
    private ColorChanger colorChanger;
    private SignManager signManager;
    private HologramManager manager;
    private LeaderboardHolograms leaderboardHolograms;
    private TabManager tabManager;
    public static List<String> autoColoredMaterials = new ArrayList<>();
    private Metrics metrics;
    private ControllableImpl controllable;
    private RecordSave recordSave;

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

    public static MainConfig getConfigurator() {
        return instance.configurator;
    }

    public static String getVersion() {
        return instance.version;
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

    public static void depositPlayer(Player player, double coins) {
        try {
            if (isVault() && instance.configurator.node("vault", "enabled").getBoolean()) {
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
        return instance.configurator.node("vault", "reward", "kill").getInt();
    }

    public static int getVaultWinReward() {
        return instance.configurator.node("vault", "reward", "win").getInt();
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
        if (instance.configurator.node("ignored-blocks", "enabled").getBoolean()) {
            try {
                return instance.configurator.node("ignored-blocks", "blocks").getList(String.class).contains(mat.name());
            } catch (SerializationException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isBreakableBlock(Material mat) {
        if (instance.configurator.node("breakable", "enabled").getBoolean()) {
            try {
                var list = instance.configurator.node("breakable", "blocks").getList(String.class);
                boolean asblacklist = instance.configurator.node("breakable", "blacklist-mode").getBoolean();
                return list.contains(mat.name()) != asblacklist;
            } catch (SerializationException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isCommandLeaveShortcut(String command) {
        if (instance.configurator.node("leaveshortcuts", "enabled").getBoolean()) {
            try {
                var commands = instance.configurator.node("leaveshortcuts", "list").getList(String.class);
                for (var comm : commands) {
                    if (!comm.startsWith("/")) {
                        comm = "/" + comm;
                    }
                    if (comm.equals(command)) {
                        return true;
                    }
                }
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isCommandAllowedInGame(String commandPref) {
        if ("/bw".equals(commandPref) || "/bedwars".equals(commandPref)) {
            return true;
        }
        try {
            var commands = instance.configurator.node("commands", "list").getList(String.class);
            for (var comm : commands) {
                if (!comm.startsWith("/")) {
                    comm = "/" + comm;
                }
                if (comm.equals(commandPref)) {
                    return !instance.configurator.node("commands", "blacklist-mode").getBoolean();
                }
            }
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return instance.configurator.node("commands", "blacklist-mode").getBoolean();
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
        return instance.configurator.node("statistics", "enabled").getBoolean();
    }

    public static boolean isHologramsEnabled() {
        return instance.configurator.node("holograms", "enabled").getBoolean() && instance.hologramInteraction != null;
    }

    public static StatisticsHolograms getHologramInteraction() {
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

    public static LeaderboardHolograms getLeaderboardHolograms() {
        return instance.leaderboardHolograms;
    }

    public static TabManager getTabManager() {
        return instance.tabManager;
    }

    public static RecordSave getRecordSave() {
        return instance.recordSave;
    }

    public void onEnable() {
        instance = this;
        version = this.getPluginDescription().getVersion();
        var snapshot = version.toLowerCase().contains("pre") || version.toLowerCase().contains("snapshot");
        isNMS = ClassStorage.NMS_BASED_SERVER;
        isSpigot = ClassStorage.IS_SPIGOT_SERVER;
        colorChanger = new org.screamingsandals.bedwars.utils.ColorChanger();

        if (!PluginManager.isEnabled(PluginManager.createKey("Vault").orElseThrow())) {
            isVault = false;
        } else {
            isVault = setupEconomy();
        }

        var bukkitVersion = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        versionNumber = 0;

        for (int i = 0; i < 2; i++) {
            versionNumber += Integer.parseInt(bukkitVersion[i]) * (i == 0 ? 100 : 1);
        }

        isLegacy = versionNumber < 113;

        configurator = new MainConfig(YamlConfigurationLoader.builder()
                .path(getPluginDescription().getDataFolder().resolve("config.yml"))
        );
        configurator.load();

        recordSave = new RecordSave(YamlConfigurationLoader.builder()
                .path(getPluginDescription().getDataFolder().resolve("database/record.yml"))
                .build()
        );
        recordSave.load();

        Debug.init(getPluginDescription().getName());
        Debug.setDebug(configurator.node("debug").getBoolean());

        I18n.load(this.getPluginDescription().as(JavaPlugin.class), configurator.node("locale").getString("en"));

        databaseManager = new DatabaseManager(configurator.node("database", "host").getString(),
                configurator.node("database", "port").getInt(), configurator.node("database", "user").getString(),
                configurator.node("database", "password").getString(), configurator.node("database", "db").getString(),
                configurator.node("database", "table-prefix").getString(), configurator.node("database", "useSSL").getBoolean());

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
            if (configurator.node("holograms", "enabled").getBoolean()) {
                hologramInteraction = new StatisticsHolograms();
                hologramInteraction.loadHolograms();

                leaderboardHolograms = new LeaderboardHolograms();
                leaderboardHolograms.loadHolograms();
            }
        } catch (Throwable exception) {
            getLogger().error("Failed to load holograms");
            exception.printStackTrace();
        }

        var partiesEnabled = false;

        if (Main.getConfigurator().node("party", "enabled").getBoolean()) {
            final var partyPlugin = PluginManager.getPlugin(PluginManager.createKey("Parties").orElseThrow());
            if (partyPlugin.isPresent() && partyPlugin.get().isEnabled()) {
                partiesEnabled = true;
            }
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
        new LeaderboardCommand();
        new DumpCommand();
        new LanguageCommand();
        if (partiesEnabled)
            new PartyCommand();

        final var cmd = new BwCommandsExecutor();
        getCommand("bw").setExecutor(cmd);
        getCommand("bw").setTabCompleter(cmd);

        registerBedwarsListener(new PlayerListener());
        if (versionNumber >= 109) {
            registerBedwarsListener(new Player19Listener());
        }

        final var playerBeforeOrAfter112Listener = versionNumber >= 122 ? new Player112Listener() : new PlayerBefore112Listener();
        registerBedwarsListener(playerBeforeOrAfter112Listener);

        registerBedwarsListener(new VillagerListener());
        registerBedwarsListener(new WorldListener());
        if (Main.getConfigurator().node("bungee", "enabled").getBoolean() && Main.getConfigurator().node("bungee", "motd", "enabled").getBoolean()) {
            registerBedwarsListener(new BungeeMotdListener());
        }

        if (partiesEnabled) {
            registerBedwarsListener(new PartyListener());
        }

        if (controllable == null) {
            controllable = InitUtils.pluginlessEnvironment(controllable1 ->
                SimpleInventoriesBukkit.init(this.getPluginDescription().as(JavaPlugin.class), controllable1.child())
            );
        } else {
            controllable.enable();
            controllable.postEnable();
        }

        this.manager = new HologramManager(this.getPluginDescription().as(JavaPlugin.class));

        SpecialRegister.onEnable(this.getPluginDescription().as(JavaPlugin.class));

        PremiumBedwars.init();

        Bukkit.getServer().getServicesManager().register(BedwarsAPI.class, this, this.getPluginDescription().as(JavaPlugin.class), ServicePriority.Normal);

        configurator.node("resources").childrenMap().forEach((spawnerK, node) -> {
                var name = node.node("name").getString();
                var translate = node.node("translate").getString();
                var interval = node.node("interval").getInt(1);
                var spread = node.node("spread").getDouble();
                var damage = node.node("damage").getInt();
                var materialName = node.node("material").getString();
                var colorName = node.node("color").getString();

                var spawnerN = spawnerK.toString();

                if (damage != 0) {
                    materialName += ":" + damage;
                }

                var result = MaterialMapping.resolve(materialName).orElse(MaterialMapping.getAir());
                if (result.as(Material.class) == Material.AIR) {
                    return;
                }

                ChatColor color;
                try {
                    color = ChatColor.valueOf(colorName.toUpperCase());
                } catch (IllegalArgumentException | NullPointerException ignored) {
                    color = ChatColor.WHITE;
                }
                spawnerTypes.put(spawnerN.toLowerCase(), new ItemSpawnerType(spawnerN.toLowerCase(), name, translate,
                        spread, result.as(Material.class), color, interval, result.getDurability()));
        });

        menu = new ShopInventory();

        if (getConfigurator().node("bungee", "enabled").getBoolean()) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this.getPluginDescription().as(JavaPlugin.class), "BungeeCord");
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "============" + ChatColor.RED + "===" + ChatColor.WHITE + "======  by ScreamingSandals <Misat11, Ceph, Pronze>");
        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.AQUA + "+ Screaming " + ChatColor.RED + "Bed" + ChatColor.WHITE + "Wars +  " + ChatColor.GOLD + "Version: " + version + " " + (PremiumBedwars.isPremium() ? ChatColor.AQUA + "PREMIUM" : ChatColor.GREEN + "FREE"));
        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.AQUA + "============" + ChatColor.RED + "===" + ChatColor.WHITE + "======  " + (snapshot ? ChatColor.RED + "SNAPSHOT VERSION (" + buildInfo + ") - Use at your own risk" : ChatColor.GREEN + "STABLE VERSION"));
        if (isVault) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.GOLD + "Found Vault");
        }
        if (!isSpigot) {
            Bukkit.getConsoleSender()
                    .sendMessage(ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.RED + "WARNING: You are not using Spigot. Some features may not work properly.");
        }

        if (versionNumber < 109) {
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.RED + "IMPORTANT WARNING: You are using version older than 1.9! This version is not officially supported, and some features may not work at all!");
        }

        final var arenasFolder = getPluginDescription().getDataFolder().resolve("arenas").toFile();
        if (arenasFolder.exists()) {
            try (var stream = Files.walk(Paths.get(arenasFolder.getAbsolutePath()))) {
                final var results = stream.filter(Files::isRegularFile)
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

        final var signOwner = new BedWarsSignOwner();
        signManager = new SignManager(signOwner, YamlConfigurationLoader.builder()
                .path(getPluginDescription().getDataFolder().resolve("database/sign.yml"))
                .build()
        );
        registerBedwarsListener(new SignListener(signOwner, signManager));

        try {
            // Fixing bugs created by third party plugin

            // PerWorldInventory
            if (Bukkit.getPluginManager().isPluginEnabled("PerWorldInventory")) {
                final var pwi = Bukkit.getPluginManager().getPlugin("PerWorldInventory");
                if (pwi.getClass().getName().equals("me.ebonjaeger.perworldinventory.PerWorldInventory")) {
                    // Kotlin version
                    registerBedwarsListener(new PerWorldInventoryKotlinListener());
                } else {
                    // Legacy version
                    registerBedwarsListener(new PerWorldInventoryLegacyListener());
                }
            }

        } catch (Throwable ignored) {
            // maybe something here can cause exception
        }

        if (Main.getConfigurator().node("tab", "enabled").getBoolean()) {
            tabManager = new TabManager();
        }

        if (Main.getConfigurator().node("update-checker", "console").getBoolean()
                || Main.getConfigurator().node("update-checker", "admins").getBoolean()) {
            UpdateChecker.run();
        }

        /* Initialize our ScoreboardLib*/
        ScoreboardManager.init(this.getPluginDescription().as(JavaPlugin.class));

        final var pluginId = 7147;
        metrics = new Metrics(this.getPluginDescription().as(JavaPlugin.class), pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("edition", () -> PremiumBedwars.isPremium() ? "Premium" : "Free"));
        metrics.addCustomChart(new Metrics.SimplePie("build_number", () -> buildInfo));

        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "Everything is loaded! If you like our work, consider visiting our Patreon! <3");
        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "https://www.patreon.com/screamingsandals");
    }

    public void onDisable() {
        controllable.preDisable();
        isDisabling = true;
        if (signManager != null) {
            signManager.save();
        }
        for (var game : games.values()) {
            game.stop();
        }
        Bukkit.getServer().getServicesManager().unregisterAll(this.getPluginDescription().as(JavaPlugin.class));

        if (isHologramsEnabled() && hologramInteraction != null) {
            hologramInteraction.unloadHolograms();
            leaderboardHolograms.unloadHolograms();
        }

        metrics = null;

        controllable.disable();
    }

    private boolean setupEconomy() {
        var plugin = PluginManager.getPlugin(PluginManager.createKey("Vault").orElseThrow());
        if (plugin.isEmpty()) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
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

    public org.screamingsandals.bedwars.api.game.Game getFirstRunningGame() {
        final TreeMap<Integer, Game> availableGames = new TreeMap<>();
        games.values().forEach(game -> {
            if (game.getStatus() != GameStatus.RUNNING && game.getStatus() != GameStatus.GAME_END_CELEBRATING) {
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
        return configurator.node("bungee", "server").getString();
    }

    @Override
    public PlayerStatisticsManager getStatisticsManager() {
        return isPlayerStatisticsEnabled() ? playerStatisticsManager : null;
    }

    public static boolean isDisabling() {
        return instance.isDisabling;
    }

    public void registerBedwarsListener(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, this.getPluginDescription().as(JavaPlugin.class));
    }
}
