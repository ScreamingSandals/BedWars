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

package org.screamingsandals.bedwars.region;

import org.screamingsandals.bedwars.utils.BedUtils;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.world.chunk.Chunk;
import org.screamingsandals.lib.block.snapshot.BlockSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlatteningRegion implements BWRegion {
    private final List<Location> builtBlocks = new ArrayList<>();
    private final Map<Location, Block> brokenOriginalBlocks = new HashMap<>();

    @Override
    public boolean isLocationModifiedDuringGame(Location loc) {
        return builtBlocks.contains(loc);
    }

    @Override
    public void putOriginalBlock(Location loc, BlockSnapshot block) {
        brokenOriginalBlocks.put(loc, block.block());
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
    public boolean isLiquid(Block material) {
        return material.isSameType("water", "lava");
    }

    @Override
    public boolean isBedBlock(BlockSnapshot block) {
        return BedUtils.isBedBlock(block.block());
    }

    @Override
    public void regen() {
        for (var block : builtBlocks) {
            var chunk = block.getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            block.getBlock().block(Block.air());
        }
        builtBlocks.clear();
        for (var block : brokenOriginalBlocks.entrySet()) {
            var chunk = block.getKey().getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            block.getKey().getBlock().block(block.getValue());
        }
        brokenOriginalBlocks.clear();
    }

    @Override
    public boolean isBedHead(BlockSnapshot block) {
        return isBedBlock(block) && "head".equals(block.block().get("part"));
    }

    @Override
    public boolean isDoorBlock(BlockSnapshot block) {
        return block.block().is("#doors");
    }

    @Override
    public boolean isDoorBottomBlock(BlockSnapshot block) {
        var type = block.block();
        return type.is("#doors") && "lower".equals(type.get("half"));
    }

    @Override
    public BlockPlacement getBedNeighbor(BlockPlacement head) {
        return BedUtils.getBedNeighbor(head);
    }

    @Override
    public boolean isChunkUsed(Chunk chunk) {
        if (chunk == null) {
            return false;
        }
        for (var loc : builtBlocks) {
            if (chunk.equals(loc.getChunk())) {
                return true;
            }
        }
        for (var loc : brokenOriginalBlocks.keySet()) {
            if (chunk.equals(loc.getChunk())) {
                return true;
            }
        }
        return false;
    }
}
