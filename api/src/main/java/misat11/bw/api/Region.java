package misat11.bw.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public interface Region {
	public boolean isBlockAddedDuringGame(Location loc);
	
	public void putOriginalBlock(Location loc, BlockState block);
	
	public void addBuiltDuringGame(Location loc);
	
	/* Archaic form of built */
	default void addBuildedDuringGame(Location loc) {
		addBuiltDuringGame(loc);
	}
	
	public void removeBlockBuiltDuringGame(Location loc);

	/* Archaic form of built */
	default void removeBlockBuildedDuringGame(Location loc) {
		removeBlockBuiltDuringGame(loc);
	}
	
	public boolean isLiquid(Material material);
	
	public boolean isBedBlock(BlockState block);
	
	public boolean isBedHead(BlockState block);
	
	public Block getBedNeighbor(Block head);
	
	public boolean isChunkUsed(Chunk chunk);
}
