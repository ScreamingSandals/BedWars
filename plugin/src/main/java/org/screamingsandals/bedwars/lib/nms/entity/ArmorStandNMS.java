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

import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.*;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.screamingsandals.bedwars.lib.nms.accessors.ArmorStandAccessor;

public class ArmorStandNMS extends EntityNMS implements ArmorStandAccessor {

	public ArmorStandNMS(Object handler) {
		super(handler);
		if (!ArmorStandAccessor.TYPE.get().isInstance(handler)) {
			throw new IllegalArgumentException("Entity must be instance of EntityArmorStand!!");
		}
	}
	
	public ArmorStandNMS(ArmorStand stand) {
		this(getHandle(stand));
	}
	
	public ArmorStandNMS(Location loc) throws Throwable {
		this(CONSTRUCTOR_0.get()
					.newInstance(getHandle(loc.getWorld()), loc.getX(), loc.getY(), loc.getZ()));
		this.setLocation(loc); // Update rotation
	}
	
	public void setSmall(boolean small) {
		getMethod(handler, METHOD_SET_SMALL.get()).invoke(small);
	}
	
	public boolean isSmall() {
		return (boolean) getMethod(handler, METHOD_IS_SMALL.get()).invoke();
	}
	
	public void setArms(boolean arms) {
		getMethod(handler, METHOD_SET_SHOW_ARMS.get()).invoke(arms);
	}
	
	public void setBasePlate(boolean basePlate) {
		getMethod(handler, METHOD_SET_NO_BASE_PLATE.get()).invoke(basePlate);
	}
	
	public void setMarker(boolean marker) {
		getMethod(handler, METHOD_SET_MARKER.get()).invoke(marker);
	}

}
