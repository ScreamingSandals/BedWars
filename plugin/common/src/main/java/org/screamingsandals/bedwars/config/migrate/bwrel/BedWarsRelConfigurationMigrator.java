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

package org.screamingsandals.bedwars.config.migrate.bwrel;

import lombok.extern.slf4j.Slf4j;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.migrate.ConfigurationNodeMigrator;
import org.screamingsandals.bedwars.config.migrate.FileMigrator;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.utils.annotations.Service;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BedWarsRelConfigurationMigrator implements FileMigrator {
    @Override
    public void migrate(File file) throws Exception {
        if (file.isFile()) {
            log.error("File '{}' not found, cannot continue with migration.", file.getName());
            return;
        }
        ConfigurationNodeMigrator.yaml(file, MainConfig.getInstance().getConfigurationNode())
                .remap("chat-prefix").toNewPath("prefix")
                .remapWithoutChanges("allow-crafting")
                .remap("holographic-stats", "head-line").toNewPath("holograms", "leaderboard", "headline")
                .remap("bed-sound").withMapper((oldNode, newNode, keys) -> {
                    try {
                        newNode.node("sounds", "bed_destroyed", "sound").set(
                                oldNode.getString("").toLowerCase(Locale.ROOT).replace("_", ".")
                        );
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remapWithoutChanges("player-drops")
                .remapWithoutChanges("keep-inventory-on-death")
                .remap("breakable-blocks").withMapper((oldNode, newNode, keys) -> {
                    final var breakableBlockOldNode = oldNode.node(keys);
                    final var breakableBlockNewNode = newNode.node("breakable");
                    try {
                        final List<String> blocks = breakableBlockOldNode.node("list").getList(String.class, new ArrayList<>());
                        if (blocks.size() >= 1 && !Objects.equals(blocks.get(0), "none")) {
                            breakableBlockNewNode.node("enabled").set(true);
                            breakableBlockNewNode.node("blocks").set(blocks);
                        }
                        breakableBlockNewNode.node("blacklist-mode").set(breakableBlockOldNode.node("use-as-blacklist").getBoolean(false));
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remap("jointeam-entity", "show-name").toNewPath("jointeam-entity-show-name")
                .remapWithoutChanges("lobby-scoreboard")
                .remapWithoutChanges("friendlyfire")
                .remap("chat-to-all-prefix").withMapper((oldNode, newNode, keys) -> {
                    try {
                        final List<String> prefixes = oldNode.node(keys).getList(String.class, new ArrayList<>());
                        if (prefixes.size() > 0) {
                            newNode.node("chat", "all-chat-prefix").set(prefixes.get(0));
                        }
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remap("seperate-game-chat").toNewPath("chat", "separate-chat", "game")
                .remapWithoutChanges("statistics")
                .setExplicitly(0, "statistics", "scores", "final-kill")
                .remapWithoutChanges("database")
                .setExplicitly(false, "database", "useSSL")
                .remap("spectation-enabled").toNewPath("allow-spectator-join")
                .remap("respawn-protection").withMapper((oldNode, newNode, keys) -> {
                    try {
                        final var respawnProtection = oldNode.node(keys).getInt(0);
                        newNode.node("respawn", "protection-enabled").set(respawnProtection != 0);
                        newNode.node("respawn", "protection-time").set(respawnProtection);
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remap("allowed-commands").toNewPath("commands", "list")
                .setExplicitly(false, "commands", "blacklist-mode")
                .remap("bungeecord", "enabled").toNewPath("bungee", "enabled")
                .remap("bungeecord", "hubserver").toNewPath("bungee", "server")
                .remap("bungeecord", "full-restart").toNewPath("bungee", "serverRestart")
                .remap("resource").withMapper((oldNode, newNode, keys) -> {
                    final var resources = oldNode.node(keys).childrenMap();
                    for (final var entry : resources.entrySet()) {
                        try {
                            final var bwrelResource = entry.getValue();
                            final var sbwResource = newNode.node("resources", entry.getKey());
                            sbwResource.node("spread").raw(bwrelResource.node("spread").raw());
                            sbwResource.node("interval").set(Math.round(bwrelResource.node("spawn-interval").getInt() / 50F));
                            final var itemStack = bwrelResource.node("item").childrenList().get(0);
                            sbwResource.node("material").set(itemStack.node("type").getString());
                            final var name = itemStack.node("meta", "display-name").getString();
                            sbwResource.node("color").set(
                                    MiscUtils.fromLegacyColorCode(
                                            Objects.requireNonNullElse(MiscUtils.getFirstColorCode(name), "§f")
                                    ).toString().toUpperCase(Locale.ROOT)
                            );
                            sbwResource.node("name").set(MiscUtils.stripColor(name));
                        } catch (SerializationException e) {
                            log.error("An unexpected error occurred while migrating.", e);
                        }
                    }
                })
                .remapWithoutChanges("rewards", "enabled")
                .remap("rewards", "player-win").withMapper((oldNode, newNode, keys) -> {
                    try {
                        final List<String> commands = oldNode.node(keys).getList(String.class, new ArrayList<>());
                        if (commands.size() >= 1 && !Objects.equals(commands.get(0), "none")) {
                            newNode.node("rewards", "player-win").set(commands);
                        } else {
                            newNode.node("rewards", "player-win").set(new ArrayList<>());
                        }
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remap("rewards", "player-end-game").withMapper((oldNode, newNode, keys) -> {
                    try {
                        final List<String> commands = oldNode.node(keys).getList(String.class, new ArrayList<>());
                        if (commands.size() >= 1 && !Objects.equals(commands.get(0), "none")) {
                            newNode.node("rewards", "player-end-game").set(commands);
                        } else {
                            newNode.node("rewards", "player-end-game").set(new ArrayList<>());
                        }
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remap("rewards", "player-destroy-bed").withMapper((oldNode, newNode, keys) -> {
                    try {
                        final List<String> commands = oldNode.node(keys).getList(String.class, new ArrayList<>());
                        if (commands.size() >= 1 && !Objects.equals(commands.get(0), "none")) {
                            newNode.node("rewards", "player-destroy-bed").set(commands);
                        } else {
                            newNode.node("rewards", "player-destroy-bed").set(new ArrayList<>());
                        }
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remap("rewards", "player-kill").withMapper((oldNode, newNode, keys) -> {
                    try {
                        final List<String> commands = oldNode.node(keys).getList(String.class, new ArrayList<>());
                        if (commands.size() >= 1 && !Objects.equals(commands.get(0), "none")) {
                            newNode.node("rewards", "player-kill").set(commands);
                        } else {
                            newNode.node("rewards", "player-kill").set(new ArrayList<>());
                        }
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remapWithoutChanges("specials", "rescue-platform", "break-time")
                .remap("specials", "rescue-platform", "using-wait-time").toNewPath("specials", "rescue-platform", "delay")
                .remap("specials", "rescue-platform", "can-break").toNewPath("specials", "rescue-platform", "is-breakable")
                .remap("specials", "rescue-platform", "block").toNewPath("specials", "rescue-platform", "material")
                .remapWithoutChanges("specials", "protection-wall", "break-time")
                .remap("specials", "protection-wall", "wait-time").toNewPath("specials", "protection-wall", "delay")
                .remap("specials", "protection-wall", "can-break").toNewPath("specials", "protection-wall", "is-breakable")
                .remap("specials", "protection-wall", "block").toNewPath("specials", "protection-wall", "material")
                .remapWithoutChanges("specials", "protection-wall", "width")
                .remapWithoutChanges("specials", "protection-wall", "height")
                .remapWithoutChanges("specials", "protection-wall", "distance")
                .remap("specials", "magnetshoe", "probability").toNewPath("specials", "magnet-shoes", "probability")
                .remapWithoutChanges("specials", "warp-powder", "teleport-time")
                .remap("specials", "tntsheep", "speed").toNewPath("specials", "tnt-sheep", "speed")
                .remap("specials", "tntsheep", "fuse-time").toNewPath("specials", "tnt-sheep", "explosion-time")
                .remapWithoutChanges("specials", "arrow-blocker", "protection-time")
                .remap("specials", "arrow-blocker", "using-wait-time").toNewPath("specials", "arrow-blocker", "delay")
                .remap("sign").withMapper((oldNode, newNode, keys) -> {
                    try {
                        newNode.node("sign", "lines").set(
                                oldNode.node(keys).childrenMap().values().stream()
                                        .map(s -> s.getString("").replace("$title$", "[BedWars]")
                                                .replace("$currentplayers$", "%players%")
                                                .replace("$gamename$", "%arena%")
                                                .replace("$status$", "%status%")
                                        )
                                        .collect(Collectors.toUnmodifiableList())
                        );
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remap("player-settings", "one-stack-on-shift").toNewPath("sell-max-64-per-click-in-shop");
        MainConfig.getInstance().saveConfig();
    }
}
