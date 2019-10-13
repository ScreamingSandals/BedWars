package org.screamingsandals.bedwars.api.special;

import org.bukkit.inventory.ItemStack;

/**
 * @author Bedwars Team
 */
public interface WarpPowder extends SpecialItem {
    /**
     * @param removeSpecial
     * @param showMessage
     * @param decrementStack
     */
    public void cancelTeleport(boolean removeSpecial, boolean showMessage, boolean decrementStack);

    /**
     * @return
     */
    public ItemStack getStack();

    /**
     *
     */
    public void runTask();
}
