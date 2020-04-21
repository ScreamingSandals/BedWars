package org.screamingsandals.bedwars.api.special;

import org.bukkit.inventory.ItemStack;

/**
 * @author Bedwars Team
 */
public interface WarpPowder extends SpecialItem {
    /**
     * @param removeSpecial
     * @param showMessage
     */
    public void cancelTeleport(boolean removeSpecial, boolean showMessage);

    /**
     * @return
     */
    public ItemStack getStack();

    /**
     *
     */
    public void runTask();
}
