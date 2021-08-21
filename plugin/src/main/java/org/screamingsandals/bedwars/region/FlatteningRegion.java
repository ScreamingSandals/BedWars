package org.screamingsandals.bedwars.region;

import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.screamingsandals.lib.material.MaterialHolder;
import org.screamingsandals.lib.material.MaterialMapping;
import org.screamingsandals.lib.world.BlockDataHolder;
import org.screamingsandals.lib.world.BlockHolder;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.chunk.ChunkHolder;
import org.screamingsandals.lib.world.state.BlockStateHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlatteningRegion implements BWRegion {
    private final List<LocationHolder> builtBlocks = new ArrayList<>();
    private final Map<LocationHolder, BlockDataHolder> brokenOriginalBlocks = new HashMap<>();

    @Override
    public boolean isBlockAddedDuringGame(LocationHolder loc) {
        return builtBlocks.contains(loc);
    }

    @Override
    public void putOriginalBlock(LocationHolder loc, BlockStateHolder block) {
        brokenOriginalBlocks.put(loc, block.getBlockData());
    }

    @Override
    public void addBuiltDuringGame(LocationHolder loc) {
        builtBlocks.add(loc);
    }

    @Override
    public void removeBlockBuiltDuringGame(LocationHolder loc) {
        builtBlocks.remove(loc);
    }

    @Override
    public boolean isLiquid(MaterialHolder material) {
        return material.is("water", "lava");
    }

    @Override
    public boolean isBedBlock(BlockStateHolder block) {
        return FlatteningBedUtils.isBedBlock(block.getBlockData());
    }

    @Override
    public void regen() {
        for (var block : builtBlocks) {
            var chunk = block.getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            block.getBlock().setType(MaterialMapping.getAir());
        }
        builtBlocks.clear();
        for (var block : brokenOriginalBlocks.entrySet()) {
            var chunk = block.getKey().getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            block.getKey().getBlock().setBlockData(block.getValue());
        }
        brokenOriginalBlocks.clear();
    }

    @Override
    public boolean isBedHead(BlockStateHolder block) {
        return isBedBlock(block) && ((Bed) block.getBlockData().as(BlockData.class)).getPart() == Bed.Part.HEAD;
    }

    @Override
    public BlockHolder getBedNeighbor(BlockHolder head) {
        return FlatteningBedUtils.getBedNeighbor(head);
    }

    @Override
    public boolean isChunkUsed(ChunkHolder chunk) {
        if (chunk == null) {
            return false;
        }
        for (var loc : builtBlocks) {
            if (chunk.equals(loc.getChunk())) {
                return true;
            }
        }
        for (var loc : brokenOriginalBlocks.keySet()) {
            if (chunk.equals(loc.getChunk())) {
                return true;
            }
        }
        return false;
    }
}
