package misat11.bw.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Bed;
import org.bukkit.material.Directional;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Redstone;

import misat11.bw.utils.IRegion;

public class LegacyRegion implements IRegion {
	private List<Location> buildedBlocks = new ArrayList<Location>();
	private List<Block> breakedBlocks = new ArrayList<Block>();
	private HashMap<Block, Byte> breakedBlockData = new HashMap<Block, Byte>();
	private HashMap<Block, BlockFace> breakedBlockFace = new HashMap<Block, BlockFace>();
	private HashMap<Block, Boolean> breakedBlockPower = new HashMap<Block, Boolean>();
	private HashMap<Block, Material> breakedBlockTypes = new HashMap<Block, Material>();

	@Override
	public boolean isBlockAddedDuringGame(Location loc) {
		return buildedBlocks.contains(loc);
	}

	@Override
	public void putOriginalBlock(Location loc, BlockState block) {
		breakedBlocks.add(loc.getBlock());

		if (block.getData() instanceof Directional) {
			breakedBlockFace.put(loc.getBlock(), ((Directional) block.getData()).getFacing());
		}

		breakedBlockTypes.put(loc.getBlock(), block.getType());
		breakedBlockData.put(loc.getBlock(), block.getData().getData());

		if (block.getData() instanceof Redstone) {
			breakedBlockPower.put(loc.getBlock(), ((Redstone) block.getData()).isPowered());
		}
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
	public void regen() {
		for (Location block : buildedBlocks) {
			Chunk chunk = block.getChunk();
			if (!chunk.isLoaded()) {
				chunk.load();
			}
			block.getBlock().setType(Material.AIR);
		}
		buildedBlocks.clear();

		for (Block block : breakedBlocks) {
			Chunk chunk = block.getChunk();
			if (!chunk.isLoaded()) {
				chunk.load();
			}
			block.setType(breakedBlockTypes.get(block));
			try {
				// The method is no longer in API, but in legacy versions exists
				Block.class.getMethod("setData", byte.class).invoke(block, breakedBlockData.get(block));
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (breakedBlockFace.containsKey(block)) {
				MaterialData data = block.getState().getData();
				if (data instanceof Directional) {
					((Directional) data).setFacingDirection(breakedBlockFace.get(block));
					block.getState().setData(data);
				}
			}

			if (block.getState().getData() instanceof Lever) {
				Lever attach = (Lever) block.getState().getData();
				BlockState supportState = block.getState();
				BlockState initalState = block.getState();
				attach.setPowered(breakedBlockPower.get(block));
				block.getState().setData(attach);

				supportState.setType(Material.AIR);
				supportState.update(true, false);
				initalState.update(true);
			} else {
				block.getState().update(true, true);
			}
		}
		breakedBlocks.clear();
		breakedBlockData.clear();
		breakedBlockFace.clear();
		breakedBlockPower.clear();
		breakedBlockTypes.clear();
	}

	@Override
	public boolean isLiquid(Material material) {
		return material == Material.valueOf("WATER") || material == Material.valueOf("LAVA")
				|| material == Material.valueOf("STATIONARY_LAVA") || material == Material.valueOf("STATIONARY_WATER");
	}

	@Override
	public boolean isBedBlock(BlockState block) {
		return block.getData() instanceof Bed;
	}

	@Override
	public boolean isBedHead(BlockState block) {
		return isBedBlock(block) && ((Bed) block.getData()).isHeadOfBed();
	}

	@Override
	public Block getBedNeighbor(Block head) {
		return LegacyBedUtils.getBedNeighbor(head);
	}

}
