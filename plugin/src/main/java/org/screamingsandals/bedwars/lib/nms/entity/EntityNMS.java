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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.screamingsandals.bedwars.lib.nms.accessors.EntityAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.IChatBaseComponentAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.IChatBaseComponent_i_ChatSerializerAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;
import org.screamingsandals.bedwars.lib.nms.utils.InstanceMethod;
import org.screamingsandals.bedwars.lib.nms.utils.Version;
import org.screamingsandals.bedwars.tab.TabManager;

public class EntityNMS {
	protected Object handler;

	public EntityNMS(Object handler) {
		this.handler = handler;
	}
	
	public EntityNMS(Entity entity) {
		this(ClassStorage.getHandle(entity));
	}

	public Location getLocation() {
		if (Version.isVersion(1, 16)) {
			double locX = (double) ClassStorage.getMethod(handler, EntityAccessor.getMethodLocX1()).invoke();
			double locY = (double) ClassStorage.getMethod(handler, EntityAccessor.getMethodLocY1()).invoke();
			double locZ = (double) ClassStorage.getMethod(handler, EntityAccessor.getMethodLocZ1()).invoke();
			float yaw, pitch;
			if (Version.isVersion(1,17)) {
				yaw = (float) ClassStorage.getMethod(handler, EntityAccessor.getMethodGetYRot1()).invoke();
				pitch = (float) ClassStorage.getMethod(handler, EntityAccessor.getMethodGetXRot1()).invoke();
			} else {
				yaw = (float) ClassStorage.getField(handler, EntityAccessor.getFieldYaw());
				pitch = (float) ClassStorage.getField(handler, EntityAccessor.getFieldPitch());
			}

			Object world = ClassStorage.getMethod(handler, EntityAccessor.getMethodGetWorld1()).invoke();
			World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();

			return new Location(craftWorld, locX, locY, locZ, yaw, pitch);
		} else {
			double locX = (double) ClassStorage.getField(handler, EntityAccessor.getFieldLocX());
			double locY = (double) ClassStorage.getField(handler, EntityAccessor.getFieldLocY());
			double locZ = (double) ClassStorage.getField(handler, EntityAccessor.getFieldLocZ());
			float yaw = (float) ClassStorage.getField(handler, EntityAccessor.getFieldYaw());
			float pitch = (float) ClassStorage.getField(handler, EntityAccessor.getFieldPitch());
			Object world = ClassStorage.getMethod(handler, EntityAccessor.getMethodGetWorld1()).invoke();
			World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();

			return new Location(craftWorld, locX, locY, locZ, yaw, pitch);
		}
	}

	
	public void setLocation(Location location) {
		Object world = ClassStorage.getMethod(handler, EntityAccessor.getMethodGetWorld1()).invoke();
		World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();
		if (!location.getWorld().equals(craftWorld)) {
			ClassStorage.setField(handler, EntityAccessor.getFieldWorld(), ClassStorage.getHandle(location.getWorld()));
		}
		
		ClassStorage.getMethod(handler, EntityAccessor.getMethodSetLocation1())
			.invoke(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public Object getHandler() {
		return handler;
	}

	public int getId() {
		return (int) ClassStorage.getMethod(handler, EntityAccessor.getMethodGetId1()).invoke();
	}

	public Object getDataWatcher() {
		return ClassStorage.getMethod(handler, EntityAccessor.getMethodGetDataWatcher1()).invoke();
	}

	public void setCustomName(String name) {
		InstanceMethod method = ClassStorage.getMethod(handler, EntityAccessor.getMethodSetCustomName1());
		if (method.getReflectedMethod() != null) {
			method.invoke(ClassStorage.getMethod(TabManager.getCorrectSerializingMethod())
				.invokeStatic("{\"text\": \"" + name + "\"}"));
		} else {
			ClassStorage.getMethod(handler, EntityAccessor.getMethodSetCustomName2()).invoke(name);
		}
	}

	public String getCustomName() {
		Object textComponent = ClassStorage.getMethod(handler, EntityAccessor.getMethodGetCustomName1()).invoke();
		String text = "";
		if (IChatBaseComponentAccessor.getType().isInstance(textComponent)) {
			text = (String) ClassStorage.getMethod(textComponent, IChatBaseComponentAccessor.getMethodGetLegacyString1()).invoke();
		} else {
			text = textComponent.toString();
		}
		return text;
	}

	public void setCustomNameVisible(boolean visible) {
		ClassStorage.getMethod(handler, EntityAccessor.getMethodSetCustomNameVisible1()).invoke(visible);
	}

	public boolean isCustomNameVisible() {
		return (boolean) ClassStorage.getMethod(handler, EntityAccessor.getMethodGetCustomNameVisible1()).invoke();
	}

	public void setInvisible(boolean invisible) {
		ClassStorage.getMethod(handler, EntityAccessor.getMethodSetInvisible1()).invoke(invisible);
	}

	public boolean isInvisible() {
		return (boolean) ClassStorage.getMethod(handler, EntityAccessor.getMethodIsInvisible1()).invoke();
	}
	
	public void setGravity(boolean gravity) {
		ClassStorage.getMethod(handler, EntityAccessor.getMethodSetNoGravity1()).invoke(!gravity);
	}
	
	public boolean isGravity() {
		return !((boolean) ClassStorage.getMethod(handler, EntityAccessor.getMethodIsNoGravity1()).invoke());
	}
}
