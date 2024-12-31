/*
 * Copyright (C) 2023 ScreamingSandals
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

import com.google.gson.*;
import lombok.Data;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.VersionInfo;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.inventories.ShopInventory;
import org.screamingsandals.bedwars.lib.debug.Debug;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class DumpCommand extends BaseCommand {

    public DumpCommand() {
        super("dump", ADMIN_PERMISSION, true, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        String service = args.size() > 0 ? args.get(0).toLowerCase(Locale.ROOT) : "pastes.dev";
        new Thread(() -> {
            try {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Location.class, (JsonSerializer<Location>) (location, type, context) ->
                                context.serialize(nullValuesAllowingMap(
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
                List<AFile> files = new ArrayList<>();
                files.add(new AFile("dump.json", "json", gson.toJson(nullValuesAllowingMap(
                        "bedwars", nullValuesAllowingMap(
                                "version", Main.getVersion(),
                                "build", VersionInfo.BUILD_NUMBER,
                                "edition", "free"
                        ),
                        "server", nullValuesAllowingMap(
                                "version", Bukkit.getVersion(),
                                "javaVersion", System.getProperty("java.version"),
                                "os", System.getProperty("os.name")
                        ),
                        "worlds", Bukkit.getWorlds().stream().map(world -> nullValuesAllowingMap(
                                "name", world.getName(),
                                "difficulty", world.getDifficulty(),
                                "spawning", nullValuesAllowingMap(
                                        "animals", world.getAllowAnimals(),
                                        "monsters", world.getAllowMonsters()
                                ),
                                "maxHeight", world.getMaxHeight(),
                                "keepSpawnInMemory", world.getKeepSpawnInMemory()
                        )).collect(Collectors.toList()),
                        "plugins", Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(plugin -> nullValuesAllowingMap(
                                "enabled", plugin.isEnabled(),
                                "name", plugin.getName(),
                                "version", plugin.getDescription().getVersion(),
                                "main", plugin.getDescription().getMain(),
                                "authors", plugin.getDescription().getAuthors()
                        )).collect(Collectors.toList()),
                        "gamesInMemory", Main.getGameNames().stream().map(Main::getGame).map(game ->
                                nullValuesAllowingMap(
                                        "file", game.getFile().getParentFile().getName() + "/" + game.getFile().getName(),
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
                                                        "team", itemSpawner.getTeam() != null ? itemSpawner.getTeam().getName() : "no team",
                                                        "hologramEnabled", itemSpawner.getHologramEnabled()
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
                                                "configurationContainer", nullValuesAllowingMap(
                                                        "compass-enabled", game.getCompassEnabled(),
                                                        "join-randomly-after-lobby-timeout", game.getJoinRandomTeamAfterLobby(),
                                                        "join-randomly-on-lobby-join", game.getJoinRandomTeamOnJoin(),
                                                        "add-wool-to-inventory-on-join", game.getAddWoolToInventoryOnJoin(),
                                                        "prevent-killing-villagers", game.getPreventKillingVillagers(),
                                                        "player-drops", game.getPlayerDrops(),
                                                        "friendlyfire", game.getFriendlyfire(),
                                                        "in-lobby-colored-leather-by-team", game.getColoredLeatherByTeamInLobby(),
                                                        "keep-inventory-on-death", game.getKeepInventory(),
                                                        "allow-crafting", game.getCrafting(),
                                                        "lobbybossbar", game.getLobbyBossbar(),
                                                        "bossbar", game.getGameBossbar(),
                                                        "scoreboard", game.getScoreboard(),
                                                        "lobbyscoreboard", game.getLobbyScoreboard(),
                                                        "prevent-spawning-mobs", game.getPreventSpawningMobs(),
                                                        "spawner-holograms", game.getSpawnerHolograms(),
                                                        "spawner-disable-merge", game.getSpawnerDisableMerge(),
                                                        "game-start-items", game.getGameStartItems(),
                                                        "player-respawn-items", game.getPlayerRespawnItems(),
                                                        "spawner-holograms-countdown", game.getSpawnerHologramsCountdown(),
                                                        "damage-when-player-is-not-in-arena", game.getDamageWhenPlayerIsNotInArena(),
                                                        "remove-unused-target-blocks", game.getRemoveUnusedTargetBlocks(),
                                                        "allow-block-falling", game.getAllowBlockFalling(),
                                                        "holo-above-bed", game.getHoloAboveBed(),
                                                        "allow-spectator-join", game.getSpectatorJoin(),
                                                        "stop-team-spawners-on-die", game.getStopTeamSpawnersOnDie(),
                                                        "anchor-auto-fill", game.getAnchorAutoFill(),
                                                        "anchor-decreasing", game.getAnchorDecreasing(),
                                                        "cake-target-block-eating", game.getCakeTargetBlockEating(),
                                                        "target-block-explosions", game.getTargetBlockExplosions()

                                                )
                                        )
                                )
                        ).collect(Collectors.toList())))
                ));
                for (String gameN : Main.getGameNames()) {
                    Game game = Main.getGame(gameN);
                    files.add(new AFile(
                            game.getFile().getParentFile().getName() + "/" + game.getFile().getName(),
                            "yaml",
                            String.join("\n", Files.readAllLines(game.getFile().toPath(), StandardCharsets.UTF_8))
                    ));
                }
                FileConfiguration config = new YamlConfiguration();
                try {
                    config.load(new File(Main.getInstance().getDataFolder(), "config.yml"));

                    config.set("database.host", "SECRET");
                    config.set("database.port", 3306);
                    config.set("database.db", "SECRET");
                    config.set("database.user", "SECRET");
                    config.set("database.password", "SECRET");
                    config.set("database.table-prefix", "bw_");
                    config.set("database.useSSL", false);

                    files.add(new AFile("config.yml", "yaml", config.saveToString()));
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                String mainShopName = Main.getConfigurator().config.getBoolean("turnOnExperimentalGroovyShop", false) ? "shop.groovy" : "shop.yml";
                files.add(new AFile(
                        mainShopName,
                        mainShopName.endsWith(".groovy") ? "groovy" : "yaml",
                        String.join("\n", Files.readAllLines(new File(Main.getInstance().getDataFolder(), mainShopName).toPath(), StandardCharsets.UTF_8))
                ));
                Main.getGameNames().stream()
                        .map(Main::getGame)
                        .map(Game::getGameStores)
                        .flatMap(Collection::stream)
                        .map(GameStore::getShopFile)
                        .filter(Objects::nonNull)
                        .distinct()
                        .filter(s -> !mainShopName.equals(s))
                        .forEach(s -> {
                            try {
                                final File file = ShopInventory.normalizeShopFile(s);
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
                    URL url = new URL("https://api.pastes.dev/post");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "text/plain");
                    connection.setRequestProperty("User-Agent", "ScreamingBedWars");
                    connection.setRequestProperty("Allow-Modification", "false");
                    connection.setDoOutput(true);
                    StringBuilder result = new StringBuilder("BedWars dump\n\nThis dump consists of multiple text files. All these files have names and have a special delimiter which marks the start and the end of the individual files (these are not really interesting for human beings).\n\nList of uploaded text files:\n");
                    for (AFile file : files) {
                        result.append("- ").append(file.getName()).append("\n");
                    }

                    for (AFile file : files) {
                        Random random = new Random();
                        String generatedString;
                        do {
                            generatedString = random.ints(97, 123)
                                    .limit(9)
                                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                    .toString();
                        } while (file.getContent().contains("-----" + generatedString)); // just in case

                        String delimiter = "-----" + generatedString;
                        result.append("\n\nFile: ")
                                .append(file.getName())
                                .append("\n")
                                .append(delimiter)
                                .append("\n\n")
                                .append(file.getContent())
                                .append("\n\n")
                                .append(delimiter);
                    }
                    connection.getOutputStream().write(result.toString().getBytes(StandardCharsets.UTF_8));
                    connection.connect();

                    int code = connection.getResponseCode();

                    if (code >= 200 && code <= 299) {
                        String location = "https://pastes.dev/" + connection.getHeaderField("Location");
                        if (Main.isSpigot() && sender instanceof Player) {
                            try {
                                TextComponent msg1 = new TextComponent(location);
                                msg1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, location));
                                msg1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").append("Click to open this link").create()));

                                ((Player) sender).spigot().sendMessage(new ComponentBuilder("")
                                        .append(TextComponent.fromLegacyText(i18n("dump_success") + ChatColor.GRAY))
                                        .append(msg1)
                                        .create());
                            } catch (Throwable ignored) {
                                sender.sendMessage(i18n("dump_success") + ChatColor.GRAY + location);
                            }
                        } else {
                            sender.sendMessage(i18n("dump_success") + ChatColor.GRAY + location);
                        }
                    } else {
                        sender.sendMessage(i18n("dump_failed"));
                        Bukkit.getLogger().severe(code + " - " + gson.fromJson(new InputStreamReader(connection.getErrorStream()), Map.class));
                    }
                } else {
                    URL url = new URL("https://api.paste.gg/v1/pastes");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);
                    String json = gson.toJson(nullValuesAllowingMap(
                            "name", "Bedwars dump",
                            "description", "Dump generated by ScreamingBedwars plugin",
                            "visibility", "unlisted",
                            "expires", LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss'Z'")),
                            "files", files
                                    .stream()
                                    .map(aFile -> nullValuesAllowingMap(
                                            "name", aFile.getName(),
                                            "content", nullValuesAllowingMap(
                                                    "format", "text",
                                                    "highlight_language", aFile.getLanguage(),
                                                    "value", aFile.getContent()
                                            )
                                    ))
                                    .collect(Collectors.toList())
                    ));
                    connection.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
                    connection.connect();

                    int code = connection.getResponseCode();

                    if (code >= 200 && code <= 299) {
                        Message message = gson.fromJson(new InputStreamReader(connection.getInputStream()), Message.class);
                        if (Main.isSpigot() && sender instanceof Player) {
                            try {
                                TextComponent msg1 = new TextComponent("https://paste.gg/" + message.getResult().getId());
                                msg1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://paste.gg/" + message.getResult().getId()));
                                msg1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").append("Open this link").create()));

                                ((Player) sender).spigot().sendMessage(new ComponentBuilder("")
                                        .append(TextComponent.fromLegacyText(i18n("dump_success") + ChatColor.GRAY))
                                        .append(msg1)
                                        .create());
                            } catch (Throwable ignored) {
                                sender.sendMessage(i18n("dump_success") + ChatColor.GRAY + "https://paste.gg/" + message.getResult().getId());
                            }
                        } else {
                            sender.sendMessage(i18n("dump_success") + ChatColor.GRAY + "https://paste.gg/" + message.getResult().getId());
                        }
                    } else {
                        sender.sendMessage(i18n("dump_failed"));
                        Bukkit.getLogger().severe(code + " - " + gson.fromJson(new InputStreamReader(connection.getErrorStream()), Map.class));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                sender.sendMessage(i18n("dump_failed"));
            }
        }).start();

        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            completion.addAll(Arrays.asList("paste.gg", "pastes.dev"));
        }
    }

    @Data
    public static class AFile {
        private final String name;
        private final String language;
        private final String content;
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
        HashMap<Object, Object> map = new HashMap<>();
        Object key = null;
        for (Object object : objects) {
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