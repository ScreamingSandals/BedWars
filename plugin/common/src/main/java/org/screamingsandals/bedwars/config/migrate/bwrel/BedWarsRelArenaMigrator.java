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

package org.screamingsandals.bedwars.config.migrate.bwrel;

import lombok.extern.slf4j.Slf4j;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.config.migrate.ConfigurationNodeMigrator;
import org.screamingsandals.bedwars.config.migrate.FileMigrator;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.world.Worlds;
import org.screamingsandals.lib.world.chunk.Chunk;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class BedWarsRelArenaMigrator implements FileMigrator {
    @Override
    public void migrate(File file) throws Exception {
        if (!file.isDirectory()) {
            log.error("Expected a directory at '{}', {}, cannot continue with migration.", file.getName(), (file.exists()) ? "found a file" : "found nothing");
            return;
        }
        if (!Server.isServerThread()) {
            throw new UnsupportedOperationException("This migrator needs to be run synchronously!");
        }
        final var arenaUUID = UUID.randomUUID();
        final var loader = YamlConfigurationLoader.builder().file(Paths.get(BedWarsPlugin.getInstance().getPluginDescription().dataFolder().toAbsolutePath().toString(), "arenas", arenaUUID + ".yml").toFile()).build();
        final var migrator = ConfigurationNodeMigrator.yaml(Paths.get(file.toPath().toAbsolutePath().toString(), "game.yml").toFile(), loader.createNode());
        final var world = Worlds.getWorld(migrator.getOldNode().node("world").getString());
        if (world != null) {
            final var pos1 = parseLocation(migrator.getOldNode().node("loc1"));
            final var pos2 = parseLocation(migrator.getOldNode().node("loc2"));
            final var chunks = new ArrayList<Chunk>();
            for (int x = Math.min(pos1.getChunk().getX(), pos2.getChunk().getX()); x <= Math.max(pos1.getChunk().getX(), pos2.getChunk().getX()); x += 16) {
                for (int z = Math.min(pos1.getChunk().getZ(), pos2.getChunk().getZ()); z <= Math.max(pos1.getChunk().getZ(), pos2.getChunk().getZ()); z += 16) {
                    chunks.add(Objects.requireNonNull(world.getChunkAt(x, z)));
                }
            }
            chunks.stream()
                    .peek(chunk -> {
                        if (!chunk.isLoaded()) {
                            chunk.load();
                        }
                    })
                    .flatMap(chunk -> Arrays.stream(chunk.getEntities()))
                    .filter(entity -> entity.getEntityType().is("minecraft:villager"))
                    .filter(entity -> entity.getLocation().getBlockY() >= Math.min(pos1.getBlockY(), pos2.getBlockY()) && entity.getLocation().getBlockY() <= Math.max(pos1.getBlockY(), pos2.getBlockY()))
                    .forEach(shopkeeper -> {
                        try {
                            final var shopNode = migrator.getNewNode().node("stores").appendListNode();
                            shopNode.node("loc").set(MiscUtils.writeLocationToString(shopkeeper.getLocation()));
                            shopNode.node("parent").set("true");
                            shopNode.node("type").set("VILLAGER");
                            shopNode.node("isBaby").set("false");
                            shopkeeper.remove(); // killing the shopkeeper to allow for bedwars to spawn a new one
                        } catch (SerializationException e) {
                            log.error("An unexpected error occurred while migrating.", e);
                        }
                    });
        } else {
            log.warn("Could not find world of arena '{}', shopkeepers will be missing!", file.getName());
        }
        migrator.setExplicitly(arenaUUID.toString(), "uuid")
                .remapWithoutChanges("name")
                .remapWithoutChanges("world")
                .remap("loc1").withMapper((oldNode, newNode, keys) -> {
                    try {
                        newNode.node("pos1").set(MiscUtils.writeLocationToString(parseLocation(oldNode.node(keys))));
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remap("loc2").withMapper((oldNode, newNode, keys) -> {
                    try {
                        newNode.node("pos2").set(MiscUtils.writeLocationToString(parseLocation(oldNode.node(keys))));
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remap("lobby").withMapper((oldNode, newNode, keys) -> {
                    try {
                        newNode.node("lobbySpawn").set(MiscUtils.writeLocationToString(parseLocation(oldNode.node(keys))));
                    } catch (SerializationException e) {
                        log.error("An unexpected error occurred while migrating.", e);
                    }
                })
                .remap("lobby", "world").toNewPath("lobbySpawnWorld")
                .remap("minplayers").toNewPath("minPlayers")
                .remap("time").toNewPath("gameTime")
                .remap("spawner").withMapper((oldNode, newNode, keys) -> {
                    for (final var generator : oldNode.node(keys).childrenList()) {
                        final var generatorNode = newNode.node("spawners").appendListNode();
                        try {
                            generatorNode.node("location").set(MiscUtils.writeLocationToString(parseLocation(generator.node("location"))));
                            generatorNode.node("type").raw(generator.node("name").raw());
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
                })
                .remap("teams").withMapper((oldNode, newNode, keys) -> {
                    for (final var team : oldNode.node(keys).childrenMap().entrySet()) {
                        final var bwrelteam = team.getValue();
                        final var teamNode = newNode.node("teams", team.getKey());
                        try {
                            teamNode.node("isNewColor").set(true);
                            teamNode.node("color").set(MiscUtils.convertColorToNewFormat(bwrelteam.node("color").getString(), false));
                            teamNode.node("maxPlayers").raw(oldNode.node("maxplayers").raw());
                            teamNode.node("bed").set(MiscUtils.writeLocationToString(parseLocation(bwrelteam.node("bedhead"))));
                            teamNode.node("spawn").set(MiscUtils.writeLocationToString(parseLocation(bwrelteam.node("spawn"))));
                        } catch (SerializationException e) {
                            log.error("An unexpected error occurred while migrating.", e);
                        }
                    }
                })
                .save(loader);
    }

    private Location parseLocation(ConfigurationNode node) {
        return new Location(
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble(),
                node.node("yaw").getFloat(),
                node.node("pitch").getFloat(),
                Objects.requireNonNull(Worlds.getWorld(node.node("world").getString()))
        );
    }
}
