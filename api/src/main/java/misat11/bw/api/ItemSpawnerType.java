package misat11.bw.api;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Bedwars Team
 *
 */
public interface ItemSpawnerType {
	/**
	 * @return
	 */
	public String getConfigKey();

	/**
	 * @return
	 */
	public ChatColor getColor();

	/**
	 * @return
	 */
	public int getInterval();

	/**
	 * @return
	 */
	public double getSpread();

	/**
	 * @return
	 */
	public String getName();

	/**
	 * @return
	 */
	public Material getMaterial();

	/**
	 * @return
	 */
	public String getTranslatableKey();

	/**
	 * @return
	 */
	public String getItemName();

	/**
	 * @return
	 */
	public String getItemBoldName();

	/**
	 * @return
	 */
	public int getDamage();

	/**
	 * @return
	 */
	public ItemStack getStack();

	/**
	 * @param amount
	 * @return
	 */
	public ItemStack getStack(int amount);
}
