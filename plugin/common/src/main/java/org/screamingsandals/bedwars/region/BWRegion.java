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

import org.screamingsandals.bedwars.api.Region;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.block.BlockPlacements;
import org.screamingsandals.lib.impl.block.snapshot.BlockSnapshots;
import org.screamingsandals.lib.impl.world.chunk.Chunks;
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.impl.world.Locations;
import org.screamingsandals.lib.world.chunk.Chunk;
import org.screamingsandals.lib.block.snapshot.BlockSnapshot;

import java.util.Objects;

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
    default boolean isLocationModifiedDuringGame(Object loc) {
        return isLocationModifiedDuringGame(Locations.wrapLocation(loc));
    }

    @Override
    @Deprecated
    default void markForRollback(Object loc, Object blockState) {
        putOriginalBlock(Locations.wrapLocation(loc), Objects.requireNonNull(BlockSnapshots.wrapBlockSnapshot(blockState)));
    }

    @Override
    @Deprecated
    default void addBuiltDuringGame(Object loc) {
        addBuiltDuringGame(Locations.wrapLocation(loc));
    }

    @Override
    @Deprecated
    default void removeBuiltDuringGame(Object loc) {
        removeBlockBuiltDuringGame(Locations.wrapLocation(loc));
    }

    @Override
    @Deprecated
    default boolean isLiquid(Object material) {
        return isLiquid(Block.of(material));
    }

    @Override
    @Deprecated
    default boolean isBedBlock(Object blockState) {
        return isBedBlock(Objects.requireNonNull(BlockSnapshots.wrapBlockSnapshot(blockState)));
    }

    @Override
    @Deprecated
    default boolean isBedHead(Object blockState) {
        return isBedHead(Objects.requireNonNull(BlockSnapshots.wrapBlockSnapshot(blockState)));
    }

    @Override
    @Deprecated
    default BlockPlacement getBedNeighbor(Object blockHead) {
        return getBedNeighbor(BlockPlacements.resolve(blockHead));
    }

    @Override
    @Deprecated
    default boolean isChunkUsed(Object chunk) {
        return isChunkUsed(Objects.requireNonNull(Chunks.wrapChunk(chunk)));
    }

    @Override
    @Deprecated
    default boolean isDoorBlock(Object blockState) {
        return isDoorBlock(Objects.requireNonNull(BlockSnapshots.wrapBlockSnapshot(blockState)));
    }

    @Override
    @Deprecated
    default boolean isDoorBottomBlock(Object blockState) {
        return isDoorBottomBlock(Objects.requireNonNull(BlockSnapshots.wrapBlockSnapshot(blockState)));
    }
}
