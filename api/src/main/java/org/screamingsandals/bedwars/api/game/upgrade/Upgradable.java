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

package org.screamingsandals.bedwars.api.game.upgrade;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * EXPERIMENTAL: Upgrade API is subject to change.
 *
 * @author ScreamingSandals
 * @since 0.3.0
 */
@ApiStatus.NonExtendable
@ApiStatus.Experimental
public interface Upgradable {
    /**
     * Registers a new upgrade for this upgradable object.
     *
     * @param name upgrade name. To prevent collisions among plugins, it is recommended to use resource location-like names,
     *             e.g. {@code my_awesome_plugin:my_awesome_upgrade}, or any other identifier containing the plugin name.
     * @param initialLevel initial level
     * @throws IllegalStateException if the name is already registered on this upgradable object
     * @since 0.3.0
     */
    default @NotNull Upgrade registerUpgrade(@NotNull String name, double initialLevel) throws IllegalStateException {
        return registerUpgrade(name, initialLevel, null);
    }

    /**
     * Registers a new upgrade for this upgradable object.
     *
     * @param name upgrade name. To prevent collisions among plugins, it is recommended to use resource location-like names,
     *             e.g. {@code my_awesome_plugin:my_awesome_upgrade}, or any other identifier containing the plugin name.
     * @param initialLevel initial level
     * @param maxLevel maximal level or null
     * @throws IllegalStateException if the name is already registered on this upgradable object
     * @since 0.3.0
     */
    @NotNull Upgrade registerUpgrade(@NotNull String name, double initialLevel, @Nullable Double maxLevel) throws IllegalStateException;

    /**
     * Checks whether an upgrade with such name is registered
     *
     * @param name upgrade name
     * @return true if the upgrade is registered; otherwise false.
     * @since 0.3.0
     */
    default boolean isUpgradeRegistered(@NotNull String name) {
        return getUpgrade(name) != null;
    }

    /**
     * Gets a registered upgrade.
     *
     * @param name upgrade name
     * @return registered upgrade or null if the upgrade is not registered.
     * @since 0.3.0
     */
    @Nullable Upgrade getUpgrade(@NotNull String name);
}
