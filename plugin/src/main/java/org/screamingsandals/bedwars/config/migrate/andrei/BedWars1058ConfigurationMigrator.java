package org.screamingsandals.bedwars.config.migrate.andrei;

import lombok.extern.slf4j.Slf4j;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.migrate.Migrator;
import org.screamingsandals.bedwars.config.migrate.ConfigurationNodeMigrator;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.utils.annotations.Service;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.nio.file.Paths;

@Slf4j
@Service
public class BedWars1058ConfigurationMigrator implements Migrator {
    @Override
    public boolean migrate() {
        final File bw1058ConfigFile = Paths.get(MiscUtils.getPluginsFolder("BedWars1058").toString(), "config.yml").toFile();
        if (bw1058ConfigFile.isFile()) {
            log.error("BedWars1058 configuration not found, cannot continue with migration.");
            return false;
        }
        try {
            ConfigurationNodeMigrator.yaml(bw1058ConfigFile, MainConfig.getInstance().getConfigurationNode())
                    .remap("lobbyServer").toNewPath("bungee", "server")
                    .remap("re-spawn-invulnerability").withMapper((oldNode, newNode, keys) -> {
                        final var protectionTime = oldNode.node(keys).getInt();
                        try {
                            newNode.node("respawn", "protection-enabled").set(protectionTime > 0);
                            if (protectionTime > 0) {
                                newNode.node("respawn", "protection-time").set(protectionTime / 1000);
                            }
                        } catch (SerializationException e) {
                            log.error("An unexpected error occurred while migrating.", e);
                        }
                    })
                    .remap("tnt-jump-settings", "y-axis-reduction-constant").toNewPath("tnt-jump", "reduce-y")
                    .remapWithoutChanges("database", "host")
                    .remapWithoutChanges("database", "port")
                    .setExplicitly("sbw", "database", "db")
                    .remapWithoutChanges("database", "user")
                    .remap("database", "pass").toNewPath("database", "password")
                    .remap("database", "ssl").toNewPath("database", "useSSL")
                    .remap("inventories", "disable-crafting-table").withMapper((oldNode, newNode, keys) -> {
                        try {
                            newNode.node("allow-crafting").set(!oldNode.node(keys).getBoolean(true));
                        } catch (SerializationException e) {
                            log.error("An unexpected error occurred while migrating.", e);
                        }
                    })
                    .remap("allowed-commands").toNewPath("commands", "list")
                    .setExplicitly(false, "commands", "blacklist-mode");
            MainConfig.getInstance().saveConfig();
        } catch (ConfigurateException e) {
            log.error("An unexpected critical error occurred while migrating.", e);
            return false;
        }
        return true;
    }
}
