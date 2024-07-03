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

package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.VersionInfo;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.api.game.target.TargetBlock;
import org.screamingsandals.bedwars.api.game.target.ExpirableTarget;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.target.TargetBlockImpl;
import org.screamingsandals.bedwars.inventories.ShopInventory;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.variants.VariantManagerImpl;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.plugin.Plugin;
import org.screamingsandals.lib.plugin.Plugins;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.event.ClickEvent;
import org.screamingsandals.lib.spectator.event.HoverEvent;
import org.screamingsandals.lib.utils.ConfigurateUtils;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.world.Worlds;
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
    private static final @NotNull List<@NotNull String> SERVICES = List.of("paste.gg", "pastes.dev");

    public DumpCommand() {
        super("dump", BedWarsPermission.ADMIN_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        // TODO: rework this so we use configurate properly and serializers actually work (it even refuses to serialize enums, like wtf)
        manager.command(
                commandSenderWrapperBuilder
                        .argument(
                                StringArgument.<CommandSender>newBuilder("service")
                                        .withSuggestionsProvider((objectCommandContext, s) -> SERVICES)
                                        .asOptionalWithDefault(SERVICES.get(0))
                                        .build()
                        )
                        .handler(commandContext -> {
                            var sender = commandContext.getSender();

                            String service = commandContext.get("service");
                            if (!SERVICES.contains(service.toLowerCase(Locale.ROOT))) {
                                org.screamingsandals.lib.lang.Message.of(LangKeys.DUMP_SUCCESS)
                                        .defaultPrefix()
                                        .placeholderRaw("unknown_service", service)
                                        .placeholderRaw("allowed_values", String.join(", ", SERVICES))
                                        .send(sender);
                                return;
                            }
                            new Thread(() -> {
                                try {
                                    var client = HttpClient.newHttpClient();

                                    var gsonBuilder = GsonConfigurationLoader.builder();
                                    var files = new ArrayList<AFile>();
                                    files.add(new AFile("dump.json", "json", gsonBuilder.buildAndSaveString(gsonBuilder.build().createNode().set(Map.of(
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
                                                            "worlds", Worlds.getWorlds().stream().map(world -> Map.of(
                                                                    "name", world.getName(),
                                                                    "difficulty", world.getDifficulty().location().asString(),
                                                                    "spawning", Map.of(
                                                                            "animals", world.isSpawningOfAnimalsAllowed(),
                                                                            "monsters", world.isSpawningOfMonstersAllowed()
                                                                    ),
                                                                    "maxHeight", world.getMaxY(),
                                                                    "minHeight", world.getMinY(),
                                                                    "keepSpawnInMemory", world.isSpawnKeptInMemory()
                                                            )).collect(Collectors.toList()),
                                                            "plugins", Plugins.getAllPlugins().stream().map(plugin -> Map.of(
                                                                    "enabled", plugin.isEnabled(),
                                                                    "name", plugin.name(),
                                                                    "version", plugin.version(),
                                                                    "main", Optional.ofNullable(plugin.getInstance()).map(Object::getClass).map(Class::getName).orElse("undefined"),
                                                                    "authors", plugin.contributors().stream().map(Plugin.Contributor::name).collect(Collectors.joining(", "))
                                                            )).collect(Collectors.toList()),
                                                            "variantsInMemory", VariantManagerImpl.getInstance().getVariants().stream().map(variant ->
                                                                    nullValuesAllowingMap(
                                                                            "name", variant.getName(),
                                                                            "configurationContainer", ConfigurateUtils.toMap(variant.getConfigurationContainer().getSaved()),
                                                                            "defaultItemSpawnerTypesIncluded", variant.isDefaultItemSpawnerTypesIncluded(),
                                                                            "customSpawners", variant.getCustomSpawnerTypes().stream().map(itemSpawnerType -> nullValuesAllowingMap(
                                                                                    "configKey", itemSpawnerType.getConfigKey(),
                                                                                    "name", itemSpawnerType.getName(),
                                                                                    "translatableKey", itemSpawnerType.getTranslatableKey().toJavaJson(),
                                                                                    "spread", itemSpawnerType.getSpread(),
                                                                                    "itemType", itemSpawnerType.getItemType().location().asString(),
                                                                                    "color", itemSpawnerType.getColor().toString(),
                                                                                    "interval", itemSpawnerType.getInterval().first() + " " + itemSpawnerType.getInterval().second()
                                                                            )).collect(Collectors.toList())
                                                                    )
                                                            ).collect(Collectors.toList()),
                                                            "gamesInMemory", GameManagerImpl.getInstance().getLocalGames().stream().map(game ->
                                                                    nullValuesAllowingMap(
                                                                            "file", game.getFile().getAbsolutePath(),
                                                                            "uuid", game.getUuid().toString(),
                                                                            "name", game.getName(),
                                                                            "displayName", game.getDisplayName(),
                                                                            "minPlayers", game.getMinPlayers(),
                                                                            "maxPlayers", game.getMaxPlayers(),
                                                                            "lobby", nullValuesAllowingMap(
                                                                                    "spawn", locationToMap(game.getLobbySpawn()),
                                                                                    "countdown", game.getLobbyCountdown()
                                                                            ),
                                                                            "arena", nullValuesAllowingMap(
                                                                                    "spectator", locationToMap(game.getSpectatorSpawn()),
                                                                                    "countdown", game.getGameTime(),
                                                                                    "pos1", locationToMap(game.getPos1()),
                                                                                    "pos2", locationToMap(game.getPos2()),
                                                                                    "weather", game.getArenaWeather() != null ? game.getArenaWeather().location().asString() : null,
                                                                                    "spawners", game.getSpawners().stream().map(itemSpawner -> nullValuesAllowingMap(
                                                                                            "type", itemSpawner.getItemSpawnerType().getConfigKey(),
                                                                                            "location", locationToMap(itemSpawner.getLocation()),
                                                                                            "maxSpawnedResources", itemSpawner.getMaxSpawnedResources(),
                                                                                            "startLevel", itemSpawner.getBaseAmountPerSpawn(),
                                                                                            "name", itemSpawner.getCustomName(),
                                                                                            "team", Optional.ofNullable(itemSpawner.getTeam()).map(Team::getName).orElse("no team"),
                                                                                            "hologramEnabled", itemSpawner.isHologramEnabled(),
                                                                                            "floatingEnabled", itemSpawner.isFloatingBlockEnabled(),
                                                                                            "rotationMode", itemSpawner.getRotationMode().name(),
                                                                                            "hologramType", itemSpawner.getHologramType().name(),
                                                                                            "customSpread", itemSpawner.getCustomSpread(),
                                                                                            "customInitialInterval", itemSpawner.getInitialInterval() != null ? (itemSpawner.getInitialInterval().first() + " " + itemSpawner.getInitialInterval().second()) : null
                                                                                    )).collect(Collectors.toList()),
                                                                                    "teams", game.getTeams().stream().map(team -> nullValuesAllowingMap(
                                                                                            "name", team.getName(),
                                                                                            "color", team.getColor().name(),
                                                                                            "spawns", team.getTeamSpawns().stream().map(DumpCommand::locationToMap).collect(Collectors.toList()),
                                                                                            "target", nullValuesAllowingMap(
                                                                                                    "type", team.getTarget() != null ? team.getTarget().getClass().getName() : null,
                                                                                                    "loc", team.getTarget() instanceof TargetBlock ? locationToMap(((TargetBlockImpl) team.getTarget()).getTargetBlock()) : null,
                                                                                                    "countdown", team.getTarget() instanceof ExpirableTarget ? ((ExpirableTarget) team.getTarget()).getCountdown() : null
                                                                                            ),
                                                                                            "maxPlayers", team.getMaxPlayers()
                                                                                    )).collect(Collectors.toList()),
                                                                                    "stores", game.getGameStores().stream().map(gameStore -> nullValuesAllowingMap(
                                                                                            "entityType", gameStore.getEntityType().location().asString(),
                                                                                            "location", locationToMap(gameStore.getStoreLocation()),
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
                                    );
                                    try {
                                        var loader = YamlConfigurationLoader.builder()
                                                .path(BedWarsPlugin.getInstance().getPluginDescription().dataFolder().resolve("config.yml"))
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

                                        files.add(new AFile("config.yml", "yaml", writer.toString()));
                                    } catch (ConfigurateException e) {
                                        e.printStackTrace();
                                    }
                                    for (var game : GameManagerImpl.getInstance().getLocalGames()) {
                                        if (game.getFile() != null && game.getFile().exists()) {
                                            files.add(new AFile(
                                                    game.getFile().getParentFile().getName() + "/" + game.getFile().getName(),
                                                    game.getFile().getName().endsWith(".json") ? "json" : "yaml",
                                                    String.join("\n", Files.readAllLines(game.getFile().toPath(), StandardCharsets.UTF_8))
                                            ));
                                        }
                                    }
                                    for (var variant : VariantManagerImpl.getInstance().getVariants()) {
                                        if (variant.getFile() != null && variant.getFile().exists()) {
                                            files.add(new AFile(
                                                    variant.getFile().getParentFile().getName() + "/" + variant.getFile().getName(),
                                                    variant.getFile().getName().endsWith(".json") ? "json" : "yaml",
                                                    String.join("\n", Files.readAllLines(variant.getFile().toPath(), StandardCharsets.UTF_8))
                                            ));
                                        }
                                    }
                                    String mainShopName = MainConfig.getInstance().node("turnOnExperimentalGroovyShop").getBoolean() ? "shop/shop.groovy" : "shop/shop.yml";
                                    files.add(new AFile(
                                            mainShopName,
                                            mainShopName.endsWith(".groovy") ? "groovy" : "yaml",
                                            String.join("\n", Files.readAllLines(BedWarsPlugin.getInstance().getPluginDescription().dataFolder().resolve(mainShopName), StandardCharsets.UTF_8))
                                    ));
                                    GameManagerImpl.getInstance()
                                            .getLocalGames()
                                            .stream()
                                            .map(GameImpl::getGameStores)
                                            .flatMap(Collection::stream)
                                            .map(GameStore::getShopFile)
                                            .filter(Objects::nonNull)
                                            .distinct()
                                            .filter(s -> !mainShopName.equals(s))
                                            .forEach(s -> {
                                                try {
                                                    final var file = ShopInventory.getInstance().normalizeShopFile(s);
                                                    if (!file.exists()) {
                                                        return;
                                                    }

                                                    files.add(new AFile(
                                                            file.getName(),
                                                            file.getName().endsWith(".groovy") ? "groovy" : "yaml",
                                                            String.join("\n", Files.readAllLines(file.toPath(), StandardCharsets.UTF_8))
                                                    ));
                                                } catch (IOException e) {
                                                    Debug.warn("Cannot add shop file to dump, it probably does not exists..", true);
                                                    e.printStackTrace();
                                                }
                                            });

                                    if ("pastes.dev".equals(service)) {
                                        var textBuilder = new StringBuilder("BedWars dump\n\nThis dump consists of multiple text files. All these files have names and have a special delimiter which marks the start and the end of the individual files (these are not really interesting for human beings).\n\nList of uploaded text files:\n");
                                        for (var file : files) {
                                            textBuilder.append("- ").append(file.getName()).append("\n");
                                        }

                                        var random = new Random();
                                        for (var file : files) {
                                            String generatedString;
                                            do {
                                                generatedString = random.ints(97, 123)
                                                        .limit(9)
                                                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                                        .toString();
                                            } while (file.getContent().contains("-----" + generatedString)); // just in case

                                            var delimiter = "-----" + generatedString;
                                            textBuilder.append("\n\nFile: ")
                                                    .append(file.getName())
                                                    .append("\n")
                                                    .append(delimiter)
                                                    .append("\n\n")
                                                    .append(file.getContent())
                                                    .append("\n\n")
                                                    .append(delimiter);
                                        }

                                        client.sendAsync(HttpRequest.newBuilder()
                                                        .uri(URI.create("https://api.pastes.dev/post"))
                                                        .header("Content-Type", "text/plain")
                                                        .header("User-Agent", "ScreamingBedWars")
                                                        .header("Allow-Modification", "false")
                                                        .POST(HttpRequest.BodyPublishers.ofString(textBuilder.toString())).build(), HttpResponse.BodyHandlers.ofString())
                                                .thenAccept(stringHttpResponse -> {
                                                    if (stringHttpResponse.statusCode() >= 200 && stringHttpResponse.statusCode() <= 299) {
                                                        var location = stringHttpResponse.headers().firstValue("Location");
                                                        org.screamingsandals.lib.lang.Message.of(LangKeys.DUMP_SUCCESS)
                                                                .defaultPrefix()
                                                                .placeholder("dump", Component
                                                                        .text()
                                                                        .content("https://pastes.dev/" + location)
                                                                        .color(Color.GRAY)
                                                                        .clickEvent(ClickEvent.openUrl("https://pastes.dev/" + location))
                                                                        .hoverEvent(HoverEvent.showText(Component.text("Open this link"))))
                                                                .send(sender);
                                                    } else {
                                                        org.screamingsandals.lib.lang.Message.of(LangKeys.DUMP_FAILED).defaultPrefix().send(sender);
                                                    }
                                                });
                                    } else {
                                        client.sendAsync(HttpRequest.newBuilder()
                                                    .uri(URI.create("https://api.paste.gg/v1/pastes"))
                                                    .header("Content-Type", "application/json")
                                                    .POST(HttpRequest.BodyPublishers.ofString(gsonBuilder.buildAndSaveString(gsonBuilder.build().createNode().set(Map.of(
                                                            "name", "Bedwars dump",
                                                            "description", "Dump generated by ScreamingBedwars plugin",
                                                            "visibility", "unlisted",
                                                            "expires", LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss'Z'")),
                                                            "files", files.stream()
                                                                    .map(aFile -> nullValuesAllowingMap(
                                                                            "name", aFile.getName(),
                                                                            "content", nullValuesAllowingMap(
                                                                                    "format", "text",
                                                                                    "highlight_language", aFile.getLanguage(),
                                                                                    "value", aFile.getContent()
                                                                            )
                                                                    ))
                                                                    .collect(Collectors.toList())
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
                                                    org.screamingsandals.lib.lang.Message.of(LangKeys.DUMP_FAILED).defaultPrefix()
                                                            .joinPlainText(" " + stringHttpResponse.statusCode() + " - " + stringHttpResponse.body())
                                                            .send(sender);
                                                }
                                            });
                                    }
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

    public static Map<?, ?> locationToMap(Location location) {
        return nullValuesAllowingMap(
          "world", location.getWorld().getName(),
          "x", location.getX(),
          "y", location.getY(),
          "z", location.getZ(),
          "yaw", location.getYaw(),
          "pitch", location.getPitch()
        );
    }

    @Data
    public static class AFile {
        private final @NotNull String name;
        private final @NotNull String language;
        private final @NotNull String content;
    }
}
