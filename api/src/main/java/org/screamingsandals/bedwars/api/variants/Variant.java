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

package org.screamingsandals.bedwars.api.variants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;

import java.util.List;

/**
 * Represents a game variant (Classic BedWars, "Certain popular server" BedWars, etc.)
 *
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface Variant {

    /**
     *
     * @return name of this variant
     * @since 0.3.0
     */
    @NotNull
    String getName();

    /**
     * Returns configuration container for all games inheriting this variant
     *
     * @return game's configuration container
     * @since 0.3.0
     */
    @NotNull
    ConfigurationContainer getConfigurationContainer();

    /**
     * Returns all spawner types accessible in this game variant.
     * This includes custom defined and also default spawner types if they are not overridden and {@link #isDefaultItemSpawnerTypesIncluded()} is true.
     *
     * @return list of all spawner types
     * @since 0.3.0
     */
    @NotNull
    List<? extends @NotNull ItemSpawnerType> getItemSpawnerTypes();

    /**
     * Returns names of all spawner types accessible in this game variant.
     * This includes custom defined and also default spawner types if they are not overridden and {@link #isDefaultItemSpawnerTypesIncluded()} is true.
     *
     * @return list of names of all spawner types
     * @since 0.3.0
     */
    @NotNull
    List<String> getItemSpawnerTypeNames();

    /**
     * Returns all custom spawner types defined in this variant
     *
     * @return list of custom spawner types
     * @since 0.3.0
     */
    @NotNull
    List<? extends @NotNull ItemSpawnerType> getCustomItemSpawnerTypes();

    /**
     * Checks if default spawner types are included in this variant or not.
     *
     * @return true if variant includes default spawner types; otherwise false
     * @since 0.3.0
     */
    boolean isDefaultItemSpawnerTypesIncluded();

    /**
     * Returns requested spawner type.
     *
     * @param key config key of the spawner type
     * @return requested type if exists; otherwise null
     * @since 0.3.0
     */
    @Nullable
    ItemSpawnerType getItemSpawnerType(@NotNull String key);


}
