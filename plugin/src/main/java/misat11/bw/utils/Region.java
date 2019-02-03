package misat11.bw.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;

public class Region implements IRegion {
	private List<Location> buildedBlocks = new ArrayList<Location>();
	private Map<Location, BlockData> breakedOriginalBlocks = new HashMap<Location, BlockData>();

	@Override
	public boolean isBlockAddedDuringGame(Location loc) {
		return buildedBlocks.contains(loc);
	}

	@Override
	public void putOriginalBlock(Location loc, BlockState block) {
		breakedOriginalBlocks.put(loc, block.getBlockData());
	}

	@Override
	public void addBuildedDuringGame(Location loc) {
		buildedBlocks.add(loc);
	}

	@Override
	public void removeBlockBuildedDuringGame(Location loc) {
		buildedBlocks.remove(loc);
	}

	@Override
	public boolean isLiquid(Material material) {
		return material == Material.WATER || material == Material.LAVA;
	}

	@Override
	public boolean isBedBlock(BlockState block) {
		return block.getBlockData() instanceof Bed;
	}

	@Override
	public void regen() {
		for (Location block : buildedBlocks) {
			Chunk chunk = block.getChunk();
			if (!chunk.isLoaded()) {
				chunk.load();
			}
			block.getBlock().setType(Material.AIR);
		}
		buildedBlocks.clear();
		for (Map.Entry<Location, BlockData> block : breakedOriginalBlocks.entrySet()) {
			Chunk chunk = block.getKey().getChunk();
			if (!chunk.isLoaded()) {
				chunk.load();
			}
			block.getKey().getBlock().setBlockData(block.getValue());
		}
		breakedOriginalBlocks.clear();
	}

	@Override
	public boolean isBedHead(BlockState block) {
		return isBedBlock(block) && ((Bed) block.getBlockData()).getPart() == Part.HEAD;
	}

	@Override
	public Block getBedNeighbor(Block head) {
		return BedUtils.getBedNeighbor(head);
	}
}
