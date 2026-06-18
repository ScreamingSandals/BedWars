/*
 * Copyright (C) 2025 ScreamingSandals
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
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.world.chunk.Chunk;

@UtilityClass
public class ArenaUtils {
    public boolean isInArea(Location l, Location p1, Location p2) {
        if (!p1.getWorld().equals(l.getWorld())) {
            return false;
        }

        double lx = l.getX();
        double p1x = p1.getX();
        double p2x = p2.getX();
        if (lx < Math.min(p1x, p2x) || lx > Math.max(p1x, p2x)) {
            return false;
        }

        double ly = l.getY();
        double p1y = p1.getY();
        double p2y = p2.getY();
        if (ly < Math.min(p1y, p2y) || ly > Math.max(p1y, p2y)) {
            return false;
        }

        double lz = l.getZ();
        double p1z = p1.getZ();
        double p2z = p2.getZ();
        return lz >= Math.min(p1z, p2z) && lz <= Math.max(p1z, p2z);
    }

    public boolean isChunkInArea(Chunk l, Location p1, Location p2) {
        if (!p1.getWorld().equals(l.getWorld())) {
            return false;
        }

        int lx = l.getX();
        int lz = l.getZ();

        int minChunkX = (int) Math.floor(Math.min(p1.getX(), p2.getX()) / 16.0);
        int maxChunkX = (int) Math.floor(Math.max(p1.getX(), p2.getX()) / 16.0);
        if (lx < minChunkX || lx > maxChunkX) {
            return false;
        }

        int minChunkZ = (int) Math.floor(Math.min(p1.getZ(), p2.getZ()) / 16.0);
        int maxChunkZ = (int) Math.floor(Math.max(p1.getZ(), p2.getZ()) / 16.0);
        return lz >= minChunkZ && lz <= maxChunkZ;
    }

    public static boolean arenaOverlaps(Location l1, Location l2, Location p1, Location p2) {
        if (!p1.getWorld().equals(l1.getWorld())) {
            return false;
        }

        double min1X = Math.min(l1.getX(), l2.getX());
        double max1X = Math.max(l1.getX(), l2.getX());
        double min2X = Math.min(p1.getX(), p2.getX());
        double max2X = Math.max(p1.getX(), p2.getX());
        if (min1X >= max2X || max1X <= min2X) {
            return false;
        }

        double min1Y = Math.min(l1.getY(), l2.getY());
        double max1Y = Math.max(l1.getY(), l2.getY());
        double min2Y = Math.min(p1.getY(), p2.getY());
        double max2Y = Math.max(p1.getY(), p2.getY());
        if (min1Y >= max2Y || max1Y <= min2Y) {
            return false;
        }

        double min1Z = Math.min(l1.getZ(), l2.getZ());
        double max1Z = Math.max(l1.getZ(), l2.getZ());
        double min2Z = Math.min(p1.getZ(), p2.getZ());
        double max2Z = Math.max(p1.getZ(), p2.getZ());
        return min1Z < max2Z && max1Z > min2Z;
    }
}
