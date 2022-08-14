/*
 * Copyright (C) 2022 ScreamingSandals
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

package org.screamingsandals.bedwars.utils;

import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.block.BlockHolder;

public class BedUtils {
    public static BlockHolder getBedNeighbor(BlockHolder head) {
        if (!isBedBlock(head)) {
            return null;
        }

        if (isBedBlock(head.getLocation().add(BlockFace.EAST).getBlock())) {
            return head.getLocation().add(BlockFace.EAST).getBlock();
        } else if (isBedBlock(head.getLocation().add(BlockFace.WEST).getBlock())) {
            return head.getLocation().add(BlockFace.WEST).getBlock();
        } else if (isBedBlock(head.getLocation().add(BlockFace.SOUTH).getBlock())) {
            return head.getLocation().add(BlockFace.SOUTH).getBlock();
        } else {
            return head.getLocation().add(BlockFace.NORTH).getBlock();
        }
    }

    public static boolean isBedBlock(BlockHolder block) {
        if (block == null) {
            return false;
        }
        var data = block.getCurrentType();

        return data.is("#beds");
    }

    public static boolean isBedBlock(BlockTypeHolder data) {
        return data != null && data.is("#beds");
    }
}
