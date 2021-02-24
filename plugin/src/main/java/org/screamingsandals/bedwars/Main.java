package org.screamingsandals.bedwars;

import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.commands.CommandService;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.RecordSave;
import org.screamingsandals.bedwars.game.*;
import org.screamingsandals.bedwars.lib.lang.I18n;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.statistics.PlayerStatisticsManager;
import org.screamingsandals.bedwars.api.utils.ColorChanger;
import org.screamingsandals.bedwars.database.DatabaseManager;
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
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.plugin.PluginContainer;
import org.screamingsandals.lib.plugin.PluginManager;
import org.screamingsandals.lib.utils.PlatformType;
import org.screamingsandals.lib.utils.annotations.Init;
import org.screamingsandals.lib.utils.annotations.Plugin;
import org.screamingsandals.lib.utils.annotations.PluginDependencies;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import pronze.lib.scoreboards.ScoreboardManager;

import java.util.*;

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
        EventManager.class,
        CommandService.class,
        GameManager.class,
        UpdateChecker.class,
        PlayerStatisticManager.class,
        StatisticsHolograms.class,
        LeaderboardHolograms.class,
        MainConfig.class,
        TabManager.class,
        ShopInventory.class,
        SpecialRegister.class,
        RecordSave.class,
        DatabaseManager.class
})
public class Main extends PluginContainer implements BedwarsAPI {
    private static Main instance;

    private String version;
    private boolean isDisabling = false;
    private boolean isSpigot, isLegacy;
    private boolean isVault;
    private boolean isNMS;
    private int versionNumber = 0;
    private Economy econ = null;
    private HashMap<Player, GamePlayer> playersInGame = new HashMap<>();
    private HashMap<Entity, Game> entitiesInGame = new HashMap<>();
    private HashMap<String, ItemSpawnerType> spawnerTypes = new HashMap<>();
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
            if (isVault() && MainConfig.getInstance().node("vault", "enabled").getBoolean()) {
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
        return MainConfig.getInstance().node("vault", "reward", "kill").getInt();
    }

