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

package org.screamingsandals.bedwars;

import lombok.Getter;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.commands.CommandService;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.RecordSave;
import org.screamingsandals.bedwars.database.DatabaseManager;
import org.screamingsandals.bedwars.econ.EconomyProvider;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.ItemSpawnerTypeImpl;
import org.screamingsandals.bedwars.holograms.LeaderboardHolograms;
import org.screamingsandals.bedwars.holograms.StatisticsHolograms;
import org.screamingsandals.bedwars.inventories.GamesInventory;
import org.screamingsandals.bedwars.inventories.ShopInventory;
import org.screamingsandals.bedwars.lang.BedWarsLangService;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.listener.*;
import org.screamingsandals.bedwars.placeholderapi.BedwarsExpansion;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.premium.PremiumBedwars;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.tab.TabManager;
import org.screamingsandals.bedwars.utils.*;
import org.screamingsandals.bedwars.variants.VariantManagerImpl;
import org.screamingsandals.lib.CustomPayload;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.healthindicator.HealthIndicatorManager;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.plugin.PluginContainer;
import org.screamingsandals.lib.plugin.PluginManager;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.sidebar.SidebarManager;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.utils.PlatformType;
import org.screamingsandals.lib.utils.annotations.Init;
import org.screamingsandals.lib.utils.annotations.Plugin;
import org.screamingsandals.lib.utils.annotations.PluginDependencies;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Plugin(
        id = "ScreamingBedWars",
        authors = {"ScreamingSandals <Misat11, iamceph, Pronze, ZlataOvce>"},
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
@Init(
        services = {
                EconomyProvider.class,
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
                RecordSave.class,
                DatabaseManager.class,
                BedWarsSignService.class,
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
                EntitiesManagerImpl.class,
                ColorChangerImpl.class,
                GamesInventory.class
        },
        packages = {
                "org.screamingsandals.bedwars.special",
                "org.screamingsandals.bedwars.lobby"
        }
)
@Init(platforms = {PlatformType.BUKKIT}, services = {
        PerWorldInventoryCompatibilityFix.class,
        BukkitBStatsMetrics.class
})
public class BedWarsPlugin extends PluginContainer implements BedwarsAPI {
    private static BedWarsPlugin instance;

    private String version;
    private boolean isDisabling = false;
    private boolean isLegacy;
    @Getter
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

    public static int getKillReward() {
        return MainConfig.getInstance().node("economy", "reward", "kill").getInt();
    }

    public static int getWinReward() {
        return MainConfig.getInstance().node("economy", "reward", "win").getInt();
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

    public static ItemSpawnerTypeImpl getSpawnerType(String key, GameImpl game) {
        if (game.getGameVariant() != null) {
            return game.getGameVariant().getItemSpawnerType(key);
        }
        return instance.spawnerTypes.get(key);
    }

    public static List<String> getAllSpawnerTypes(GameImpl game) {
        if (game.getGameVariant() != null) {
            return game.getGameVariant().getItemSpawnerTypeNames();
        }
        return List.copyOf(instance.spawnerTypes.keySet());
    }

    @Override
    public void load() {
        if (PluginManager.getPlatformType() == PlatformType.BUKKIT) {
            var folder = getDataFolder();
            if (!Files.exists(folder)) {
                var sbw0_2_x = folder.getParent().resolve("BedWars");
                /*
                 * I hope it won't copy a folder of a plugin with the same name (actually I haven't found any)
                 * or that there won't be the original Bedwars plugin on case-insensitive system (I'm looking at you Windows)
                 */
                if (Files.exists(sbw0_2_x) && Files.exists(sbw0_2_x.resolve("config.yml"))) {
                    // Hello Screaming BedWars 0.2.x
                    try (var walk = Files.walk(sbw0_2_x)) {
                        Files.createDirectory(folder);

                        walk.forEach(source -> {
                            var f = sbw0_2_x.relativize(source);
                            if (f.toString().isEmpty()) {
                                return; // WTF??
                            }
                            var destination = folder.resolve(f);
                            try {
                                Files.copy(source, destination);
                            } catch (IOException e) {
                                throw new RuntimeException(e); // just crash the migration process
                            }
                        });
                        Files.move(sbw0_2_x, sbw0_2_x.getParent().resolve("BedWars.old"));
                        getLogger().info("Thank you for updating the plugin! We are now in new folder: plugins/ScreamingBedWars :)");
                    } catch (Throwable e) {
                        getLogger().info("We couldn't copy your old SBW 0.2.x setup. Sorry :(");
                        e.printStackTrace();
                    }
                }
            }
        }

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
            var type = ItemSpawnerTypeImpl.deserialize(spawnerK.toString(), node);
            if (type != null) {
                spawnerTypes.put(type.getConfigKey(), type);
            }
        });

        if (MainConfig.getInstance().node("bungee", "enabled").getBoolean()) {
            CustomPayload.registerOutgoingChannel("BungeeCord");
        }

        PlayerMapper.getConsoleSender().sendMessage(Component
                .text()
                .content("============")
                .color(Color.AQUA)
                .append(
                        Component.text("===", Color.RED),
                        Component.text("======  by ScreamingSandals <Misat11, Iamceph, Pronze, Zlataovce>", Color.WHITE)
                )
        );

        PlayerMapper.getConsoleSender().sendMessage(Component
                .text()
                .content("+ Screaming ")
                .color(Color.AQUA)
                .append(
                        Component.text("Bed", Color.RED),
                        Component.text("Wars +  ", Color.WHITE),
                        Component.text("Version: " + version + " ",Color.GOLD),
                        Component.text(PremiumBedwars.isPremium() ? "PREMIUM" : "FREE",PremiumBedwars.isPremium() ? Color.AQUA : Color.GREEN)
                )
        );

        PlayerMapper.getConsoleSender().sendMessage(Component
                .text()
                .content("============")
                .color(Color.AQUA)
                .append(
                        Component.text("===", Color.RED),
                        Component.text("======  ",Color.WHITE),
                        Component.text(snapshot ? "SNAPSHOT VERSION (" + VersionInfo.BUILD_NUMBER + ") - Use at your own risk" : "STABLE VERSION", snapshot ? Color.RED : Color.GREEN)
                )
        );

        PlayerMapper.getConsoleSender().sendMessage(Component.text("Everything has finished loading! If you like our work, consider subscribing to our Patreon! <3", Color.WHITE));
        PlayerMapper.getConsoleSender().sendMessage(Component.text("https://www.patreon.com/screamingsandals", Color.WHITE));
    }

    @Override
    public void disable() {
        isDisabling = true;
    }

    @Override
    public GameManagerImpl getGameManager() {
        return GameManagerImpl.getInstance();
    }

    @Override
    public VariantManagerImpl getVariantManager() {
        return VariantManagerImpl.getInstance();
    }

    @Override
    public PlayerManagerImpl getPlayerManager() {
        return PlayerManagerImpl.getInstance();
    }

    @Override
    public EntitiesManagerImpl getEntitiesManager() {
        return EntitiesManagerImpl.getInstance();
    }

    @Override
    public EventUtilsImpl getEventUtils() {
        return EventUtilsImpl.getInstance();
    }

    @Override
    public List<ItemSpawnerTypeImpl> getItemSpawnerTypes() {
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
