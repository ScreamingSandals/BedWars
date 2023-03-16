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

package org.screamingsandals.bedwars.lib.signmanager;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.screamingsandals.bedwars.Main;

public class SignBlock {
    private final Location loc;
    private final String name;

    public SignBlock(Location loc, String name) {
        this.loc = loc;
        this.name = name;
    }

    public Location getLocation() {
        return loc;
    }

    public String getName() {
        return name;
    }

    public Optional<Block> getBlockBehindSign() {
        return Optional.ofNullable(getGlassBehind());
    }

    private Block getGlassBehind() {
        Block block = loc.getBlock();

        if (!(block.getState() instanceof Sign)) {
            return null;
        }

        if (!Main.isLegacy()) {
            if (block.getState().getBlockData() instanceof Directional) {
                Directional directional = (Directional) block.getState().getBlockData();
                BlockFace blockFace = directional.getFacing().getOppositeFace();
                return block.getRelative(blockFace);
            }
        } else {
            if (block.getState().getData() instanceof org.bukkit.material.Directional) {
                org.bukkit.material.Directional directional = (org.bukkit.material.Directional) block.getState().getData();
                BlockFace blockFace = directional.getFacing().getOppositeFace();
                return block.getRelative(blockFace);
            }
        }
        return block.getRelative(BlockFace.DOWN);
    }
}
