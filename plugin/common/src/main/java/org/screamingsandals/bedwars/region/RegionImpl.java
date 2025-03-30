/*
 * Copyright (C) 2025 ScreamingSandals
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
import org.screamingsandals.bedwars.utils.BedUtils;
import org.screamingsandals.lib.api.types.server.BlockHolder;
import org.screamingsandals.lib.api.types.server.BlockPlacementHolder;
import org.screamingsandals.lib.api.types.server.BlockSnapshotHolder;
import org.screamingsandals.lib.api.types.server.ChunkHolder;
import org.screamingsandals.lib.api.types.server.LocationHolder;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.world.chunk.Chunk;
import org.screamingsandals.lib.block.snapshot.BlockSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionImpl implements Region {
    private final List<Location> builtBlocks = new ArrayList<>();
    private final Map<Location, BlockSnapshot> brokenOriginalBlocks = new HashMap<>();

    public boolean isLocationModifiedDuringGame(Location loc) {
        return builtBlocks.contains(loc);
    }

    public void putOriginalBlock(Location loc, BlockSnapshot block) {
        brokenOriginalBlocks.put(loc, block);
    }

    public void addBuiltDuringGame(Location loc) {
        builtBlocks.add(loc);
    }

    public void removeBlockBuiltDuringGame(Location loc) {
        builtBlocks.remove(loc);
    }

    public boolean isLiquid(Block material) {
        return material.isSameType("water", "lava");
    }

    public boolean isBedBlock(BlockSnapshot block) {
        return BedUtils.isBedBlock(block.block());
    }

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
            block.getValue().updateBlock(true, false);
        }
        brokenOriginalBlocks.clear();
    }

    public boolean isBedHead(BlockSnapshot block) {
        return isBedBlock(block) && "head".equals(block.block().get("part"));
    }

    public boolean isDoorBlock(BlockSnapshot block) {
        return block.block().is("#doors");
    }

    public boolean isDoorBottomBlock(BlockSnapshot block) {
        var type = block.block();
        return type.is("#doors") && "lower".equals(type.get("half"));
    }

    public BlockPlacement getBedNeighbor(BlockPlacement head) {
        return BedUtils.getBedNeighbor(head);
    }

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

    @Override
    @Deprecated
    public boolean isLocationModifiedDuringGame(LocationHolder loc) {
        return isLocationModifiedDuringGame(loc.as(Location.class));
    }

    @Override
    @Deprecated
    public void markForRollback(LocationHolder loc, BlockSnapshotHolder blockSnapshot) {
        putOriginalBlock(loc.as(Location.class), blockSnapshot.as(BlockSnapshot.class));
    }

    @Override
    @Deprecated
    public void addBuiltDuringGame(LocationHolder loc) {
        addBuiltDuringGame(loc.as(Location.class));
    }

    @Override
    @Deprecated
    public void removeBuiltDuringGame(LocationHolder loc) {
        removeBlockBuiltDuringGame(loc.as(Location.class));
    }

    @Override
    @Deprecated
    public boolean isLiquid(BlockHolder blockHolder) {
        return isLiquid(blockHolder.as(Block.class));
    }

    @Override
    @Deprecated
    public boolean isBedBlock(BlockSnapshotHolder blockSnapshot) {
        return isBedBlock(blockSnapshot.as(BlockSnapshot.class));
    }

    @Override
    @Deprecated
    public boolean isBedHead(BlockSnapshotHolder blockSnapshot) {
        return isBedHead(blockSnapshot.as(BlockSnapshot.class));
    }

    @Override
    @Deprecated
    public BlockPlacement getBedNeighbor(BlockPlacementHolder blockHead) {
        return getBedNeighbor(blockHead.as(BlockPlacement.class));
    }

    @Override
    @Deprecated
    public boolean isChunkUsed(ChunkHolder chunk) {
        return isChunkUsed(chunk.as(Chunk.class));
    }

    @Override
    @Deprecated
    public boolean isDoorBlock(BlockSnapshotHolder blockSnapshot) {
        return isDoorBlock(blockSnapshot.as(BlockSnapshot.class));
    }

    @Override
    @Deprecated
    public boolean isDoorBottomBlock(BlockSnapshotHolder blockSnapshot) {
        return isDoorBottomBlock(blockSnapshot.as(BlockSnapshot.class));
    }
}
