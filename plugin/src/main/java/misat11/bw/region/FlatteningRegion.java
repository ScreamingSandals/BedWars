package misat11.bw.region;

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

public class FlatteningRegion implements IRegion {
	private List<Location> builtBlocks = new ArrayList<>();
	private Map<Location, BlockData> brokenOriginalBlocks = new HashMap<>();

	@Override
	public boolean isBlockAddedDuringGame(Location loc) {
		return builtBlocks.contains(loc);
	}

	@Override
	public void putOriginalBlock(Location loc, BlockState block) {
		brokenOriginalBlocks.put(loc, block.getBlockData());
	}

	@Override
	public void addBuiltDuringGame(Location loc) {
		builtBlocks.add(loc);
	}

	@Override
	public void removeBlockBuiltDuringGame(Location loc) {
		builtBlocks.remove(loc);
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
		for (Location block : builtBlocks) {
			Chunk chunk = block.getChunk();
			if (!chunk.isLoaded()) {
				chunk.load();
			}
			block.getBlock().setType(Material.AIR);
		}
		builtBlocks.clear();
		for (Map.Entry<Location, BlockData> block : brokenOriginalBlocks.entrySet()) {
			Chunk chunk = block.getKey().getChunk();
			if (!chunk.isLoaded()) {
				chunk.load();
			}
			block.getKey().getBlock().setBlockData(block.getValue());
		}
		brokenOriginalBlocks.clear();
	}

	@Override
	public boolean isBedHead(BlockState block) {
		return isBedBlock(block) && ((Bed) block.getBlockData()).getPart() == Part.HEAD;
	}

	@Override
	public Block getBedNeighbor(Block head) {
		return FlatteningBedUtils.getBedNeighbor(head);
	}

	@Override
	public boolean isChunkUsed(Chunk chunk) {
		if (chunk == null) {
			return false;
		}
		for (Location loc : builtBlocks) {
			if (chunk.equals(loc.getChunk())) {
				return true;
			}
		}
		for (Location loc : brokenOriginalBlocks.keySet()) {
			if (chunk.equals(loc.getChunk())) {
				return true;
			}
		}
		return false;
	}
}
