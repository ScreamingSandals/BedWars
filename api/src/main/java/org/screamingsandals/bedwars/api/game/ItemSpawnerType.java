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

package org.screamingsandals.bedwars.api.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Bedwars Team
 */
public interface ItemSpawnerType {
    /**
     * @return
     */
    String getConfigKey();

    /**
     * @return
     */
    ChatColor getColor();

    /**
     * @return
     */
    int getInterval();

    /**
     * @return
     */
    double getSpread();

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    Material getMaterial();

    /**
     * @return
     */
    String getTranslatableKey();

    /**
     * @return
     */
    String getItemName();

    /**
     * @return
     */
    String getItemBoldName();

    /**
     * @return
     */
    int getDamage();

    /**
     * @return
     */
    ItemStack getStack();

    /**
     * @param amount
     * @return
     */
    ItemStack getStack(int amount);
}
