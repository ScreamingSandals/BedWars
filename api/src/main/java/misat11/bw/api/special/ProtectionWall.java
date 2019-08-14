package misat11.bw.api.special;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author Bedwars Team
 *
 */
public interface ProtectionWall extends SpecialItem {
	/**
	 * @return
	 */
	public int getBreakingTime();

	/**
	 * @return
	 */
	public int getWidth();
	
	/**
	 * @return
	 */
	public int getHeight();
	
	/**
	 * @return
	 */
	public int getDistance();
	
	/**
	 * @return
	 */
	public boolean canBreak();
	
	/**
	 * @return
	 */
	public Material getMaterial();
	
	/**
	 * 
	 */
	public void runTask();
	
	/**
	 * @return
	 */
	public List<Block> getWallBlocks();
}
