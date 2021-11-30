package org.screamingsandals.bedwars.api;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * <p>Abstract region API.</p>
 *
 * @author ScreamingSandals
 * @param <B> block impl (BlockHolder)
 */
@ApiStatus.NonExtendable
public interface Region<B extends Wrapper> {
    /**
     * <p>Determines if the supplied location was modified during a BedWars game.</p>
     *
     * @param loc the location
     * @return was the supplied location modified?
     * @since 0.3.0
     */
    boolean isLocationModifiedDuringGame(Object loc);

    /**
     * <p>Determines if the supplied location was modified during a BedWars game.</p>
     *
     * @param loc the location
     * @return was the supplied location modified?
     * @deprecated in favor of {@link Region#isLocationModifiedDuringGame(Object)}
     */
    @Deprecated
    default boolean isBlockAddedDuringGame(Object loc) {
        return isLocationModifiedDuringGame(loc);
    }

    /**
     * <p>Marks a location for rollback.</p>
     * <p>This should be used for restoring a broken block that was a part of the original world.</p>
     *
     * @param loc the location
     * @param blockState old block state (BlockStateHolder or the platform impl)
     * @since 0.3.0
     */
    void markForRollback(Object loc, Object blockState);

    /**
     * <p>Marks a location for rollback.</p>
     * <p>This should be used for restoring a broken block that was a part of the original world.</p>
     *
     * @param loc the location
     * @param blockState old block state (BlockStateHolder or the platform impl)
     * @deprecated in favor of {@link Region#markForRollback(Object, Object)}
     */
    @Deprecated
    default void putOriginalBlock(Object loc, Object blockState) {
        markForRollback(loc, blockState);
    }

    /**
     * <p>Schedules a location for removal (set to AIR) while rolling back.</p>
     *
     * @param loc the location
     */
    void addBuiltDuringGame(Object loc);

    /**
     * <p>Schedules a location for removal (set to AIR) while rolling back.</p>
     *
     * @param loc the location
     * @since 0.3.0
     */
    void removeBuiltDuringGame(Object loc);

    /**
     * <p>Schedules a location for removal (set to AIR) while rolling back.</p>
     *
     * @param loc the location
     * @deprecated in favor of {@link Region#removeBuiltDuringGame(Object)}
     */
    @Deprecated
    default void removeBlockBuiltDuringGame(Object loc) {
        removeBuiltDuringGame(loc);
    }

    /**
     * <p>Checks if a material is a liquid.</p>
     *
     * @param material the material (BlockTypeHolder or the platform impl)
     * @return is the material a liquid?
     */
    boolean isLiquid(Object material);

    /**
     * <p>Checks if a block state matches a bed block.</p>
     *
     * @param blockState the block state (BlockStateHolder or the platform impl)
     * @return does the block state match a bed block?
     */
    boolean isBedBlock(Object blockState);

    /**
     * <p>Checks if a block state matches a bed head block.</p>
     *
     * @param blockState the block state (BlockStateHolder or the platform impl)
     * @return does the block state match a bed head block?
     */
    boolean isBedHead(Object blockState);

    /**
     * <p>Gets the bed's neighbor block (the second part of the bed) from the bed head block.</p>
     *
     * @param blockHead the bed head block
     * @return the bed neighbor block
     */
    B getBedNeighbor(Object blockHead);

    /**
     * <p>Determines if anything was modified in the supplied chunk.</p>
     *
     * @param chunk the chunk
     * @return was anything in the chunk modified?
     */
    boolean isChunkUsed(Object chunk);
}
