package misat11.bw.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

/**
 * @author Bedwars Team
 *
 */
public interface Region {
	/**
	 * @param loc
	 * @return
	 */
	public boolean isBlockAddedDuringGame(Location loc);
	
	/**
	 * @param loc
	 * @param block
	 */
	public void putOriginalBlock(Location loc, BlockState block);
	
	/**
	 * @param loc
	 */
	public void addBuiltDuringGame(Location loc);
	
	/**
	 * @param loc
	 */
	@Deprecated
	default void addBuildedDuringGame(Location loc) {
		addBuiltDuringGame(loc);
	}
	
	/**
	 * @param loc
	 */
	public void removeBlockBuiltDuringGame(Location loc);

	/**
	 * @param loc
	 */
	@Deprecated
	default void removeBlockBuildedDuringGame(Location loc) {
		removeBlockBuiltDuringGame(loc);
	}
	
	/**
	 * @param material
	 * @return
	 */
	public boolean isLiquid(Material material);
	
	/**
	 * @param block
	 * @return
	 */
	public boolean isBedBlock(BlockState block);
	
	/**
	 * @param block
	 * @return
	 */
	public boolean isBedHead(BlockState block);
	
	/**
	 * @param head
	 * @return
	 */
	public Block getBedNeighbor(Block head);
	
	/**
	 * @param chunk
	 * @return
	 */
	public boolean isChunkUsed(Chunk chunk);
	
	/**
	 * Don't use from API 
	 */
	public void regen();
}
