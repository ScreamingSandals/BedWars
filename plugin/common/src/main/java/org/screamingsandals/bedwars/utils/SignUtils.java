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
import org.screamingsandals.lib.signs.ClickableSign;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.world.Location;

import java.util.Optional;

@UtilityClass
public class SignUtils {
    public Optional<BlockPlacement> getBlockBehindSign(ClickableSign sign) {
        return Optional.ofNullable(getGlassBehind(sign));
    }

    private BlockPlacement getGlassBehind(ClickableSign sign) {
        var location = sign.getLocation().as(Location.class);
        var block = location.getBlock();

        var type = block.block();
        if (!type.is("#signs")) {
            return null;
        }

        var data = type.get("facing");
        if (data != null) {
            return location.add(BlockFace.valueOf(data).getOppositeFace()).getBlock();
        } else {
            return location.add(BlockFace.DOWN).getBlock();
        }
    }
}
