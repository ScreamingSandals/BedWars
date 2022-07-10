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

package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.VersionInfo;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.api.game.target.TargetBlock;
import org.screamingsandals.bedwars.api.game.target.TargetCountdown;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.inventories.ShopInventory;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.variants.VariantManagerImpl;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.configurate.SLibSerializers;
import org.screamingsandals.lib.plugin.PluginManager;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.event.ClickEvent;
import org.screamingsandals.lib.spectator.event.HoverEvent;
import org.screamingsandals.lib.utils.ConfigurateUtils;
import org.screamingsandals.lib.utils.annotations.Service;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DumpCommand extends BaseCommand {
    public DumpCommand() {
        super("dump", BedWarsPermission.ADMIN_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            var sender = commandContext.getSender();
                            new Thread(() -> {
                                try {
                                    var client = HttpClient.newHttpClient();

                                    var gsonBuilder = GsonConfigurationLoader.builder()
                                            .defaultOptions(configurationOptions ->
                                                    configurationOptions.serializers(SLibSerializers::appendSerializers)
                                            );
                                    var files = new ArrayList<>();
                                    files.add(Map.of(
                                            "name", "dump.json",
                                            "content", Map.of(
                                                    "format", "text",
                                                    "highlight_language", "json",
                                                    "value", gsonBuilder.buildAndSaveString(BasicConfigurationNode.root(gsonBuilder.defaultOptions()).set(Map.of(
                                                            "bedwars", Map.of(
                                                                    "version", VersionInfo.VERSION,
                                                                    "build", VersionInfo.BUILD_NUMBER,
                                                                    "edition", "free"
                                                            ),
                                                            "server", Map.of(
                                                                    "version", Server.getServerSoftwareVersion(),
                                                                    "javaVersion", System.getProperty("java.version"),
                                                                    "os", System.getProperty("os.name")
                                                            ),
                                                            "worlds", Server.getWorlds().stream().map(world -> Map.of(
                                                                    "name", world.getName(),
                                                                    "difficulty", world.getDifficulty(),
                                                                    "spawning", Map.of(
                                                                            "animals", world.isSpawningOfAnimalsAllowed(),
                                                                            "monsters", world.isSpawningOfMonstersAllowed()
                                                                    ),
                                                                    "maxHeight", world.getMaxY(),
                                                                    "minHeight", world.getMinY(),
                                                                    "keepSpawnInMemory", world.isSpawnKeptInMemory()
                                                            )).collect(Collectors.toList()),
                                                            "plugins", PluginManager.getAllPlugins().stream().map(plugin -> Map.of(
                                                                    "enabled", plugin.isEnabled(),
                                                                    "name", plugin.getName(),
                                                                    "version", plugin.getVersion(),
                                                                    "main", plugin.getInstance().map(Object::getClass).map(Class::getName).orElse("undefined"),
                                                                    "authors", plugin.getAuthors()
                                                            )).collect(Collectors.toList()),
                                                            "variants", VariantManagerImpl.getInstance().getVariants().stream().map(variant ->
                                                                    nullValuesAllowingMap(
                                                                            "name", variant.getName(),
                                                                            "configurationContainer", ConfigurateUtils.toMap(variant.getConfigurationContainer().getSaved()),
                                                                            "defaultItemSpawnerTypesIncluded", variant.isDefaultItemSpawnerTypesIncluded(),
                                                                            "customSpawners", variant.getCustomSpawnerTypes().stream().map(itemSpawnerType -> nullValuesAllowingMap(
                                                                                    "configKey", itemSpawnerType.getConfigKey(),
                                                                                    "name", itemSpawnerType.getName(),
                                                                                    "translatableKey", itemSpawnerType.getTranslatableKey(),
                                                                                    "spread", itemSpawnerType.getSpread(),
                                                                                    "itemType", itemSpawnerType.getItemType().platformName(),
                                                                                    "color", itemSpawnerType.getColor().toString(),
                                                                                    "interval", itemSpawnerType.getInterval()
                                                                            )).collect(Collectors.toList())
                                                                    )
                                                            ).collect(Collectors.toList()),
                                                            "games", GameManagerImpl.getInstance().getGames().stream().map(game ->
                                                                    nullValuesAllowingMap(
                                                                            "file", game.getFile(),
                                                                            "uuid", game.getUuid(),
                                                                            "name", game.getName(),
                                                                            "displayName", game.getDisplayName(),
                                                                            "minPlayers", game.getMinPlayers(),
                                                                            "maxPlayers", game.getMaxPlayers(),
                                                                            "lobby", nullValuesAllowingMap(
                                                                                    "spawn", game.getLobbySpawn(),
                                                                                    "countdown", game.getLobbyCountdown()
                                                                            ),
                                                                            "arena", nullValuesAllowingMap(
                                                                                    "spectator", game.getSpectatorSpawn(),
                                                                                    "countdown", game.getGameTime(),
                                                                                    "pos1", game.getPos1(),
                                                                                    "pos2", game.getPos2(),
                                                                                    "weather", game.getArenaWeather(),
                                                                                    "spawners", game.getSpawners().stream().map(itemSpawner -> nullValuesAllowingMap(
                                                                                            "type", itemSpawner.getItemSpawnerType().getConfigKey(),
                                                                                            "location", itemSpawner.getLocation(),
                                                                                            "maxSpawnedResources", itemSpawner.getMaxSpawnedResources(),
                                                                                            "startLevel", itemSpawner.getBaseAmountPerSpawn(),
                                                                                            "name", itemSpawner.getCustomName(),
                                                                                            "team", Optional.ofNullable(itemSpawner.getTeam()).map(Team::getName).orElse("no team"),
                                                                                            "hologramEnabled", itemSpawner.isHologramEnabled(),
                                                                                            "floatingEnabled", itemSpawner.isFloatingBlockEnabled(),
                                                                                            "rotationMode", itemSpawner.getRotationMode(),
                                                                                            "hologramType", itemSpawner.getHologramType()
                                                                                    )).collect(Collectors.toList()),
                                                                                    "teams", game.getTeams().stream().map(team -> nullValuesAllowingMap(
                                                                                            "name", team.getName(),
                                                                                            "color", team.getColor(),
                                                                                            "spawns", team.getTeamSpawns(),
                                                                                            "target", nullValuesAllowingMap(
                                                                                                    "type", team.getTarget() != null ? team.getTarget().getClass().getName() : null,
                                                                                                    "loc", team.getTarget() instanceof TargetBlock ? ((TargetBlock) team.getTarget()).getTargetBlock() : null,
                                                                                                    "countdown", team.getTarget() instanceof TargetCountdown ? ((TargetCountdown) team.getTarget()).getCountdown() : null
                                                                                            ),
                                                                                            "maxPlayers", team.getMaxPlayers()
                                                                                    )).collect(Collectors.toList()),
                                                                                    "stores", game.getGameStores().stream().map(gameStore -> nullValuesAllowingMap(
                                                                                            "entityType", gameStore.getEntityType(),
                                                                                            "location", gameStore.getStoreLocation(),
                                                                                            "shopFile", gameStore.getShopFile(),
                                                                                            "customName", gameStore.getShopCustomName(),
                                                                                            "baby", gameStore.isBaby(),
                                                                                            "skinName", gameStore.getSkinName()
                                                                                    )).collect(Collectors.toList()),
                                                                                    "configurationContainer", ConfigurateUtils.toMap(game.getConfigurationContainer().getSaved())
                                                                            )
                                                                    )
                                                            ).collect(Collectors.toList()))))
                                            )
                                    ));
                                    try {
                                        var loader = YamlConfigurationLoader.builder()
                                                .path(BedWarsPlugin.getInstance().getPluginDescription().getDataFolder().resolve("config.yml"))
                                                .build();

                                        var writer = new StringWriter();

                                        var configToString = YamlConfigurationLoader.builder()
                                                .sink(() -> new BufferedWriter(writer))
                                                .nodeStyle(NodeStyle.BLOCK)
                                                .build();

                                        var config = loader.load();

                                        config.node("database", "host").set("SECRET");
                                        config.node("database", "port").set(3306);
                                        config.node("database", "db").set("SECRET");
                                        config.node("database", "user").set("SECRET");
                                        config.node("database", "password").set("SECRET");
                                        config.node("database", "table-prefix").set("bw_");
                                        config.node("database", "useSSL").set(false);

                                        configToString.save(config);

                                        files.add(Map.of(
                                                "name", "config.yml",
                                                "content", Map.of(
                                                        "format", "text",
                                                        "highlight_language", "yaml",
                                                        "value", writer.toString()
                                                )
                                        ));
                                    } catch (ConfigurateException e) {
                                        e.printStackTrace();
                                    }
                                    var mainShop = Map.of(
                                            "name", MainConfig.getInstance().node("turnOnExperimentalGroovyShop").getBoolean() ? "shop.groovy" : "shop.yml",
                                            "content", Map.of(
                                                    "format", "text",
                                                    "highlight_language", MainConfig.getInstance().node("turnOnExperimentalGroovyShop").getBoolean() ? "groovy" : "yaml",
                                                    "value", String.join("\n", Files.readAllLines(BedWarsPlugin.getInstance().getPluginDescription().getDataFolder().resolve(MainConfig.getInstance().node("turnOnExperimentalGroovyShop").getBoolean() ? "shop/shop.groovy" : "shop/shop.yml"), StandardCharsets.UTF_8))
                                            )
                                    );
                                    files.add(mainShop);
                                    GameManagerImpl.getInstance()
                                            .getGames()
                                            .stream()
                                            .map(GameImpl::getGameStores)
                                            .flatMap(Collection::stream)
                                            .map(GameStore::getShopFile)
                                            .filter(Objects::nonNull)
                                            .distinct()
                                            .filter(s -> !mainShop.get("name").equals(s))
                                            .forEach(s -> {
                                                try {
                                                    final var file = ShopInventory.getInstance().normalizeShopFile(s);
                                                    if (!file.exists()) {
                                                        return;
                                                    }

                                                    files.add(Map.of(
                                                            "name", file.getName(),
                                                            "content", Map.of(
                                                                    "format", "text",
                                                                    "highlight_language", file.getName().endsWith(".groovy") ? "groovy" : "yaml",
                                                                    "value", String.join("\n", Files.readAllLines(file.toPath(), StandardCharsets.UTF_8))
                                                            )
                                                    ));
                                                } catch (IOException e) {
                                                    Debug.warn("Cannot add shop file to dump, it probably does not exists..", true);
                                                    e.printStackTrace();
                                                }
                                            });

                                    client.sendAsync(HttpRequest.newBuilder()
                                                    .uri(URI.create("https://api.paste.gg/v1/pastes"))
                                                    .header("Content-Type", "application/json")
                                                    .POST(HttpRequest.BodyPublishers.ofString(gsonBuilder.buildAndSaveString(BasicConfigurationNode.root(gsonBuilder.defaultOptions()).set(Map.of(
                                                            "name", "Bedwars dump",
                                                            "description", "Dump generated by ScreamingBedwars plugin",
                                                            "visibility", "unlisted",
                                                            "expires", LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss'Z'")),
                                                            "files", files
                                                    ))))).build(), HttpResponse.BodyHandlers.ofString())
                                            .thenAccept(stringHttpResponse -> {
                                                if (stringHttpResponse.statusCode() >= 200 && stringHttpResponse.statusCode() <= 299) {
                                                    try {
                                                        var message = gsonBuilder.buildAndLoadString(stringHttpResponse.body());
                                                        org.screamingsandals.lib.lang.Message.of(LangKeys.DUMP_SUCCESS)
                                                                .defaultPrefix()
                                                                .placeholder("dump", Component
                                                                        .text()
                                                                        .content("https://paste.gg/" + message.node("result", "id").getString(""))
                                                                        .color(Color.GRAY)
                                                                        .clickEvent(ClickEvent.openUrl("https://paste.gg/" + message.node("result", "id").getString("")))
                                                                        .hoverEvent(HoverEvent.showText(Component.text("Open this link"))))
                                                                .send(sender);
                                                    } catch (ConfigurateException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                } else {
                                                    org.screamingsandals.lib.lang.Message.of(LangKeys.DUMP_FAILED).defaultPrefix().send(sender);
                                                }
                                            });
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    org.screamingsandals.lib.lang.Message.of(LangKeys.DUMP_FAILED).defaultPrefix().send(sender);
                                }
                            }).start();
                        })
        );
    }

    public static Map<?, ?> nullValuesAllowingMap(Object... objects) {
        var map = new HashMap<>();
        Object key = null;
        for (var object : objects) {
            if (key == null) {
                key = Objects.requireNonNull(object);
            } else {
                map.put(key, object);
                key = null;
            }
        }
        return map;
    }
}
