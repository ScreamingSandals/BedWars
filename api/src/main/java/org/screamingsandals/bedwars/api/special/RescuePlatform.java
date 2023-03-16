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

package org.screamingsandals.bedwars.api.special;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Bedwars Team
 */
public interface RescuePlatform extends SpecialItem {
    /**
     * @return
     */
    int getBreakingTime();

    /**
     * @return
     */
    boolean canBreak();

    /**
     * @return
     */
    Material getMaterial();

    /**
     * @return
     */
    ItemStack getStack();

    /**
     *
     */
    void runTask();

    /**
     * @return
     */
    List<Block> getPlatformBlocks();
}
