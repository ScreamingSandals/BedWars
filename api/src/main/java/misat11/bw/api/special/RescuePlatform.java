package misat11.bw.api.special;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Bedwars Team
 *
 */
public interface RescuePlatform extends SpecialItem {
	/**
	 * @return
	 */
	public int getBreakingTime();
	
	/**
	 * @return
	 */
	public boolean canBreak();
	
	/**
	 * @return
	 */
	public Material getMaterial();

	/**
	 * @return
	 */
	public ItemStack getStack();
	
	/**
	 * 
	 */
	public void runTask();
	
	/**
	 * @return
	 */
	public List<Block> getPlatformBlocks();
}
