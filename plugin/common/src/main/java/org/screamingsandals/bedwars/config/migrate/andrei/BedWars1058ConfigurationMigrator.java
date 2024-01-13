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
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.migrate.ConfigurationNodeMigrator;
import org.screamingsandals.bedwars.config.migrate.FileMigrator;
import org.screamingsandals.lib.utils.annotations.Service;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;

@Slf4j
@Service
public class BedWars1058ConfigurationMigrator implements FileMigrator {
    @Override
    public void migrate(File file) throws Exception {
        if (file.isFile()) {
            log.error("File '{}' not found, cannot continue with migration.", file.getName());
            return;
        }
        ConfigurationNodeMigrator.yaml(file, MainConfig.getInstance().getConfigurationNode())
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
    }
}
