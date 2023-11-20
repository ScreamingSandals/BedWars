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

package org.screamingsandals.bedwars.config.migrate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

@Getter
@RequiredArgsConstructor
public final class ConfigurationNodeMigrator {
    private final ConfigurationNode oldNode;
    private final ConfigurationNode newNode;

    public static ConfigurationNodeMigrator yaml(File file, ConfigurationNode newNode) throws ConfigurateException {
        return new ConfigurationNodeMigrator(YamlConfigurationLoader.builder().file(file).build().load(), newNode);
    }

    public static ConfigurationNodeMigrator gson(File file, ConfigurationNode newNode) throws ConfigurateException {
        return new ConfigurationNodeMigrator(GsonConfigurationLoader.builder().file(file).build().load(), newNode);
    }

    public Remappable remap(Object... keys) {
        return new Remappable(this, keys);
    }

    public ConfigurationNodeMigrator remapWithoutChanges(Object... keys) {
        return new Remappable(this, keys).toNewPath(keys);
    }

    public ConfigurationNodeMigrator setExplicitly(Object value, Object... keys) throws ConfigurateException {
        newNode.node(keys).set(value);
        return this;
    }

    public void save(AbstractConfigurationLoader<?> loader) throws ConfigurateException {
        loader.save(newNode);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Remappable {
        private final ConfigurationNodeMigrator migrator;
        private final Object[] oldPath;

        public ConfigurationNodeMigrator withMapper(Mapper mapper) {
            mapper.map(migrator.oldNode, migrator.newNode, oldPath);
            return migrator;
        }

        public ConfigurationNodeMigrator toNewPath(Object... keys) {
            migrator.newNode.node(keys).raw(migrator.oldNode.node(oldPath).raw());
            return migrator;
        }

        public interface Mapper {
            void map(ConfigurationNode oldNode, ConfigurationNode newNode, Object[] oldPath);
        }
    }
}