    public static int getVaultWinReward() {
        return MainConfig.getInstance().node("vault", "reward", "win").getInt();
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

    public static void openStore(Player player, GameStore store) {
        ShopInventory.getInstance().show(PlayerMapper.wrapPlayer(player), store);
    }

    public static boolean isFarmBlock(Material mat) {
        if (MainConfig.getInstance().node("ignored-blocks", "enabled").getBoolean()) {
            try {
                return MainConfig.getInstance().node("ignored-blocks", "blocks").getList(String.class).contains(mat.name());
            } catch (SerializationException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isBreakableBlock(Material mat) {
        if (MainConfig.getInstance().node("breakable", "enabled").getBoolean()) {
            try {
                var list = MainConfig.getInstance().node("breakable", "blocks").getList(String.class);
                boolean asblacklist = MainConfig.getInstance().node("breakable", "blacklist-mode").getBoolean();
                return list.contains(mat.name()) != asblacklist;
            } catch (SerializationException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isCommandLeaveShortcut(String command) {
        if (MainConfig.getInstance().node("leaveshortcuts", "enabled").getBoolean()) {
            try {
                var commands = MainConfig.getInstance().node("leaveshortcuts", "list").getList(String.class);
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
            var commands = MainConfig.getInstance().node("commands", "list").getList(String.class);
            for (var comm : commands) {
                if (!comm.startsWith("/")) {
                    comm = "/" + comm;
                }
                if (comm.equals(commandPref)) {
                    return !MainConfig.getInstance().node("commands", "blacklist-mode").getBoolean();
                }
            }
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return MainConfig.getInstance().node("commands", "blacklist-mode").getBoolean();
    }

    public static ItemSpawnerType getSpawnerType(String key) {
        return instance.spawnerTypes.get(key);
    }

    public static List<String> getAllSpawnerTypes() {
        return new ArrayList<>(instance.spawnerTypes.keySet());
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

    @Override
    public void enable() {
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

        Debug.init(getPluginDescription().getName());
        Debug.setDebug(MainConfig.getInstance().node("debug").getBoolean());

        I18n.load(this.getPluginDescription().as(JavaPlugin.class), MainConfig.getInstance().node("locale").getString("en"));

        try {
            if (PluginManager.isEnabled(PluginManager.createKey("PlaceholderAPI").orElseThrow())) {
                new BedwarsExpansion().register();
            }
        } catch (Throwable ignored) {
        }

        var partiesEnabled = false;

        if (MainConfig.getInstance().node("party", "enabled").getBoolean()) {
            final var partyPlugin = PluginManager.getPlugin(PluginManager.createKey("Parties").orElseThrow());
            if (partyPlugin.isPresent() && partyPlugin.get().isEnabled()) {
                partiesEnabled = true;
            }
        }

        registerBedwarsListener(new PlayerListener());
        if (versionNumber >= 109) {
            registerBedwarsListener(new Player19Listener());
        }

        final var playerBeforeOrAfter112Listener = versionNumber >= 122 ? new Player112Listener() : new PlayerBefore112Listener();
        registerBedwarsListener(playerBeforeOrAfter112Listener);

        registerBedwarsListener(new VillagerListener());
        registerBedwarsListener(new WorldListener());
        if (MainConfig.getInstance().node("bungee", "enabled").getBoolean() && MainConfig.getInstance().node("bungee", "motd", "enabled").getBoolean()) {
            registerBedwarsListener(new BungeeMotdListener());
        }

        if (partiesEnabled) {
            registerBedwarsListener(new PartyListener());
        }

        this.manager = new HologramManager(this.getPluginDescription().as(JavaPlugin.class));

        PremiumBedwars.init();

        Bukkit.getServer().getServicesManager().register(BedwarsAPI.class, this, this.getPluginDescription().as(JavaPlugin.class), ServicePriority.Normal);

        MainConfig.getInstance().node("resources").childrenMap().forEach((spawnerK, node) -> {
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

        if (MainConfig.getInstance().node("bungee", "enabled").getBoolean()) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this.getPluginDescription().as(JavaPlugin.class), "BungeeCord");
        }

        PlayerMapper.getConsoleSender().sendMessage(ChatColor.AQUA + "============" + ChatColor.RED + "===" + ChatColor.WHITE + "======  by ScreamingSandals <Misat11, Ceph, Pronze>");
        PlayerMapper.getConsoleSender()
                .sendMessage(ChatColor.AQUA + "+ Screaming " + ChatColor.RED + "Bed" + ChatColor.WHITE + "Wars +  " + ChatColor.GOLD + "Version: " + version + " " + (PremiumBedwars.isPremium() ? ChatColor.AQUA + "PREMIUM" : ChatColor.GREEN + "FREE"));
        PlayerMapper.getConsoleSender()
                .sendMessage(ChatColor.AQUA + "============" + ChatColor.RED + "===" + ChatColor.WHITE + "======  " + (snapshot ? ChatColor.RED + "SNAPSHOT VERSION (" + VersionInfo.BUILD_NUMBER + ") - Use at your own risk" : ChatColor.GREEN + "STABLE VERSION"));
        if (isVault) {
            PlayerMapper.getConsoleSender().sendMessage(ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.GOLD + "Found Vault");
        }
        if (!isSpigot) {
            PlayerMapper.getConsoleSender()
                    .sendMessage(ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.RED + "WARNING: You are not using Spigot. Some features may not work properly.");
        }

        if (versionNumber < 109) {
            PlayerMapper.getConsoleSender().sendMessage(
                    ChatColor.RED + "[B" + ChatColor.WHITE + "W] " + ChatColor.RED + "IMPORTANT WARNING: You are using version older than 1.9! This version is not officially supported, and some features may not work at all!");
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

        /* Initialize our ScoreboardLib*/
        ScoreboardManager.init(this.getPluginDescription().as(JavaPlugin.class));

        final var pluginId = 7147;
        metrics = new Metrics(this.getPluginDescription().as(JavaPlugin.class), pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("edition", () -> PremiumBedwars.isPremium() ? "Premium" : "Free"));
        metrics.addCustomChart(new Metrics.SimplePie("build_number", () -> VersionInfo.BUILD_NUMBER));

        PlayerMapper.getConsoleSender().sendMessage(ChatColor.WHITE + "Everything is loaded! If you like our work, consider visiting our Patreon! <3");
        PlayerMapper.getConsoleSender().sendMessage(ChatColor.WHITE + "https://www.patreon.com/screamingsandals");
    }

    @Override
    public void disable() {
        isDisabling = true;
        if (signManager != null) {
            signManager.save();
        }
        Bukkit.getServer().getServicesManager().unregisterAll(this.getPluginDescription().as(JavaPlugin.class));

        metrics = null;
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
    public org.screamingsandals.bedwars.api.game.GameManager<?> getGameManager() {
        return GameManager.getInstance();
    }

    @Override
    public org.screamingsandals.bedwars.api.game.Game getGameOfPlayer(Player player) {
        return isPlayerInGame(player) ? getPlayerGameProfile(player).getGame() : null;
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
    public String getHubServerName() {
        return MainConfig.getInstance().node("bungee", "server").getString();
    }

    @Override
    public PlayerStatisticsManager<?> getStatisticsManager() {
        return PlayerStatisticManager.isEnabled() ? PlayerStatisticManager.getInstance() : null;
    }

    public static boolean isDisabling() {
        return instance.isDisabling;
    }

    public void registerBedwarsListener(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, this.getPluginDescription().as(JavaPlugin.class));
    }
}
