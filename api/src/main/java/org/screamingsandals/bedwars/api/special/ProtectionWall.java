package org.screamingsandals.bedwars.api.special;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

/**
 * @author Bedwars Team
 */
public interface ProtectionWall extends SpecialItem {
    /**
     * @return
     */
    int getBreakingTime();

    /**
     * @return
     */
    int getWidth();

    /**
     * @return
     */
    int getHeight();

    /**
     * @return
     */
    int getDistance();

    /**
     * @return
     */
    boolean canBreak();

    /**
     * @return
     */
    Material getMaterial();

    /**
     *
     */
    void runTask();

    /**
     * @return
     */
    List<Block> getWallBlocks();
}
