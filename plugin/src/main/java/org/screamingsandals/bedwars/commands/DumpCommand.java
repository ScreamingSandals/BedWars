package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.VersionInfo;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.bedwars.inventories.ShopInventory;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.premium.PremiumBedwars;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.ConfigurateUtils;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.BufferedWriter;
import java.io.File;
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

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class DumpCommand extends BaseCommand {
    public DumpCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "dump", BedWarsPermission.ADMIN_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            var sender = commandContext.getSender();
                            new Thread(() -> {
                                try {
                                    var client = HttpClient.newHttpClient();

                                    var gson = new GsonBuilder()
                                            .registerTypeAdapter(Location.class, (JsonSerializer<Location>) (location, type, context) ->
                                                    context.serialize(Map.of(
                                                            "x", location.getX(),
                                                            "y", location.getY(),
                                                            "z", location.getZ(),
                                                            "world", location.getWorld().getName(),
                                                            "pitch", location.getPitch(),
                                                            "yaw", location.getYaw()
                                                    )))
                                            .registerTypeAdapter(World.class, (JsonSerializer<World>) (world, type, context) -> context.serialize(world.getName()))
                                            .registerTypeAdapter(File.class, (JsonSerializer<File>) (file, type, context) -> context.serialize(file.getAbsolutePath()))
                                            .setPrettyPrinting().create();
                                    var files = new ArrayList<>();
                                    files.add(Map.of(
                                            "name", "dump.json",
                                            "content", Map.of(
                                                    "format", "text",
                                                    "highlight_language", "json",
                                                    "value", gson.toJson(Map.of(
                                                            "bedwars", Map.of(
                                                                    "version", VersionInfo.VERSION,
                                                                    "build", VersionInfo.BUILD_NUMBER,
                                                                    "edition", PremiumBedwars.isPremium() ? "premium" : "free"
                                                            ),
                                                            "server", Map.of(
                                                                    "version", Bukkit.getVersion(),
                                                                    "javaVersion", System.getProperty("java.version"),
                                                                    "os", System.getProperty("os.name")
                                                            ),
                                                            "worlds", Bukkit.getWorlds().stream().map(world -> Map.of(
                                                                    "name", world.getName(),
                                                                    "difficulty", world.getDifficulty(),
                                                                    "spawning", Map.of(
                                                                            "animals", world.getAllowAnimals(),
                                                                            "monsters", world.getAllowMonsters()
                                                                    ),
                                                                    "maxHeight", world.getMaxHeight(),
                                                                    "keepSpawnInMemory", world.getKeepSpawnInMemory()
                                                            )).collect(Collectors.toList()),
                                                            "plugins", Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(plugin -> Map.of(
                                                                    "enabled", plugin.isEnabled(),
                                                                    "name", plugin.getName(),
                                                                    "version", plugin.getDescription().getVersion(),
                                                                    "main", plugin.getDescription().getMain(),
                                                                    "authors", plugin.getDescription().getAuthors()
                                                            )).collect(Collectors.toList()),
                                                            "games", GameManager.getInstance().getGames().stream().map(game ->
                                                                    nullValuesAllowingMap(
                                                                            "file", game.getFile(),
                                                                            "name", game.getName(),
                                                                            "minPlayers", game.getMinPlayers(),
                                                                            "maxPlayers", game.getMaxPlayers(),
                                                                            "lobby", nullValuesAllowingMap(
                                                                                    "spawn", game.getLobbySpawn(),
                                                                                    "countdown", game.getLobbyCountdown(),
                                                                                    "bossbar", game.getLobbyBossBarColor()
                                                                            ),
                                                                            "arena", nullValuesAllowingMap(
                                                                                    "spectator", game.getSpectatorSpawn(),
                                                                                    "countdown", game.getGameTime(),
                                                                                    "pos1", game.getPos1(),
                                                                                    "pos2", game.getPos2(),
                                                                                    "bossbar", game.getGameBossBarColor(),
                                                                                    "arenaTime", game.getArenaTime(),
                                                                                    "weather", game.getArenaWeather(),
                                                                                    "customPrefix", game.getCustomPrefix(),
                                                                                    "spawners", game.getSpawners().stream().map(itemSpawner -> nullValuesAllowingMap(
                                                                                            "type", itemSpawner.getItemSpawnerType().getConfigKey(),
                                                                                            "location", itemSpawner.getLocation(),
                                                                                            "maxSpawnedResources", itemSpawner.getMaxSpawnedResources(),
                                                                                            "startLevel", itemSpawner.getStartLevel(),
                                                                                            "name", itemSpawner.getCustomName(),
                                                                                            "team", itemSpawner.getTeam().map(Team::getName).orElse("no team"),
                                                                                            "hologramEnabled", itemSpawner.getHologramEnabled(),
                                                                                            "floatingEnabled", itemSpawner.getFloatingEnabled()
                                                                                    )).collect(Collectors.toList()),
                                                                                    "teams", game.getTeams().stream().map(team -> nullValuesAllowingMap(
                                                                                            "name", team.getName(),
                                                                                            "color", team.getColor(),
                                                                                            "spawn", team.getTeamSpawn(),
                                                                                            "targetBlock", team.getTargetBlock(),
                                                                                            "maxPlayers", team.getMaxPlayers()
                                                                                    )).collect(Collectors.toList()),
                                                                                    "stores", game.getGameStores().stream().map(gameStore -> nullValuesAllowingMap(
                                                                                            "entityType", gameStore.getEntityType(),
                                                                                            "location", gameStore.getStoreLocation(),
                                                                                            "shopFile", gameStore.getShopFile(),
                                                                                            "customName", gameStore.getShopCustomName(),
                                                                                            "useParent", gameStore.getUseParent(),
                                                                                            "baby", gameStore.isBaby(),
                                                                                            "skinName", gameStore.getSkinName()
                                                                                    )).collect(Collectors.toList()),
                                                                                    "configurationContainer", ConfigurateUtils.toMap(game.getConfigurationContainer().getSaved())
                                                                            )
                                                                    )
                                                            ).collect(Collectors.toList())))
                                            )
                                    ));
                                    try {
                                        var loader = YamlConfigurationLoader.builder()
                                                .path(Main.getInstance().getPluginDescription().getDataFolder().resolve("config.yml"))
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
                                                    "value", String.join("\n", Files.readAllLines(Main.getInstance().getPluginDescription().getDataFolder().resolve(MainConfig.getInstance().node("turnOnExperimentalGroovyShop").getBoolean() ? "shop.groovy" : "shop.yml"), StandardCharsets.UTF_8))
                                            )
                                    );
                                    files.add(mainShop);
                                    GameManager.getInstance()
                                            .getGames()
                                            .stream()
                                            .map(Game::getGameStores)
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
                                            .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(Map.of(
                                                    "name", "Bedwars dump",
                                                    "description", "Dump generated by ScreamingBedwars plugin",
                                                    "visibility", "unlisted",
                                                    "expires", LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss'Z'")),
                                                    "files", files
                                            )))).build(), HttpResponse.BodyHandlers.ofString())
                                            .thenAccept(stringHttpResponse -> {
                                                if (stringHttpResponse.statusCode() >= 200 && stringHttpResponse.statusCode() <= 299) {
                                                    var message = gson.fromJson(stringHttpResponse.body(), Message.class);
                                                    sender.sendMessage(AdventureHelper
                                                            .toComponent(i18n("dump_success"))
                                                            .append(Component
                                                                        .text("https://paste.gg/" + message.getResult().getId())
                                                                        .color(NamedTextColor.GRAY)
                                                                        .clickEvent(ClickEvent.openUrl("https://paste.gg/" + message.getResult().getId()))
                                                                        .hoverEvent(HoverEvent.showText(Component.text("Open this link")))
                                                            )
                                                    );
                                                } else {
                                                    sender.sendMessage(i18n("dump_failed"));
                                                }
                                            });
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    sender.sendMessage(i18n("dump_failed"));
                                }
                            }).start();
                        })
        );
    }


    @Data
    public static class Message {
        private Result result;
    }

    @Data
    public static class Result {
        private String id;
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
