package org.screamingsandals.bedwars.config.migrate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.lib.utils.TriConsumer;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

@RequiredArgsConstructor
public final class ConfigurationNodeMigrator {
    private final ConfigurationNode oldNode;
    private final ConfigurationNode newNode;

    public static ConfigurationNodeMigrator yaml(File file, ConfigurationNode newNode) throws ConfigurateException {
        return new ConfigurationNodeMigrator(YamlConfigurationLoader.builder().file(file).build().load(), newNode);
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

    public ConfigurationNode end() {
        return newNode;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Remappable {
        private final ConfigurationNodeMigrator migrator;
        private final Object[] oldPath;

        public ConfigurationNodeMigrator withMapper(TriConsumer<ConfigurationNode, ConfigurationNode, Object[]> mapper) {
            mapper.accept(migrator.oldNode, migrator.newNode, oldPath);
            return migrator;
        }

        public ConfigurationNodeMigrator toNewPath(Object... keys) {
            migrator.newNode.node(keys).raw(migrator.oldNode.node(oldPath).raw());
            return migrator;
        }
    }
}
