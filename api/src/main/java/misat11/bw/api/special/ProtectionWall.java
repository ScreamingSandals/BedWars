package misat11.bw.api.special;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

public interface ProtectionWall extends SpecialItem {
	public int getBreakingTime();

	public int getWidth();
	
	public int getHeight();
	
	public int getDistance();
	
	public boolean canBreak();
	
	public Material getMaterial();
	
	public void runTask();
	
	public List<Block> getWallBlocks();
}
