package org.screamingsandals.bedwars.api.special;

import org.bukkit.inventory.ItemStack;

/**
 * @author Bedwars Team
 */
public interface WarpPowder extends SpecialItem {
    /**
     * @param showCancelledMessage
     */
    void cancelTeleport(boolean showCancelledMessage);

    /**
     * @return
     */
    ItemStack getStack();

    /**
     *
     */
    void runTask();
}
