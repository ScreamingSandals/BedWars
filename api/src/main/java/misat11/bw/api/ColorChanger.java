package misat11.bw.api;

import org.bukkit.inventory.ItemStack;


/**
 * @author Bedwars Team
 *
 */
public interface ColorChanger {
	
	/**
	 * Apply color of team to ItemStack
	 * 
	 * @param color Color of team
	 * @param stack ItemStack that should be colored
	 * @return colored ItemStack or normal ItemStack if ItemStack can't be colored
	 */
	public ItemStack applyColor(TeamColor color, ItemStack stack);
}
