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

package org.screamingsandals.bedwars.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

/**
 * @author Bedwars Team
 */
public interface Region {
    /**
     * @param loc
     * @return
     */
    boolean isBlockAddedDuringGame(Location loc);

    /**
     * @param loc
     * @param block
     */
    void putOriginalBlock(Location loc, BlockState block);

    /**
     * @param loc
     */
    void addBuiltDuringGame(Location loc);

    /**
     * @param loc
     */
    void removeBlockBuiltDuringGame(Location loc);

    /**
     * @param material
     * @return
     */
    boolean isLiquid(Material material);

    /**
     * @param block
     * @return
     */
    boolean isBedBlock(BlockState block);

    /**
     * @param block
     * @return
     */
    boolean isBedHead(BlockState block);

    boolean isDoorBlock(BlockState block);

    boolean isDoorBottomBlock(BlockState block);

    /**
     * @param head
     * @return
     */
    Block getBedNeighbor(Block head);

    /**
     * @param chunk
     * @return
     */
    boolean isChunkUsed(Chunk chunk);

    /**
     * Don't use from API
     */
    void regen();
}
