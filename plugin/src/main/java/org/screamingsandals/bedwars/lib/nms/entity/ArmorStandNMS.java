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

package org.screamingsandals.bedwars.lib.nms.entity;

import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.*;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.screamingsandals.bedwars.lib.nms.accessors.EntityArmorStandAccessor;

public class ArmorStandNMS extends EntityNMS {

	public ArmorStandNMS(Object handler) {
		super(handler);
		if (!EntityArmorStandAccessor.getType().isInstance(handler)) {
			throw new IllegalArgumentException("Entity must be instance of EntityArmorStand!!");
		}
	}
	
	public ArmorStandNMS(ArmorStand stand) {
		this(getHandle(stand));
	}
	
	public ArmorStandNMS(Location loc) throws Throwable {
		this(EntityArmorStandAccessor.getConstructor0()
					.newInstance(getHandle(loc.getWorld()), loc.getX(), loc.getY(), loc.getZ()));
		this.setLocation(loc); // Update rotation
	}
	
	public void setSmall(boolean small) {
		getMethod(handler, EntityArmorStandAccessor.getMethodSetSmall1()).invoke(small);
	}
	
	public boolean isSmall() {
		return (boolean) getMethod(handler, EntityArmorStandAccessor.getMethodIsSmall1()).invoke();
	}
	
	public void setArms(boolean arms) {
		getMethod(handler, EntityArmorStandAccessor.getMethodSetArms1()).invoke(arms);
	}
	
	public boolean isArms() {
		return (boolean) getMethod(handler, EntityArmorStandAccessor.getMethodHasArms1()).invoke();
	}
	
	public void setBasePlate(boolean basePlate) {
		getMethod(handler, EntityArmorStandAccessor.getMethodSetBasePlate1()).invoke(basePlate);
	}
	
	public boolean isBasePlate() {
		return (boolean) getMethod(handler, EntityArmorStandAccessor.getMethodHasBasePlate1()).invoke();
	}
	
	public void setMarker(boolean marker) {
		getMethod(handler, EntityArmorStandAccessor.getMethodSetMarker1()).invoke(marker);
	}
	
	public boolean isMarker() {
		return (boolean) getMethod(handler, EntityArmorStandAccessor.getMethodIsMarker1()).invoke();
	}

}
