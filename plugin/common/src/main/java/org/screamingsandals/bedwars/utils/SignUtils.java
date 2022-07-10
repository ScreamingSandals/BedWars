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

import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.lib.bukkit.block.state.SignBlockStateHolder;
import org.screamingsandals.lib.signs.ClickableSign;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.block.BlockHolder;
import org.screamingsandals.lib.world.LocationHolder;

import java.util.Optional;

@UtilityClass
public class SignUtils {
    public Optional<BlockHolder> getBlockBehindSign(ClickableSign sign) {
        return Optional.ofNullable(getGlassBehind(sign));
    }

    private BlockHolder getGlassBehind(ClickableSign sign) {
        var location = sign.getLocation().as(LocationHolder.class);
        var block = location.getBlock();

        var state = block.getBlockState();
        if (state.isEmpty() || (state.get() instanceof SignBlockStateHolder)) {
            return null;
        }
        var sState = state.get();

        if (!BedWarsPlugin.isLegacy()) {
            var data = sState.getType().get("facing");
            if (data.isPresent()) {
                return location.add(BlockFace.valueOf(data.get()).getOppositeFace()).getBlock();
            } else {
                return location.add(BlockFace.DOWN).getBlock();
            }
        } else {
            if (sState.getType().isSameType("standing_sign")) {
                return location.add(BlockFace.DOWN).getBlock();
            } else {
                var data = sState.getType().legacyData();
                switch (data) {
                    case 3:
                        return location.add(BlockFace.NORTH).getBlock();
                    case 4:
                        return location.add(BlockFace.EAST).getBlock();
                    case 5:
                        return location.add(BlockFace.WEST).getBlock();
                    case 2:
                    default:
                        return location.add(BlockFace.SOUTH).getBlock();
                }
            }
        }
    }
}
