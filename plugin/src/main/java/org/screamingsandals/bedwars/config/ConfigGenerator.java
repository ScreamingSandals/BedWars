/*
 * Copyright (C) 2022 ScreamingSandals
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

package org.screamingsandals.bedwars.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

// TODO: ScreamingLib candidate
@RequiredArgsConstructor
public class ConfigGenerator {
    private final AbstractConfigurationLoader<?> loader;
    private final ConfigurationNode mainNode;
    private boolean modified = false;

    public ConfigSection start() {
        return new ConfigSection(null, mainNode);
    }

    public void saveIfModified() throws ConfigurateException {
        if (modified) {
            loader.save(mainNode);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class ConfigSection {
        private final ConfigSection previous;
        private final ConfigurationNode section;

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        public class ConfigValue {
            private final ConfigurationNode selected;
            private ConfigurationNode migration;
            private Consumer<ConfigurationNode> remapConsumer;
            private boolean clearMigration = true;

            public ConfigValue migrateOld(Object... keys) throws SerializationException {
                return migrate(section, keys);
            }

            public ConfigValue migrateOldAbsoluteKey(Object... keys) throws SerializationException {
                return migrate(mainNode, keys);
            }

            private ConfigValue migrate(ConfigurationNode node, Object[] keys) throws SerializationException {
                if (migration != null) {
                    migrate();
                }
                this.migration = node.node(keys);
                return this;
            }

            public ConfigValue remap(Consumer<ConfigurationNode> remapConsumer) {
                this.remapConsumer = remapConsumer;
                return this;
            }

            public ConfigValue preventOldKeyRemove() {
                this.clearMigration = false;
                return this;
            }

            public ConfigSection defValue(Object defaultValue) throws SerializationException {
                return defValue(() -> defaultValue);
            }

            public ConfigSection defValue(Supplier<Object> defaultValue) throws SerializationException {
                if (migration != null) {
                    migrate();
                }
                if (selected.virtual()) {
                    selected.set(defaultValue.get());
                    modified = true;
                }
                return ConfigSection.this;
            }

            public ConfigSection moveIf(Predicate<ConfigurationNode> condition, Object... newKeys) throws SerializationException {
                if (!selected.virtual() && condition.test(selected)) {
                    section.node(newKeys).from(selected);
                    selected.set(null);
                    modified = true;
                }
                return ConfigSection.this;
            }

            public ConfigSection moveIfAbsolute(Predicate<ConfigurationNode> condition, Object... newKeys) throws SerializationException {
                if (!selected.virtual() && condition.test(selected)) {
                    var copy = selected.copy();
                    selected.set(null);
                    mainNode.node(newKeys).from(copy);
                    modified = true;
                }
                return ConfigSection.this;
            }

            private void migrate() throws SerializationException {
                if (!migration.virtual()) {
                    if (remapConsumer != null) {
                        remapConsumer.accept(this.migration);
                    }
                    if (selected.virtual()) {
                        selected.from(migration);
                        modified = true;
                    }
                    if (clearMigration) {
                        migration.set(null);
                        modified = true;
                    }
                    migration = null;
                    remapConsumer = null;
                }
            }
        }

        public ConfigSection section(Object... keys) {
            return new ConfigSection(this, section.node(keys));
        }

        public ConfigSection migrateOld(Object... keys) throws SerializationException {
            if (previous != null) {
                final var oldNode = previous.section.node(keys);
                if (!oldNode.virtual()) {
                    final var newNode = previous.section.node(section.key()).raw(oldNode.raw());
                    oldNode.set(null);
                    modified = true;
                    return new ConfigSection(previous, newNode);
                }
            }
            return ConfigSection.this;
        }

        public ConfigValue key(Object... keys) {
            return new ConfigValue(section.node(keys));
        }

        public ConfigSection drop(Object... keys) throws SerializationException {
            var drop = section.node(keys);
            if (!drop.virtual()) {
                section.node(keys).set(null);
                modified = true;
            }
            return this;
        }

        public ConfigSection back() {
            return Objects.requireNonNullElse(previous, this);
        }
    }
}
