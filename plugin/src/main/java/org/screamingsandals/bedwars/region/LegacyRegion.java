/*
 * Copyright (C) 2023 ScreamingSandals
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

package org.screamingsandals.bedwars.region;

import org.screamingsandals.bedwars.api.Region;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LegacyRegion implements Region {
    private List<Location> builtBlocks = new ArrayList<>();
    private List<Block> brokenBlocks = new ArrayList<>();
    private HashMap<Block, Block> brokenBeds = new HashMap<>();
    private HashMap<Block, Byte> brokenBlockData = new HashMap<>();
    private HashMap<Block, BlockFace> brokenBlockFace = new HashMap<>();
    private HashMap<Block, Boolean> brokenBlockPower = new HashMap<>();
    private HashMap<Block, Material> brokenBlockTypes = new HashMap<>();
    private HashMap<Block, DyeColor> brokenBlockColors = new HashMap<>();

    @Override
    public boolean isBlockAddedDuringGame(Location loc) {
        return builtBlocks.contains(loc);
    }

    @Override
    public void putOriginalBlock(Location loc, BlockState block) {
    	if (!block.getType().name().equals("BED_BLOCK")) {
    		brokenBlocks.add(loc.getBlock());
    	}

        if (block.getData() instanceof Directional) {
            brokenBlockFace.put(loc.getBlock(), ((Directional) block.getData()).getFacing());
        }

        brokenBlockTypes.put(loc.getBlock(), block.getType());
        brokenBlockData.put(loc.getBlock(), block.getData().getData());

        if (block.getData() instanceof Redstone) {
            brokenBlockPower.put(loc.getBlock(), ((Redstone) block.getData()).isPowered());
        }

        if (block instanceof Colorable) {
            // Save bed color on 1.12.x
            brokenBlockColors.put(loc.getBlock(), ((Colorable) block).getColor());
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
        for (Location block : builtBlocks) {
            Chunk chunk = block.getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            block.getBlock().setType(Material.AIR);
        }
        builtBlocks.clear();

        for (Block block : brokenBlocks) {
            Chunk chunk = block.getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            block.setType(brokenBlockTypes.get(block));
            try {
                // The method is no longer in API, but in legacy versions exists
                Block.class.getMethod("setData", byte.class).invoke(block, brokenBlockData.get(block));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (brokenBlockFace.containsKey(block)) {
                MaterialData data = block.getState().getData();
                if (data instanceof Directional) {
                    ((Directional) data).setFacingDirection(brokenBlockFace.get(block));
                    block.getState().setData(data);
                }
            }

            if (block.getState().getData() instanceof Lever) {
                Lever attach = (Lever) block.getState().getData();
                BlockState supportState = block.getState();
                BlockState initalState = block.getState();
                attach.setPowered(brokenBlockPower.get(block));
                block.getState().setData(attach);

                supportState.setType(Material.AIR);
                supportState.update(true, false);
                initalState.update(true);
            } else {
                block.getState().update(true, true);
            }

            if (brokenBlockColors.containsKey(block) && block.getState() instanceof Colorable) {
                // Update bed color on 1.12.x
                BlockState state = block.getState();
                ((Colorable) state).setColor(brokenBlockColors.get(block));
                state.update(true, false);
            }
        }
        
        for (Map.Entry<Block, Block> entry : brokenBeds.entrySet()) {
            Block blockHead = entry.getKey();
            Block blockFeed = entry.getValue();
            BlockState headState = blockHead.getState();
            BlockState feedState = blockFeed.getState();

            headState.setType(brokenBlockTypes.get(blockHead));
            feedState.setType(brokenBlockTypes.get(blockFeed));
            headState.setRawData((byte) 0x0);
            feedState.setRawData((byte) 0x8);
            feedState.update(true, false);
            headState.update(true, false);

            Bed bedHead = (Bed) headState.getData();
            bedHead.setHeadOfBed(true);
            bedHead.setFacingDirection(blockHead.getFace(blockFeed).getOppositeFace());

            Bed bedFeed = (Bed) feedState.getData();
            bedFeed.setHeadOfBed(false);
            bedFeed.setFacingDirection(blockFeed.getFace(blockHead));

            feedState.update(true, false);
            headState.update(true, true);
            headState = blockHead.getState();
            feedState = blockFeed.getState();

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
    public boolean isDoorBlock(BlockState block) {
        return block.getData() instanceof Door;
    }

    @Override
    public boolean isDoorBottomBlock(BlockState block) {
        return block.getData() instanceof Door && !((Door) block.getData()).isTopHalf();
    }

    @Override
    public Block getBedNeighbor(Block head) {
        return LegacyBedUtils.getBedNeighbor(head);
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
        for (Block block : brokenBlocks) {
            if (chunk.equals(block.getChunk())) {
                return true;
            }
        }
        return false;
    }

}
