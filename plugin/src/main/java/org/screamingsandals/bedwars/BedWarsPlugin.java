package org.screamingsandals.bedwars;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.api.entities.EntitiesManager;
import org.screamingsandals.bedwars.api.game.GameManager;
import org.screamingsandals.bedwars.api.player.PlayerManager;
import org.screamingsandals.bedwars.commands.CommandService;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.RecordSave;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.*;
import org.screamingsandals.bedwars.lang.BedWarsLangService;
import org.screamingsandals.bedwars.lang.LangKeys;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.premium.PremiumBedwars;
import org.screamingsandals.bedwars.special.SpecialRegister;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.tab.TabManager;
import org.screamingsandals.bedwars.utils.*;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.lib.healthindicator.HealthIndicatorManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.material.Item;
import org.screamingsandals.lib.material.MaterialHolder;
import org.screamingsandals.lib.material.MaterialMapping;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.plugin.PluginContainer;
import org.screamingsandals.lib.plugin.PluginManager;
import org.screamingsandals.lib.sidebar.SidebarManager;
import org.screamingsandals.lib.utils.PlatformType;
import org.screamingsandals.lib.utils.annotations.Init;
import org.screamingsandals.lib.utils.annotations.Plugin;
import org.screamingsandals.lib.utils.annotations.PluginDependencies;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

@Plugin(
        id = "BedWars",
        authors = {"ScreamingSandals <Misat11, iamceph, Pronze>"},
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
        "Parties"
})
@Init(services = {
        CommandService.class,
        GameManagerImpl.class,
        UpdateChecker.class,
        PlayerStatisticManager.class,
        StatisticsHolograms.class,
        LeaderboardHolograms.class,
        MainConfig.class,
        BedWarsLangService.class,
        TabManager.class,
        ShopInventory.class,
        SpecialRegister.class,
        RecordSave.class,
        DatabaseManager.class,
        BedWarsSignService.class,
        BukkitBStatsMetrics.class,
        PlayerManagerImpl.class,
        PartyListener.class,
        EventUtils.class,
        LobbyInvisibilityListener.class,
        BedwarsExpansion.class,
        SidebarManager.class,
        HealthIndicatorManager.class,
        BungeeMotdListener.class,
        WorldListener.class,
        VillagerListener.class,
        PlayerListener.class,
        NPCUtils.class,
        EntitiesManagerImpl.class
})
public class BedWarsPlugin extends PluginContainer implements BedwarsAPI {
    private static BedWarsPlugin instance;

    private String version;
    private boolean isDisabling = false;
    private boolean isLegacy;
    private boolean isVault;
    private int versionNumber = 0;
    private Economy econ = null;
    private HashMap<String, ItemSpawnerType> spawnerTypes = new HashMap<>();
    private ColorChanger colorChanger;
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

    public static BedWarsPlugin getInstance() {
        return instance;
    }

    public static String getVersion() {
        return instance.version;
    }

    public static boolean isVault() {
        return instance.isVault;
    }

    public static boolean isLegacy() {
        return instance.isLegacy;
    }

    @Deprecated
    public static void depositPlayer(Player player, double coins) {
        try {
            if (isVault() && MainConfig.getInstance().node("vault", "enabled").getBoolean()) {
                EconomyResponse response = instance.econ.depositPlayer(player, coins);
                if (response.transactionSuccess()) {
                    Message
                            .of(LangKeys.IN_GAME_VAULT_DEPOSITE)
                            .defaultPrefix()
                            .placeholder("coins", coins)
                            .placeholder("currency",  (coins == 1 ? instance.econ.currencyNameSingular() : instance.econ.currencyNamePlural()))
                            .send(PlayerMapper.wrapPlayer(player));
                }
            }
        } catch (Throwable ignored) {
        }
    }

