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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.lib.api.Wrapper;

/**
 * @author ScreamingSandals
 */
@ApiStatus.NonExtendable
public interface GameStore {
    /**
     * @return shop entity
     */
    @Nullable
    Wrapper getEntity();

    /**
     * @return entity type used for the shop
     */
    @NotNull
    Wrapper getEntityType();

    /**
     * @return location of this store
     */
    @NotNull
    Wrapper getStoreLocation();

    /**
     * @return shop file
     */
    @Nullable
    String getShopFile();

    /**
     * @return shopkeeper's name
     */
    @Nullable
    String getShopCustomName();

    /**
     * @return true if shopkeeper is baby
     */
    boolean isBaby();

    /**
     * @return if type is PLAYER, than returns skin, otherwise null
     */
    @Nullable
    String getSkinName();
}
