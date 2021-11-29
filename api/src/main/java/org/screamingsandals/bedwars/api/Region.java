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
     *
     * @param loc the location
     * @param blockState old block state
     * @since 0.3.0
     */
    void markForRollback(Object loc, Object blockState);

    /**
     * <p>Marks a location for rollback.</p>
     *
     * @param loc the location
     * @param blockState old block state
     * @deprecated in favor of {@link Region#markForRollback(Object, Object)}
     */
    @Deprecated
    default void putOriginalBlock(Object loc, Object blockState) {
        markForRollback(loc, blockState);
    }

    /**
     * @param loc
     */
    void addBuiltDuringGame(Object loc);

    /**
     * @param loc
     */
    void removeBlockBuiltDuringGame(Object loc);

    /**
     * @param material
     * @return
     */
    boolean isLiquid(Object material);

    /**
     * @param blockState
     * @return
     */
    boolean isBedBlock(Object blockState);

    /**
     * @param blockState
     * @return
     */
    boolean isBedHead(Object blockState);

    /**
     * @param blockHead
     * @return
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
