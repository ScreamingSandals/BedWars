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

package org.screamingsandals.bedwars.config.migrate.andrei;

import lombok.extern.slf4j.Slf4j;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.config.migrate.ConfigurationNodeMigrator;
import org.screamingsandals.bedwars.config.migrate.FileMigrator;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.utils.annotations.Service;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class BedWars1058ArenaMigrator implements FileMigrator {
    @Override
    public void migrate(File file) throws Exception {
        if (!file.isFile()) {
            log.error("File '{}' not found, cannot continue with migration.", file.getName());
            return;
        }
        final var arenaUUID = UUID.randomUUID();
        final var name = file.getName().substring(0, file.getName().lastIndexOf("."));
        final var loader = YamlConfigurationLoader.builder().file(Paths.get(BedWarsPlugin.getInstance().getPluginDescription().dataFolder().toAbsolutePath().toString(), "arenas", arenaUUID + ".yml").toFile()).build();
        ConfigurationNodeMigrator.yaml(file, loader.createNode())
                .setExplicitly(arenaUUID.toString(), "uuid")
                .setExplicitly(name, "name")
                .setExplicitly(30, "pauseCountdown")
                // TODO: remap game time
                .setExplicitly(name, "world")
                .setExplicitly(MiscUtils.MAX_LOCATION, "pos1") // TODO: add native support for whole world arenas instead of this max_location shit
                .setExplicitly(MiscUtils.MIN_LOCATION, "pos2")
                .remap("waiting", "Loc").toNewPath("specSpawn")
                .remap("waiting", "Loc").toNewPath("lobbySpawn")
                .setExplicitly(name, "lobbySpawnWorld")
                .remapWithoutChanges("minPlayers")
                .setExplicitly(5, "postGameWaiting")
                .remap("displayName").withMapper((oldNode, newNode, keys) -> {
                    final var displayName = oldNode.node(keys).getString("");
                    try {
                        if (displayName.equals("")) {
                            newNode.node("displayName").set(name);
                        } else {
                            newNode.node("displayName").set(displayName);
                        }
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remap("Team").withMapper((oldNode, newNode, keys) -> {
                    for (final var team : oldNode.node(keys).childrenMap().entrySet()) {
                        final var bw1058team = team.getValue();
                        final var teamNode = newNode.node("teams", team.getKey());
                        final var shopNode = newNode.node("stores").appendListNode();
                        try {
                            // team remapping
                            teamNode.node("isNewColor").set(true);
                            teamNode.node("color").set(MiscUtils.convertColorToNewFormat(bw1058team.node("Color").getString(), false));
                            teamNode.node("maxPlayers").raw(oldNode.node("maxInTeam").raw());
                            teamNode.node("bed").raw(bw1058team.node("Bed").raw());
                            teamNode.node("spawn").raw(bw1058team.node("Spawn").raw());
                            // shop remapping
                            shopNode.node("loc").raw(bw1058team.node("Shop").raw());
                            shopNode.node("parent").set("true");
                            shopNode.node("type").set("VILLAGER");
                            shopNode.node("isBaby").set("false");
                            // TODO: remap upgrade store for certain popular server variant
                        } catch (SerializationException e) {
                            log.error("An unexpected error occurred while migrating.", e);
                        }
                    }
                })
                .remap("generator").withMapper((oldNode, newNode, keys) -> {
                    for (final var generatorGroup : oldNode.node(keys).childrenMap().entrySet()) {
                        final var resource = ((String) generatorGroup.getKey()).toLowerCase(Locale.ROOT);
                        for (final var generator : generatorGroup.getValue().childrenList()) {
                            final var generatorNode = newNode.node("spawners").appendListNode();
                            try {
                                generatorNode.node("location").raw(generator.raw());
                                generatorNode.node("type").set(resource);
                                generatorNode.node("startLevel").set(1.0);
                                generatorNode.node("hologramEnabled").set(true);
                                generatorNode.node("maxSpawnedResources").set(-1);
                                generatorNode.node("floatingEnabled").set(true);
                                generatorNode.node("rotationMode").set("Y");
                                generatorNode.node("hologramType").set("DEFAULT");
                            } catch (SerializationException e) {
                                log.error("An unexpected error occurred while migrating.", e);
                            }
                        }
                    }
                })
                .setExplicitly(Map.of("certain-popular-server-holograms", true), "constant")
                .setExplicitly("WORLD", "arenaTime")
                .setExplicitly("default", "arenaWeather")
                .setExplicitly("default", "lobbyBossBarColor")
                .setExplicitly("default", "gameBossBarColor")
                .save(loader);
    }
}
