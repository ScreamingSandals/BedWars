package misat11.bw.api.special;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface RescuePlatform extends SpecialItem {
	public int getBreakingTime();
	
	public boolean canBreak();
	
	public Material getMaterial();

	public ItemStack getStack();
	
	public void runTask();
	
	public List<Block> getPlatformBlocks();
}
