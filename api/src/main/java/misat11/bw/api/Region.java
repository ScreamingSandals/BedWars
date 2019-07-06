package misat11.bw.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public interface Region {
	public boolean isBlockAddedDuringGame(Location loc);
	
	public void putOriginalBlock(Location loc, BlockState block);
	
	public void addBuildedDuringGame(Location loc);
	
	public void removeBlockBuildedDuringGame(Location loc);
	
	public boolean isLiquid(Material material);
	
	public boolean isBedBlock(BlockState block);
	
	public boolean isBedHead(BlockState block);
	
	public Block getBedNeighbor(Block head);
}
