package org.screamingsandals.bedwars.api;

import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author ScreamingSandals
 */
public interface Region<B extends Wrapper> {
    /**
     * @param loc
     * @return
     */
    boolean isBlockAddedDuringGame(Object loc);

    /**
     * @param loc
     * @param blockState
     */
    void putOriginalBlock(Object loc, Object blockState);

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
     * @param chunk
     * @return
     */
    boolean isChunkUsed(Object chunk);
}
