package org.screamingsandals.bedwars;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.screamingsandals.bedwars.api.entities.EntitiesManager;
import org.screamingsandals.bedwars.api.game.GameManager;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.bedwars.api.player.PlayerManager;
import org.screamingsandals.bedwars.api.variants.VariantManager;
import org.screamingsandals.bedwars.commands.CommandService;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.RecordSave;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.*;
import org.screamingsandals.bedwars.inventories.GamesInventory;
import org.screamingsandals.bedwars.lang.BedWarsLangService;
import org.screamingsandals.bedwars.api.BedwarsAPI;
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
import org.screamingsandals.bedwars.variants.VariantManagerImpl;
import org.screamingsandals.lib.CustomPayload;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.healthindicator.HealthIndicatorManager;
import org.screamingsandals.lib.item.ItemTypeHolder;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.plugin.PluginContainer;
import org.screamingsandals.lib.plugin.ServiceManager;
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
        VariantManagerImpl.class,
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
        EventUtilsImpl.class,
        LobbyInvisibilityListener.class,
        BedwarsExpansion.class,
        SidebarManager.class,
        HealthIndicatorManager.class,
        BungeeMotdListener.class,
        WorldListener.class,
        VillagerListener.class,
        PlayerListener.class,
        NPCUtils.class,
        EntitiesManagerImpl.class,
        ColorChangerImpl.class,
        VaultUtils.class,
        PerWorldInventoryCompatibilityFix.class,
        GamesInventory.class
})
public class BedWarsPlugin extends PluginContainer implements BedwarsAPI {
    private static BedWarsPlugin instance;

    private String version;
    private boolean isDisabling = false;
    private boolean isLegacy;
    private final HashMap<String, ItemSpawnerTypeImpl> spawnerTypes = new HashMap<>();

    public static BedWarsPlugin getInstance() {
        return instance;
    }

    public static String getVersion() {
        return instance.version;
    }

    public static boolean isLegacy() {
        return instance.isLegacy;
    }

    public static int getVaultKillReward() {
        return MainConfig.getInstance().node("vault", "reward", "kill").getInt();
    }

    public static int getVaultWinReward() {
        return MainConfig.getInstance().node("vault", "reward", "win").getInt();
    }

    public static boolean isFarmBlock(BlockTypeHolder mat) {
        if (MainConfig.getInstance().node("ignored-blocks", "enabled").getBoolean()) {
            try {
                return mat.isSameType(Objects.requireNonNull(MainConfig.getInstance().node("ignored-blocks", "blocks").getList(String.class)).toArray());
            } catch (SerializationException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isBreakableBlock(BlockTypeHolder mat) {
        if (MainConfig.getInstance().node("breakable", "enabled").getBoolean()) {
            try {
                var list = MainConfig.getInstance().node("breakable", "blocks").getList(String.class);
                boolean asblacklist = MainConfig.getInstance().node("breakable", "blacklist-mode").getBoolean();
                return Objects.requireNonNull(list).contains(mat.platformName()) != asblacklist;
            } catch (SerializationException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isCommandLeaveShortcut(String command) {
        if (MainConfig.getInstance().node("leaveshortcuts", "enabled").getBoolean()) {
            try {
                var commands = Objects.requireNonNull(MainConfig.getInstance().node("leaveshortcuts", "list").getList(String.class));
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
            var commands = Objects.requireNonNull(MainConfig.getInstance().node("commands", "list").getList(String.class));
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

    public static ItemSpawnerTypeImpl getSpawnerType(String key) {
        return instance.spawnerTypes.get(key);
    }

    public static List<String> getAllSpawnerTypes() {
        return List.copyOf(instance.spawnerTypes.keySet());
    }

    @Override
    public void load() {
        instance = this;
        version = this.getPluginDescription().getVersion();
        BedwarsAPI.Internal.setBedWarsAPI(this);
    }

    @Override
    public void enable() {
        var snapshot = version.toLowerCase().contains("pre") || version.toLowerCase().contains("snapshot");

        isLegacy = !Server.isVersion(1, 13);

        Debug.init(getPluginDescription().getName());
        Debug.setDebug(MainConfig.getInstance().node("debug").getBoolean());

        PremiumBedwars.init();

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

            var result = ItemTypeHolder.ofOptional(materialName).orElse(ItemTypeHolder.air());
            if (result.isAir()) {
                return;
            }

            spawnerTypes.put(spawnerN.toLowerCase(), new ItemSpawnerTypeImpl(spawnerN.toLowerCase(), name, translate,
                    spread, result, MiscUtils.getColor(colorName), interval, result.durability()));
        });

        if (MainConfig.getInstance().node("bungee", "enabled").getBoolean()) {
            CustomPayload.registerOutgoingChannel("BungeeCord");
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

        PlayerMapper.getConsoleSender().sendMessage(Component.text("Everything has finished loading! If you like our work, consider subscribing to our Patreon! <3").color(NamedTextColor.WHITE));
        PlayerMapper.getConsoleSender().sendMessage(Component.text("https://www.patreon.com/screamingsandals").color(NamedTextColor.WHITE));
    }

    @Override
    public void disable() {
        isDisabling = true;
    }

    @Override
    public GameManager<?> getGameManager() {
        return GameManagerImpl.getInstance();
    }

    @Override
    public VariantManager getVariantManager() {
        return VariantManagerImpl.getInstance();
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
    public EventUtilsImpl getEventUtils() {
        return EventUtilsImpl.getInstance();
    }

    @Override
    public List<ItemSpawnerType> getItemSpawnerTypes() {
        return new ArrayList<>(spawnerTypes.values());
    }

    @Override
    public ItemSpawnerTypeImpl getItemSpawnerTypeByName(String name) {
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
    public ColorChangerImpl getColorChanger() {
        return ServiceManager.get(ColorChangerImpl.class);
    }

    @Override
    public String getHubServerName() {
        return MainConfig.getInstance().node("bungee", "server").getString();
    }

    @Override
    public PlayerStatisticManager getStatisticsManager() {
        return PlayerStatisticManager.isEnabled() ? PlayerStatisticManager.getInstance() : null;
    }

    public static boolean isDisabling() {
        return instance.isDisabling;
    }
}
