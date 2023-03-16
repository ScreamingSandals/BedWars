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

package org.screamingsandals.bedwars.region;

import org.screamingsandals.bedwars.api.Region;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlatteningRegion implements Region {
    private List<Location> builtBlocks = new ArrayList<>();
    private Map<Location, BlockData> brokenOriginalBlocks = new HashMap<>();

    @Override
    public boolean isBlockAddedDuringGame(Location loc) {
        return builtBlocks.contains(loc);
    }

    @Override
    public void putOriginalBlock(Location loc, BlockState block) {
        brokenOriginalBlocks.put(loc, block.getBlockData());
    }

    @Override
    public void addBuiltDuringGame(Location loc) {
        builtBlocks.add(loc);
    }

    @Override
    public void removeBlockBuiltDuringGame(Location loc) {
        builtBlocks.remove(loc);
    }

    @Override
    public boolean isLiquid(Material material) {
        return material == Material.WATER || material == Material.LAVA;
    }

    @Override
    public boolean isBedBlock(BlockState block) {
        return block.getBlockData() instanceof Bed;
    }

    @Override
    public void regen() {
        for (Location block : builtBlocks) {
            Chunk chunk = block.getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            block.getBlock().setType(Material.AIR);
        }
        builtBlocks.clear();
        for (Map.Entry<Location, BlockData> block : brokenOriginalBlocks.entrySet()) {
            Chunk chunk = block.getKey().getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            block.getKey().getBlock().setBlockData(block.getValue());
        }
        brokenOriginalBlocks.clear();
    }

    @Override
    public boolean isBedHead(BlockState block) {
        return isBedBlock(block) && ((Bed) block.getBlockData()).getPart() == Part.HEAD;
    }

    @Override
    public Block getBedNeighbor(Block head) {
        return FlatteningBedUtils.getBedNeighbor(head);
    }

    @Override
    public boolean isChunkUsed(Chunk chunk) {
        if (chunk == null) {
            return false;
        }
        for (Location loc : builtBlocks) {
            if (chunk.equals(loc.getChunk())) {
                return true;
            }
        }
        for (Location loc : brokenOriginalBlocks.keySet()) {
            if (chunk.equals(loc.getChunk())) {
                return true;
            }
        }
        return false;
    }
}
