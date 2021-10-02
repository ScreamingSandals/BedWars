package org.screamingsandals.bedwars.region;

import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.block.BlockHolder;

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
        var data = block.getCurrentType();

        return data.platformName().toLowerCase().endsWith("_bed") && data.get("part").isPresent();
    }

    public static boolean isBedBlock(BlockTypeHolder data) {
        return data != null && data.platformName().toLowerCase().endsWith("_bed") && data.get("part").isPresent();
    }
}
