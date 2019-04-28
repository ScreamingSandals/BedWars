package misat11.bw.api;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ItemSpawnerType {
	public String getConfigKey();

	public ChatColor getColor();

	public int getInterval();

	public double getSpread();

	public String getName();

	public Material getMaterial();

	public String getTranslatableKey();

	public String getItemName();

	public String getItemBoldName();

	public int getDamage();

	public ItemStack getStack();
}
