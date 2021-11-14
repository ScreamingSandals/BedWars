package org.screamingsandals.bedwars.config.migrate.andrei;

import lombok.extern.slf4j.Slf4j;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.config.migrate.ConfigurationNodeMigrator;
import org.screamingsandals.bedwars.config.migrate.Migrator;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.utils.annotations.Service;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

@Slf4j
@Service
public class BedWars1058ArenaMigrator implements Migrator {
    @Override
    public boolean migrate() {
        final var bw1058ArenaFolder = Paths.get(MiscUtils.getPluginsFolder("BedWars1058").toString(), "Arenas").toFile();
        if (bw1058ArenaFolder.isDirectory()) {
            try {
                Files.walkFileTree(bw1058ArenaFolder.toPath(), new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        final var arenaUUID = UUID.randomUUID();
                        final var sbwArenaFile = Paths.get(BedWarsPlugin.getInstance().getDataFolder().toAbsolutePath().toString(), "arenas", arenaUUID + ".yml").toFile();
                        final var loader = YamlConfigurationLoader.builder().file(sbwArenaFile).build();
                        ConfigurationNodeMigrator.yaml(file.toFile(), loader.createNode());
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                log.error("An unexpected critical error occurred while migrating.", e);
                return false;
            }
        }
        return true;
    }
}
