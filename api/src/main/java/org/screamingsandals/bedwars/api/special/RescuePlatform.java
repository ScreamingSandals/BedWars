package org.screamingsandals.bedwars.api.special;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Bedwars Team
 */
public interface RescuePlatform extends SpecialItem {
    /**
     * @return
     */
    int getBreakingTime();

    /**
     * @return
     */
    boolean canBreak();

    /**
     * @return
     */
    Material getMaterial();

    /**
     * @return
     */
    ItemStack getStack();

    /**
     *
     */
    void runTask();

    /**
     * @return
     */
    List<Block> getPlatformBlocks();
}
