package misat11.bw.api;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Bedwars Team
 */
public interface ItemSpawnerType {
    /**
     * @return
     */
    String getConfigKey();

    /**
     * @return
     */
    ChatColor getColor();

    /**
     * @return
     */
    int getInterval();

    /**
     * @return
     */
    double getSpread();

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    Material getMaterial();

    /**
     * @return
     */
    String getTranslatableKey();

    /**
     * @return
     */
    String getItemName();

    /**
     * @return
     */
    String getItemBoldName();

    /**
     * @return
     */
    int getDamage();

    /**
     * @return
     */
    ItemStack getStack();

    /**
     * @param amount
     * @return
     */
    ItemStack getStack(int amount);
}
