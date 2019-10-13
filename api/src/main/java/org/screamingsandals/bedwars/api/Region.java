package org.screamingsandals.bedwars.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

/**
 * @author Bedwars Team
 */
public interface Region {
    /**
     * @param loc
     * @return
     */
    boolean isBlockAddedDuringGame(Location loc);

    /**
     * @param loc
     * @param block
     */
    void putOriginalBlock(Location loc, BlockState block);

    /**
     * @param loc
     */
    void addBuiltDuringGame(Location loc);

    /**
     * @param loc
     */
    @Deprecated
    default void addBuildedDuringGame(Location loc) {
        addBuiltDuringGame(loc);
    }

    /**
     * @param loc
     */
    void removeBlockBuiltDuringGame(Location loc);

    /**
     * @param loc
     */
    @Deprecated
    default void removeBlockBuildedDuringGame(Location loc) {
        removeBlockBuiltDuringGame(loc);
    }

    /**
     * @param material
     * @return
     */
    boolean isLiquid(Material material);

    /**
     * @param block
     * @return
     */
    boolean isBedBlock(BlockState block);

    /**
     * @param block
     * @return
     */
    boolean isBedHead(BlockState block);

    /**
     * @param head
     * @return
     */
    Block getBedNeighbor(Block head);

    /**
     * @param chunk
     * @return
     */
    boolean isChunkUsed(Chunk chunk);

    /**
     * Don't use from API
     */
    void regen();
}
