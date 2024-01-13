/*
 * Copyright (C) 2024 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.bukkit.region;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.*;
import org.screamingsandals.bedwars.region.BWRegion;
import org.screamingsandals.bedwars.utils.BedUtils;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.block.snapshot.BlockSnapshot;
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO: Check if FlatteningRegion can't be used
// (we don't have to migrate this to slib, because slib is not going to target any legacy platforms other than bukkit)
public class LegacyRegion implements BWRegion {
    private final List<Location> builtBlocks = new ArrayList<>();
    private final List<BlockPlacement> brokenBlocks = new ArrayList<>();
    private final HashMap<BlockPlacement, BlockPlacement> brokenBeds = new HashMap<>();
    private final HashMap<BlockPlacement, Byte> brokenBlockData = new HashMap<>();
    private final HashMap<BlockPlacement, BlockFace> brokenBlockFace = new HashMap<>();
    private final HashMap<BlockPlacement, Boolean> brokenBlockPower = new HashMap<>();
    private final HashMap<BlockPlacement, Material> brokenBlockTypes = new HashMap<>();
    private final HashMap<BlockPlacement, DyeColor> brokenBlockColors = new HashMap<>();

    @Override
    public boolean isLocationModifiedDuringGame(Location loc) {
        return builtBlocks.contains(loc);
    }

    @Override
    public void putOriginalBlock(Location loc, BlockSnapshot block) {
    	if (!block.block().isSameType("minecraft:legacy/bed_block")) {
    		brokenBlocks.add(loc.getBlock());
    	}

    	var bState = block.as(BlockState.class);
        if (bState.getData() instanceof Directional) {
            brokenBlockFace.put(loc.getBlock(), ((Directional) bState.getData()).getFacing());
        }

        brokenBlockTypes.put(loc.getBlock(), bState.getType());
        brokenBlockData.put(loc.getBlock(), bState.getData().getData());

        if (bState.getData() instanceof Redstone) {
            brokenBlockPower.put(loc.getBlock(), ((Redstone) bState.getData()).isPowered());
        }

        if (bState instanceof Colorable) {
            // Save bed color on 1.12.x
            brokenBlockColors.put(loc.getBlock(), ((Colorable) bState).getColor());
        }
        
        if (isBedHead(block)) {
        	brokenBeds.put(loc.getBlock(), getBedNeighbor(loc.getBlock()));
        }
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
    public void regen() {
        for (var block : builtBlocks) {
            var chunk = block.getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            block.getBlock().block(org.screamingsandals.lib.block.Block.air());
        }
        builtBlocks.clear();

        for (var block : brokenBlocks) {
            var chunk = block.location().getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            var bBlock = block.as(Block.class);
            bBlock.setType(brokenBlockTypes.get(block));
            try {
                // The method is no longer in API, but in legacy versions exists
                Block.class.getMethod("setData", byte.class).invoke(bBlock, brokenBlockData.get(block));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (brokenBlockFace.containsKey(block)) {
                var data = bBlock.getState().getData();
                if (data instanceof Directional) {
                    ((Directional) data).setFacingDirection(brokenBlockFace.get(block));
                    bBlock.getState().setData(data);
                }
            }

            if (bBlock.getState().getData() instanceof Lever) {
                var attach = (Lever) bBlock.getState().getData();
                var supportState = bBlock.getState();
                var initialState = bBlock.getState();
                attach.setPowered(brokenBlockPower.get(block));
                bBlock.getState().setData(attach);

                supportState.setType(Material.AIR);
                supportState.update(true, false);
                initialState.update(true);
            } else {
                bBlock.getState().update(true, true);
            }

            if (brokenBlockColors.containsKey(block) && bBlock.getState() instanceof Colorable) {
                // Update bed color on 1.12.x
                var state = bBlock.getState();
                ((Colorable) state).setColor(brokenBlockColors.get(block));
                state.update(true, false);
            }
        }
        
        for (var entry : brokenBeds.entrySet()) {
            var blockHead = entry.getKey();
            var blockFeed = entry.getValue();
            var bBlockHead = blockHead.as(Block.class);
            var bBlockFeed = blockFeed.as(Block.class);
            var headState = bBlockHead.getState();
            var feedState = bBlockFeed.getState();

            headState.setType(brokenBlockTypes.get(blockHead));
            feedState.setType(brokenBlockTypes.get(blockFeed));
            headState.setRawData((byte) 0x0);
            feedState.setRawData((byte) 0x8);
            feedState.update(true, false);
            headState.update(true, false);

            Bed bedHead = (Bed) headState.getData();
            bedHead.setHeadOfBed(true);
            bedHead.setFacingDirection(bBlockHead.getFace(bBlockFeed).getOppositeFace());

            Bed bedFeed = (Bed) feedState.getData();
            bedFeed.setHeadOfBed(false);
            bedFeed.setFacingDirection(bBlockFeed.getFace(bBlockHead));

            feedState.update(true, false);
            headState.update(true, true);
            headState = bBlockHead.getState();
            feedState = bBlockFeed.getState();

            if (brokenBlockColors.containsKey(blockFeed) && feedState instanceof Colorable) {
                // Update bed color on 1.12.x
            	((Colorable) feedState).setColor(brokenBlockColors.get(blockFeed));
                feedState.update(true, false);
            }

            if (brokenBlockColors.containsKey(blockHead) && headState instanceof Colorable) {
                // Update bed color on 1.12.x
            	((Colorable) headState).setColor(brokenBlockColors.get(blockHead));
                headState.update(true, true);
            }
        }
        brokenBeds.clear();
        
        brokenBlocks.clear();
        brokenBlockData.clear();
        brokenBlockFace.clear();
        brokenBlockPower.clear();
        brokenBlockTypes.clear();
        brokenBlockColors.clear();
    }

    @Override
    public boolean isLiquid(org.screamingsandals.lib.block.Block material) {
        return material.isSameType("water", "lava", "stationary_water", "stationary_lava");
    }

    @Override
    public boolean isBedBlock(BlockSnapshot block) {
        return block.as(BlockState.class).getData() instanceof Bed;
    }

    @Override
    public boolean isBedHead(BlockSnapshot block) {
        return isBedBlock(block) && ((Bed) block.as(BlockState.class).getData()).isHeadOfBed();
    }

    @Override
    public boolean isDoorBlock(BlockSnapshot block) {
        return block.as(BlockState.class).getData() instanceof Door;
    }

    @Override
    public boolean isDoorBottomBlock(BlockSnapshot block) {
        var data = block.as(BlockState.class).getData();
        return data instanceof Door && !((Door) data).isTopHalf();
    }

    @Override
    public BlockPlacement getBedNeighbor(BlockPlacement head) {
        return BedUtils.getBedNeighbor(head);
    }

    @Override
    public boolean isChunkUsed(Chunk chunk) {
        if (chunk == null) {
            return false;
        }
        for (var loc : builtBlocks) {
            if (chunk.equals(loc.getChunk())) {
                return true;
            }
        }
        for (var block : brokenBlocks) {
            if (chunk.equals(block.location().getChunk())) {
                return true;
            }
        }
        return false;
    }

}