    public static void depositPlayer(PlayerWrapper player, double coins) {
        try {
            if (isVault() && MainConfig.getInstance().node("vault", "enabled").getBoolean()) {
                var response = instance.econ.depositPlayer(player.as(Player.class), coins);
                if (response.transactionSuccess()) {
                    Message
                            .of(LangKeys.IN_GAME_VAULT_DEPOSITE)
                            .defaultPrefix()
                            .placeholder("coins", coins)
                            .placeholder("currency",  (coins == 1 ? instance.econ.currencyNameSingular() : instance.econ.currencyNamePlural()))
                            .send(player);
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

    @Deprecated
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

    public static boolean isFarmBlock(MaterialHolder mat) {
        if (MainConfig.getInstance().node("ignored-blocks", "enabled").getBoolean()) {
            try {
                return mat.is(MainConfig.getInstance().node("ignored-blocks", "blocks").getList(String.class).toArray());
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

    public static int getVersionNumber() {
        return instance.versionNumber;
    }

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {
        instance = this;
        version = this.getPluginDescription().getVersion();
        var snapshot = version.toLowerCase().contains("pre") || version.toLowerCase().contains("snapshot");
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

        PlayerMapper.getConsoleSender().sendMessage(Component
                .text("============")
                .color(NamedTextColor.AQUA)
                .append(
                        Component
                                .text("===")
                                .color(NamedTextColor.RED)
                )
                .append(
                        Component
                                .text("======  by ScreamingSandals <Misat11, Iamceph, Pronze>")
                                .color(NamedTextColor.WHITE)
                )
        );

        PlayerMapper.getConsoleSender().sendMessage(Component
                .text("+ Screaming ")
                .color(NamedTextColor.AQUA)
                .append(
                        Component
                                .text("Bed")
                                .color(NamedTextColor.RED)
                )
                .append(
                        Component
                                .text("Wars +  ")
                                .color(NamedTextColor.WHITE)
                )
                .append(
                        Component
                                .text("Version: " + version + " ")
                                .color(NamedTextColor.GOLD)
                )
                .append(
                        Component
                                .text(PremiumBedwars.isPremium() ? "PREMIUM" : "FREE")
                                .color(PremiumBedwars.isPremium() ? NamedTextColor.AQUA : NamedTextColor.GREEN)
                )
        );

        PlayerMapper.getConsoleSender().sendMessage(Component
                .text("============")
                .color(NamedTextColor.AQUA)
                .append(
                        Component
                                .text("===")
                                .color(NamedTextColor.RED)
                )
                .append(
                        Component
                                .text("======  ")
                                .color(NamedTextColor.WHITE)
                )
                .append(
                        Component
                                .text(snapshot ? "SNAPSHOT VERSION (" + VersionInfo.BUILD_NUMBER + ") - Use at your own risk" : "STABLE VERSION")
                                .color(snapshot ? NamedTextColor.RED : NamedTextColor.GREEN)
                )
        );

        if (isVault) {
            PlayerMapper.getConsoleSender().sendMessage(
                    Component
                            .text("[B")
                            .color(NamedTextColor.RED)
                            .append(
                                    Component
                                            .text("W] ")
                                            .color(NamedTextColor.WHITE)
                            )
                            .append(
                                    Component
                                            .text("Found Vault")
                                            .color(NamedTextColor.GOLD)
                            ));
        }

        if (versionNumber < 109) {
            PlayerMapper.getConsoleSender().sendMessage(
                    Component
                            .text("[B")
                            .color(NamedTextColor.RED)
                            .append(
                                    Component
                                            .text("W] ")
                                            .color(NamedTextColor.WHITE)
                            )
                            .append(
                                    Component
                                            .text("IMPORTANT WARNING: You are using a version which is older than 1.9! This version is not officially supported and some features may not work at all! Update your server version to remove this message.")
                                            .color(NamedTextColor.RED)
                            ));
        }

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

        PlayerMapper.getConsoleSender().sendMessage(Component.text("Everything has finished loading! If you like our work, consider subscribing to our Patreon! <3").color(NamedTextColor.WHITE));
        PlayerMapper.getConsoleSender().sendMessage(Component.text("https://www.patreon.com/screamingsandals").color(NamedTextColor.WHITE));
    }

    @Override
    public void disable() {
        isDisabling = true;
        Bukkit.getServer().getServicesManager().unregisterAll(this.getPluginDescription().as(JavaPlugin.class));
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
    public GameManager<?> getGameManager() {
        return GameManagerImpl.getInstance();
    }

    @Override
    public PlayerManager<?,?> getPlayerManager() {
        return PlayerManagerImpl.getInstance();
    }

    @Override
    public EntitiesManager<?, ?> getEntitiesManager() {
        return EntitiesManagerImpl.getInstance();
    }

    @Override
    public org.screamingsandals.bedwars.api.utils.EventUtils getEventUtils() {
        return EventUtils.getInstance();
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
    public String getPluginVersion() {
        return version;
    }

    @Override
    public ColorChanger<Item> getColorChanger() {
        return colorChanger;
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
