/*
 * Copyright (C) 2024 ScreamingSandals
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
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.commands.CommandService;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.RecordSave;
import org.screamingsandals.bedwars.database.DatabaseManager;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.GroupManagerImpl;
import org.screamingsandals.bedwars.game.ItemSpawnerTypeImpl;
import org.screamingsandals.bedwars.game.LocalGameLoaderImpl;
import org.screamingsandals.bedwars.game.remote.RemoteGameLoaderImpl;
import org.screamingsandals.bedwars.game.remote.RemoteGameStateManager;
import org.screamingsandals.bedwars.game.remote.ServerNameChangeEvent;
import org.screamingsandals.bedwars.game.remote.protocol.ProtocolManagerImpl;
import org.screamingsandals.bedwars.holograms.LeaderboardHolograms;
import org.screamingsandals.bedwars.holograms.StatisticsHolograms;
import org.screamingsandals.bedwars.inventories.GamesInventory;
import org.screamingsandals.bedwars.inventories.ShopInventory;
import org.screamingsandals.bedwars.lang.BedWarsLangService;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.listener.*;
import org.screamingsandals.bedwars.placeholderapi.BedwarsExpansion;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.tab.TabManager;
import org.screamingsandals.bedwars.utils.*;
import org.screamingsandals.bedwars.variants.VariantLoaderImpl;
import org.screamingsandals.bedwars.variants.VariantManagerImpl;
import org.screamingsandals.lib.CustomPayload;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.ai.AiManager;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.economy.EconomyManager;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.fakedeath.FakeDeath;
import org.screamingsandals.lib.healthindicator.HealthIndicatorManager;
import org.screamingsandals.lib.hologram.HologramManager;
import org.screamingsandals.lib.plugin.PluginUtils;
import org.screamingsandals.lib.plugin.Plugins;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.sidebar.SidebarManager;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.utils.PlatformType;
import org.screamingsandals.lib.utils.ProxyType;
import org.screamingsandals.lib.utils.annotations.Init;
import org.screamingsandals.lib.utils.annotations.Plugin;
import org.screamingsandals.lib.utils.annotations.PluginDependencies;
import org.screamingsandals.lib.utils.annotations.methods.OnDisable;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPluginLoad;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.screamingsandals.lib.utils.logger.Logger;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

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
                EconomyManager.class,
                PlatformService.class,
                AiManager.class,
                FakeDeath.class,
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
                WorldListener.class,
                VillagerListener.class,
                PlayerListener.class,
                EntitiesManagerImpl.class,
                ColorChangerImpl.class,
                GamesInventory.class,
                GroupManagerImpl.class,
                TargetInvalidatedListener.class,
                LocalGameLoaderImpl.class,
                RemoteGameLoaderImpl.class,
                ProtocolManagerImpl.class,
                RemoteGameStateManager.class,
                BungeeMotdListener.class,
                VariantLoaderImpl.class
        },
        packages = {
                "org.screamingsandals.bedwars.special",
                "org.screamingsandals.bedwars.lobby"
        }
)
@RequiredArgsConstructor
public class BedWarsPlugin implements BedwarsAPI {
    public final @NotNull String SERVER_NAME_SYSTEM_PROPERTY_NAME = "org.screamingsandals.bedwars.serverName";

    @Getter
    private final org.screamingsandals.lib.plugin.@NotNull Plugin pluginDescription;
    @ConfigFile("serverName.txt")
    private final Path serverNameFile;
    @Getter
    private final @NotNull Logger logger;
    private static BedWarsPlugin instance;

    private boolean isDisabling = false;
    @Getter
    private final HashMap<String, ItemSpawnerTypeImpl> spawnerTypes = new HashMap<>();
    @Getter
    private @Nullable String serverName;
    @Getter
    private @Nullable List<@NotNull String> bungeeServers;

    public static BedWarsPlugin getInstance() {
        return instance;
    }

    public static boolean isFarmBlock(Block mat) {
        if (MainConfig.getInstance().node("ignored-blocks", "enabled").getBoolean()) {
            try {
                return mat.isSameType(Objects.requireNonNull(MainConfig.getInstance().node("ignored-blocks", "blocks").getList(String.class)).toArray());
            } catch (SerializationException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isBreakableBlock(Block mat) {
        if (MainConfig.getInstance().node("breakable", "enabled").getBoolean()) {
            try {
                var list = MainConfig.getInstance().node("breakable", "blocks").getList(String.class);
                boolean asblacklist = MainConfig.getInstance().node("breakable", "blacklist-mode").getBoolean();
                return (list != null && mat.isSameType(list.toArray())) != asblacklist;
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

    public static void processFakeDeath(@NotNull BedWarsPlayer bedWarsPlayer) {
        /* BedWars Player is required = they have custom teleport method */

        FakeDeath.die(bedWarsPlayer, (player, keepInventory) -> {
            if (keepInventory) {
                bedWarsPlayer.invClean();
            }
            bedWarsPlayer.resetLife();
        });
    }

    @OnPluginLoad
    public void load() {
        if (Plugins.getPlatformType() == PlatformType.BUKKIT) {
            var folder = pluginDescription.dataFolder();
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
                        logger.info("Thank you for updating the plugin! We are now in new folder: plugins/ScreamingBedWars :)");
                    } catch (Throwable e) {
                        logger.error("We couldn't copy your old SBW 0.2.x setup. Sorry :(", e);
                    }
                }
            }
        }

        instance = this;
        BedwarsAPI.Internal.setBedWarsAPI(this);
    }

    @OnEnable
    public void enable() {
        var snapshot = VersionInfo.VERSION.toLowerCase(Locale.ROOT).contains("pre") || VersionInfo.VERSION.toLowerCase(Locale.ROOT).contains("snapshot");

        Debug.init(pluginDescription.name());
        Debug.setDebug(MainConfig.getInstance().node("debug").getBoolean());

        MainConfig.getInstance().node("resources").childrenMap().forEach((spawnerK, node) -> {
            var type = ItemSpawnerTypeImpl.deserialize(spawnerK.toString(), node);
            if (type != null) {
                spawnerTypes.put(type.getConfigKey(), type);
            }
        });

        var serverNameFromProperty = System.getProperty(SERVER_NAME_SYSTEM_PROPERTY_NAME);
        var usesProperty = serverNameFromProperty != null && !serverNameFromProperty.isBlank();
        if (usesProperty) {
            serverName = serverNameFromProperty;
        }

        if (!usesProperty && Files.exists(serverNameFile)) {
            try {
                serverName = Files.readString(serverNameFile);
            } catch (IOException e) {
                logger.error("An error occurred while reading serverName.txt file", e);
            }
        }

        if (Server.getProxyType() != ProxyType.NONE) {
            CustomPayload.registerOutgoingChannel("BungeeCord");
            CustomPayload.registerIncomingChannel("BungeeCord", (player, bytes) -> {
                var in = new DataInputStream(new ByteArrayInputStream(bytes));

                try {
                    var channel = in.readUTF();
                    if ("GetServer".equals(channel)) {
                        if (usesProperty) {
                            return;
                        }

                        var newServerName = in.readUTF();
                        if (!newServerName.equals(serverName)) {
                            serverName = newServerName;

                            Files.writeString(serverNameFile, serverName);

                            // Notify other parts of the plugin that the server name has been updated
                            EventManager.fire(new ServerNameChangeEvent());
                        }
                    } else if ("GetServers".equals(channel)) {
                        bungeeServers = Arrays.asList(in.readUTF().split(", "));
                    }
                } catch (IOException e) {
                    logger.error("An error occurred while handling BungeeCord message", e);
                }
            });
        }

        if (!VersionInfo.VERSION.equals(pluginDescription.version())) {
            Server.getConsoleSender().sendMessage(Component.text()
                    .content("Version in plugin.yml of ScreamingBedWars has been modified! Expected ")
                    .color(Color.RED)
                    .append(Component.text(VersionInfo.VERSION, Color.GRAY))
                    .append(", got")
                    .append(Component.text(pluginDescription.version(), Color.GRAY))
            );
            Server.getConsoleSender().sendMessage(Component.text("You should download ScreamingBedWars from official sources!", Color.RED));
        }

        Server.getConsoleSender().sendMessage(Component
                .text()
                .content("============")
                .color(Color.AQUA)
                .append(
                        Component.text("===", Color.RED),
                        Component.text("======  by " + pluginDescription.contributors().stream().map(org.screamingsandals.lib.plugin.Plugin.Contributor::name).collect(Collectors.joining(", ")), Color.WHITE)
                )
        );

        Server.getConsoleSender().sendMessage(Component
                .text()
                .content("+ Screaming ")
                .color(Color.AQUA)
                .append(
                        Component.text("Bed", Color.RED),
                        Component.text("Wars +  ", Color.WHITE),
                        Component.text("Version: " + VersionInfo.VERSION + " ", Color.GOLD)
                )
        );

        Server.getConsoleSender().sendMessage(Component
                .text()
                .content("============")
                .color(Color.AQUA)
                .append(
                        Component.text("===", Color.RED),
                        Component.text("======  ",Color.WHITE),
                        Component.text(snapshot ? "SNAPSHOT VERSION (" + VersionInfo.BUILD_NUMBER + ") - Use at your own risk" : "STABLE VERSION", snapshot ? Color.RED : Color.GREEN)
                )
        );

        Server.getConsoleSender().sendMessage(Component.text("Everything has finished loading! If you like our work, consider subscribing to our Patreon! <3", Color.WHITE));
        Server.getConsoleSender().sendMessage(Component.text("https://www.patreon.com/screamingsandals", Color.WHITE));

        HologramManager.setPreferDisplayEntities(MainConfig.getInstance().node("prefer-1-19-4-display-entities").getBoolean());
        if (!Server.isVersion(1, 9)) {
            // 1.8.8 boss bars
            var backend = MainConfig.getInstance().node("bossbar", "backend-entity").getString("dragon");
            Server.preferEnderDragonBossBar("dragon".equalsIgnoreCase(backend) || "ender_dragon".equalsIgnoreCase(backend));
            Server.enableViaHooksForBossBar(MainConfig.getInstance().node("bossbar", "allow-via-hooks").getBoolean());
        }
    }

    @OnDisable
    public void disable() {
        isDisabling = true;
    }

    @Override
    public GameManagerImpl getGameManager() {
        return GameManagerImpl.getInstance();
    }

    @Override
    public GroupManagerImpl getGroupManager() {
        return GroupManagerImpl.getInstance();
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
        return VersionInfo.VERSION;
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

    public void saveResource(@NotNull String resourcePath, boolean replace) {
        PluginUtils.saveResource(pluginDescription, logger, resourcePath, replace);
    }
}
