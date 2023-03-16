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

package org.screamingsandals.bedwars.api.game;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 * @author Bedwars Team
 */
public interface GameStore {
    /**
     * @return shop entity
     */
    LivingEntity getEntity();

    /**
     * @return entity type used for the shop
     */
    EntityType getEntityType();

    /**
     * @return location of this store
     */
    Location getStoreLocation();

    /**
     * @return shop file
     */
    String getShopFile();

    /**
     * @return shopkeeper's name
     */
    String getShopCustomName();

    /**
     * @return true if shop file should be merged with custom shop file
     */
    boolean getUseParent();

    /**
     * @return true if shopkeeper has name
     */
    boolean isShopCustomName();

    /**
     * @return true if shopkeeper is baby
     */
    boolean isBaby();

    /**
     * @return if type is PLAYER, than returns skin, otherwise null
     */
    String getSkinName();
}
