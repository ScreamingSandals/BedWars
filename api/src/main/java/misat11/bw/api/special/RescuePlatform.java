package misat11.bw.api.special;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

public interface RescuePlatform extends SpecialItem {
	public int getBreakingTime();
	
	public boolean canBreak();
	
	public Material getMaterial();
	
	public void runTask();
	
	public List<Block> getPlatformBlocks();
}
