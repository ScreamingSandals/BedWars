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
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.lib.api.Wrapper;
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.world.Worlds;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigSerializable
public class SerializableLocation implements Wrapper {
    private @NotNull String world;
    private double x;
    private double y;
    private double z;
    private double yaw = 0;
    private double pitch = 0;

    public SerializableLocation(@NotNull Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public boolean isWorldLoaded() {
        return Worlds.getWorld(world) != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @NotNull T as(@NotNull Class<T> type) {
        if (type == Location.class) {
            var holder = new Location(x, y, z, (float) this.yaw, (float) this.pitch, Objects.requireNonNull(Worlds.getWorld(world)));
            return (T) holder;
        }
        throw new UnsupportedOperationException("Unsupported type!");
    }
}
