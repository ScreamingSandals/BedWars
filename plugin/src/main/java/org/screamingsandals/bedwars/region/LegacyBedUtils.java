package org.screamingsandals.bedwars.region;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class LegacyBedUtils {
    public static Block getBedNeighbor(Block head) {
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

    public static boolean isBedBlock(Block isBed) {
        if (isBed == null) {
            return false;
        }

        return (isBed.getType() == Material.valueOf("BED") || isBed.getType() == Material.valueOf("BED_BLOCK"));
    }
}
