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

package org.screamingsandals.bedwars.region;

import org.screamingsandals.bedwars.utils.BedUtils;
import org.screamingsandals.lib.block.BlockHolder;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.chunk.ChunkHolder;
import org.screamingsandals.lib.block.state.BlockStateHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlatteningRegion implements BWRegion {
    private final List<LocationHolder> builtBlocks = new ArrayList<>();
    private final Map<LocationHolder, BlockTypeHolder> brokenOriginalBlocks = new HashMap<>();

    @Override
    public boolean isLocationModifiedDuringGame(LocationHolder loc) {
        return builtBlocks.contains(loc);
    }

    @Override
    public void putOriginalBlock(LocationHolder loc, BlockStateHolder block) {
        brokenOriginalBlocks.put(loc, block.getType());
    }

    @Override
    public void addBuiltDuringGame(LocationHolder loc) {
        builtBlocks.add(loc);
    }

    @Override
    public void removeBlockBuiltDuringGame(LocationHolder loc) {
        builtBlocks.remove(loc);
    }

    @Override
    public boolean isLiquid(BlockTypeHolder material) {
        return material.isSameType("water", "lava");
    }

    @Override
    public boolean isBedBlock(BlockStateHolder block) {
        return BedUtils.isBedBlock(block.getType());
    }

    @Override
    public void regen() {
        for (var block : builtBlocks) {
            var chunk = block.getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            block.getBlock().setType(BlockTypeHolder.air());
        }
        builtBlocks.clear();
        for (var block : brokenOriginalBlocks.entrySet()) {
            var chunk = block.getKey().getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            block.getKey().getBlock().setType(block.getValue());
        }
        brokenOriginalBlocks.clear();
    }

    @Override
    public boolean isBedHead(BlockStateHolder block) {
        return isBedBlock(block) && block.getType().get("part").map("head"::equals).orElse(true);
    }

    @Override
    public boolean isDoorBlock(BlockStateHolder block) {
        var type = block.getType();
        return type != null && type.is("#doors");
    }

    @Override
    public boolean isDoorBottomBlock(BlockStateHolder block) {
        var type = block.getType();
        return type != null && type.is("#doors") && type.get("half").map("lower"::equals).orElse(false);
    }

    @Override
    public BlockHolder getBedNeighbor(BlockHolder head) {
        return BedUtils.getBedNeighbor(head);
    }

    @Override
    public boolean isChunkUsed(ChunkHolder chunk) {
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
