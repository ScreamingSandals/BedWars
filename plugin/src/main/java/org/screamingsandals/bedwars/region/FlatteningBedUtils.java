package org.screamingsandals.bedwars.region;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;

public class FlatteningBedUtils {
    public static Block getBedNeighbor(Block head) {
        if (!(head.getBlockData() instanceof Bed)) {
            return null;
        }

        if (isBedBlock(head.getRelative(BlockFace.EAST))) {
            return head.getRelative(BlockFace.EAST);
        } else if (isBedBlock(head.getRelative(BlockFace.WEST))) {
            return head.getRelative(BlockFace.WEST);
        } else if (isBedBlock(head.getRelative(BlockFace.SOUTH))) {
            return head.getRelative(BlockFace.SOUTH);
        } else {
            return head.getRelative(BlockFace.NORTH);
        }
    }

    public static boolean isBedBlock(Block block) {
        if (block == null) {
            return false;
        }

        return block.getBlockData() instanceof Bed;
    }
}
