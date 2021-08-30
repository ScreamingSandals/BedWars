package org.screamingsandals.bedwars.region;

import org.screamingsandals.bedwars.api.Region;
import org.screamingsandals.lib.material.MaterialHolder;
import org.screamingsandals.lib.world.BlockHolder;
import org.screamingsandals.lib.world.BlockMapper;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.LocationMapper;
import org.screamingsandals.lib.world.chunk.ChunkHolder;
import org.screamingsandals.lib.world.chunk.ChunkMapper;
import org.screamingsandals.lib.world.state.BlockStateHolder;
import org.screamingsandals.lib.world.state.BlockStateMapper;

public interface BWRegion extends Region<BlockHolder> {

    boolean isBlockAddedDuringGame(LocationHolder loc);

    void putOriginalBlock(LocationHolder loc, BlockStateHolder block);

    void addBuiltDuringGame(LocationHolder loc);

    void removeBlockBuiltDuringGame(LocationHolder loc);

    boolean isLiquid(MaterialHolder material);

    boolean isBedBlock(BlockStateHolder block);

    boolean isBedHead(BlockStateHolder block);

    BlockHolder getBedNeighbor(BlockHolder head);

    boolean isChunkUsed(ChunkHolder chunk);

    void regen();


    @Override
    @Deprecated
    default boolean isBlockAddedDuringGame(Object loc) {
        return isBlockAddedDuringGame(LocationMapper.wrapLocation(loc));
    }

    @Override
    @Deprecated
    default void putOriginalBlock(Object loc, Object blockState) {
        putOriginalBlock(LocationMapper.wrapLocation(loc), BlockStateMapper.wrapBlockState(blockState).orElseThrow());
    }

    @Override
    @Deprecated
    default void addBuiltDuringGame(Object loc) {
        addBuiltDuringGame(LocationMapper.wrapLocation(loc));
    }

    @Override
    @Deprecated
    default void removeBlockBuiltDuringGame(Object loc) {
        removeBlockBuiltDuringGame(LocationMapper.wrapLocation(loc));
    }

    @Override
    @Deprecated
    default boolean isLiquid(Object material) {
        return isLiquid(MaterialHolder.of(material));
    }

    @Override
    @Deprecated
    default boolean isBedBlock(Object blockState) {
        return isBedBlock(BlockStateMapper.wrapBlockState(blockState).orElseThrow());
    }

    @Override
    @Deprecated
    default boolean isBedHead(Object blockState) {
        return isBedHead(BlockStateMapper.wrapBlockState(blockState).orElseThrow());
    }

    @Override
    @Deprecated
    default BlockHolder getBedNeighbor(Object blockHead) {
        return getBedNeighbor(BlockMapper.wrapBlock(blockHead));
    }

    @Override
    @Deprecated
    default boolean isChunkUsed(Object chunk) {
        return isChunkUsed(ChunkMapper.wrapChunk(chunk).orElseThrow());
    }
}
