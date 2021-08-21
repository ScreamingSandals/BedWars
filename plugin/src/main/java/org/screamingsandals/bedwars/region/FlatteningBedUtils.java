package org.screamingsandals.bedwars.region;

import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.world.BlockDataHolder;
import org.screamingsandals.lib.world.BlockHolder;

public class FlatteningBedUtils {
    public static BlockHolder getBedNeighbor(BlockHolder head) {
        if (!isBedBlock(head)) {
            return null;
        }

        if (isBedBlock(head.getLocation().add(BlockFace.EAST.getDirection()).getBlock())) {
            return head.getLocation().add(BlockFace.EAST.getDirection()).getBlock();
        } else if (isBedBlock(head.getLocation().add(BlockFace.WEST.getDirection()).getBlock())) {
            return head.getLocation().add(BlockFace.WEST.getDirection()).getBlock();
        } else if (isBedBlock(head.getLocation().add(BlockFace.SOUTH.getDirection()).getBlock())) {
            return head.getLocation().add(BlockFace.SOUTH.getDirection()).getBlock();
        } else {
            return head.getLocation().add(BlockFace.NORTH.getDirection()).getBlock();
        }
    }

    public static boolean isBedBlock(BlockHolder block) {
        if (block == null) {
            return false;
        }
        var data = block.getBlockData();

        return data.isPresent() && data.get().as(BlockData.class) instanceof Bed;
    }

    public static boolean isBedBlock(BlockDataHolder data) {
        return data != null && data.as(BlockData.class) instanceof Bed;
    }
}
