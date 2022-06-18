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

package org.screamingsandals.bedwars.api.config;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.ArenaTime;

import java.util.List;
import java.util.Optional;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
@ApiStatus.NonExtendable
public interface ConfigurationContainer {

    /**
     * Gets configuration from the key
     *
     * @param key Key of the configuration
     * @return configuration or empty optional
     * @since 0.3.0
     */
    <T> Optional<Configuration<T>> get(ConfigurationKey<T> key);

    /**
     * Gets configuration from the key
     *
     * @param key Key of the configuration
     * @return configuration or empty optional
     * @since 0.3.0
     */
    <T> Optional<Configuration<List<T>>> get(ConfigurationListKey<T> key);

    /**
     * Registers new configuration type. This allows addons to save information directly to game.
     *
     * @param key Key of new configuration, it's invalid to use dots or colons
     * @return true on success
     * @since 0.3.0
     */
    <T> boolean register(ConfigurationKey<T> key);

    /**
     * Registers new configuration type. This allows addons to save information directly to the game or the variant.
     *
     * @param key Key of new configuration, it's invalid to use dots or colons
     * @return true on success
     * @since 0.3.0
     */
    <T> boolean register(ConfigurationListKey<T> key);

    /**
     * Gets all keys known by this configuration container
     *
     * @return list of all registered keys
     * @since 0.3.0
     */
    List<ConfigurationKey<?>> getRegisteredKeys();

    /**
     * Gets all list keys known by this configuration container
     *
     * @return list of all registered list keys
     * @since 0.3.0
     */
    List<ConfigurationListKey<?>> getRegisteredListKeys();

    /**
     * Gets the value from configuration or returns the default value
     *
     * @param key Key of the configuration
     * @param defaultValue Default value if the configuration won't be found
     * @return object from configuration if registered; otherwise defaultValue
     * @since 0.3.0
     */
    <T> T getOrDefault(ConfigurationKey<T> key, T defaultValue);

    /**
     * Gets the value from configuration or returns the default value
     *
     * @param key Key of the configuration
     * @param defaultValue Default value if the configuration won't be found
     * @return object from configuration if registered; otherwise defaultValue
     * @since 0.3.0
     */
    <T> List<T> getOrDefault(ConfigurationListKey<T> key, List<T> defaultValue);

    /**
     *
     * @return the parent configuration container or null
     * @since 0.3.0
     */
    @Nullable
    ConfigurationContainer getParentContainer();
}
