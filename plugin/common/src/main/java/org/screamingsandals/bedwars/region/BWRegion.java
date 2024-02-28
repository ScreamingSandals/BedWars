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

import org.screamingsandals.bedwars.api.Region;
import org.screamingsandals.lib.api.types.server.BlockHolder;
import org.screamingsandals.lib.api.types.server.BlockPlacementHolder;
import org.screamingsandals.lib.api.types.server.BlockSnapshotHolder;
import org.screamingsandals.lib.api.types.server.ChunkHolder;
import org.screamingsandals.lib.api.types.server.LocationHolder;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.world.chunk.Chunk;
import org.screamingsandals.lib.block.snapshot.BlockSnapshot;

public interface BWRegion extends Region {

    boolean isLocationModifiedDuringGame(Location loc);

    void putOriginalBlock(Location loc, BlockSnapshot block);

    void addBuiltDuringGame(Location loc);

    void removeBlockBuiltDuringGame(Location loc);

    boolean isLiquid(Block material);

    boolean isBedBlock(BlockSnapshot block);

    boolean isBedHead(BlockSnapshot block);

    boolean isDoorBlock(BlockSnapshot block);

    boolean isDoorBottomBlock(BlockSnapshot block);

    BlockPlacement getBedNeighbor(BlockPlacement head);

    boolean isChunkUsed(Chunk chunk);

    void regen();


    @Override
    @Deprecated
    default boolean isLocationModifiedDuringGame(LocationHolder loc) {
        return isLocationModifiedDuringGame(loc.as(Location.class));
    }

    @Override
    @Deprecated
    default void markForRollback(LocationHolder loc, BlockSnapshotHolder blockSnapshot) {
        putOriginalBlock(loc.as(Location.class), blockSnapshot.as(BlockSnapshot.class));
    }

    @Override
    @Deprecated
    default void addBuiltDuringGame(LocationHolder loc) {
        addBuiltDuringGame(loc.as(Location.class));
    }

    @Override
    @Deprecated
    default void removeBuiltDuringGame(LocationHolder loc) {
        removeBlockBuiltDuringGame(loc.as(Location.class));
    }

    @Override
    @Deprecated
    default boolean isLiquid(BlockHolder blockHolder) {
        return isLiquid(blockHolder.as(Block.class));
    }

    @Override
    @Deprecated
    default boolean isBedBlock(BlockSnapshotHolder blockSnapshot) {
        return isBedBlock(blockSnapshot.as(BlockSnapshot.class));
    }

    @Override
    @Deprecated
    default boolean isBedHead(BlockSnapshotHolder blockSnapshot) {
        return isBedHead(blockSnapshot.as(BlockSnapshot.class));
    }

    @Override
    @Deprecated
    default BlockPlacement getBedNeighbor(BlockPlacementHolder blockHead) {
        return getBedNeighbor(blockHead.as(BlockPlacement.class));
    }

    @Override
    @Deprecated
    default boolean isChunkUsed(ChunkHolder chunk) {
        return isChunkUsed(chunk.as(Chunk.class));
    }

    @Override
    @Deprecated
    default boolean isDoorBlock(BlockSnapshotHolder blockSnapshot) {
        return isDoorBlock(blockSnapshot.as(BlockSnapshot.class));
    }

    @Override
    @Deprecated
    default boolean isDoorBottomBlock(BlockSnapshotHolder blockSnapshot) {
        return isDoorBottomBlock(blockSnapshot.as(BlockSnapshot.class));
    }
}
