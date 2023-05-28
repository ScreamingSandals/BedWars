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

package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.screamingsandals.bedwars.lib.nms.accessors.EnderDragonAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.EntityAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

public class BossBarDragon extends FakeEntityNMS<EnderDragon> {

    public BossBarDragon(Location location) {
        super(construct(location));
        setInvisible(true);
    }

    public static Object construct(Location location) {
        try {
            final Object nmsEntity = EnderDragonAccessor.CONSTRUCTOR_0.get()
                    .newInstance(ClassStorage.getHandle(location.getWorld()));
            ClassStorage.getMethod(EntityAccessor.METHOD_ABS_MOVE_TO.get()).invokeInstance(
                    nmsEntity,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getYaw(),
                    location.getPitch()
            );
            return nmsEntity;
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public Location createPosition(Location position) {
        final Location clone = position.clone();
        clone.setPitch(0);
        clone.setYaw(0);
        clone.setY(clone.getY() - 500);
        return clone;
    }
}
