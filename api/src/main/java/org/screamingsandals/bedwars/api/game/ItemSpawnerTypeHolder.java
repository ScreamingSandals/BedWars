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

package org.screamingsandals.bedwars.api.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.variants.Variant;

/**
 * @since 0.3.0
 */
public interface ItemSpawnerTypeHolder {
    /**
     * @since 0.3.0
     */
    @NotNull String configKey();

    /**
     * @since 0.3.0
     */
    @Nullable ItemSpawnerType toSpawnerType(@NotNull LocalGame variant);

    /**
     * @since 0.3.0
     */
    @Nullable ItemSpawnerType toSpawnerType(@Nullable Variant variant);

    /**
     * @since 0.3.0
     */
    default boolean isValid(@NotNull LocalGame game) {
        return toSpawnerType(game) != null;
    }

    /**
     * @since 0.3.0
     */
    default boolean isValid(@Nullable Variant variant) {
        return toSpawnerType(variant) != null;
    }
}
