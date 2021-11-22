package org.screamingsandals.bedwars.config.migrate.bwrel;

import lombok.extern.slf4j.Slf4j;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.config.migrate.ConfigurationNodeMigrator;
import org.screamingsandals.bedwars.config.migrate.FileMigrator;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.WorldMapper;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Paths;
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
        final var arenaUUID = UUID.randomUUID();
        final var loader = YamlConfigurationLoader.builder().file(Paths.get(BedWarsPlugin.getInstance().getDataFolder().toAbsolutePath().toString(), "arenas", arenaUUID + ".yml").toFile()).build();
        ConfigurationNodeMigrator.yaml(Paths.get(file.toPath().toAbsolutePath().toString(), "game.yml").toFile(), loader.createNode())
                .setExplicitly(arenaUUID.toString(), "uuid")
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
                // TODO: remap shopkeepers (fucking hell)
                .save(loader);
    }

    private LocationHolder parseLocation(ConfigurationNode node) {
        return new LocationHolder(
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble(),
                node.node("yaw").getFloat(),
                node.node("pitch").getFloat(),
                WorldMapper.getWorld(node.node("world").getString()).orElse(null)
        );
    }
}
