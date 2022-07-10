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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.screamingsandals.lib.utils.Wrapper;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.WorldMapper;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigSerializable
public class SerializableLocation implements Wrapper {
    private String world;
    private double x;
    private double y;
    private double z;
    private double yaw = 0;
    private double pitch = 0;

    public SerializableLocation(LocationHolder location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public boolean isWorldLoaded() {
        return WorldMapper.getWorld(world).isPresent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T as(Class<T> type) {
        if (type == LocationHolder.class) {
            var holder = new LocationHolder(x, y, z);
            holder.setYaw((float) this.yaw);
            holder.setPitch((float) this.pitch);
            holder.setWorld(WorldMapper.getWorld(world).orElseThrow());
            return (T) holder;
        }
        throw new UnsupportedOperationException("Unsupported type!");
    }
}
