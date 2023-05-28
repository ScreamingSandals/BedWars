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
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.screamingsandals.bedwars.lib.nms.accessors.ComponentAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.EntityAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;
import org.screamingsandals.bedwars.lib.nms.utils.InstanceMethod;
import org.screamingsandals.bedwars.lib.nms.utils.Version;
import org.screamingsandals.bedwars.tab.TabManager;

public class EntityNMS {
	protected Object handler;

	protected EntityNMS() {}

	public EntityNMS(Object handler) {
		this.handler = handler;
	}
	
	public EntityNMS(Entity entity) {
		this(ClassStorage.getHandle(entity));
	}

	public Location getLocation() {
		if (Version.isVersion(1, 16)) {
			double locX = (double) ClassStorage.getMethod(handler, EntityAccessor.METHOD_GET_X.get()).invoke();
			double locY = (double) ClassStorage.getMethod(handler, EntityAccessor.METHOD_GET_Y.get()).invoke();
			double locZ = (double) ClassStorage.getMethod(handler, EntityAccessor.METHOD_GET_Z.get()).invoke();
			float yaw, pitch;
			if (Version.isVersion(1,17)) {
				yaw = (float) ClassStorage.getMethod(handler, EntityAccessor.METHOD_GET_Y_ROT.get()).invoke();
				pitch = (float) ClassStorage.getMethod(handler, EntityAccessor.METHOD_GET_X_ROT.get()).invoke();
			} else {
				yaw = (float) ClassStorage.getField(handler, EntityAccessor.FIELD_Y_ROT.get());
				pitch = (float) ClassStorage.getField(handler, EntityAccessor.FIELD_X_ROT.get());
			}

			Object world = ClassStorage.getMethod(handler, EntityAccessor.METHOD_GET_COMMAND_SENDER_WORLD.get()).invoke();
			World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();

			return new Location(craftWorld, locX, locY, locZ, yaw, pitch);
		} else {
			double locX = (double) ClassStorage.getField(handler, EntityAccessor.FIELD_X.get());
			double locY = (double) ClassStorage.getField(handler, EntityAccessor.FIELD_Y.get());
			double locZ = (double) ClassStorage.getField(handler, EntityAccessor.FIELD_Z.get());
			float yaw = (float) ClassStorage.getField(handler, EntityAccessor.FIELD_Y_ROT.get());
			float pitch = (float) ClassStorage.getField(handler, EntityAccessor.FIELD_X_ROT.get());
			Object world = ClassStorage.getMethod(handler, EntityAccessor.METHOD_GET_COMMAND_SENDER_WORLD.get()).invoke();
			World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();

			return new Location(craftWorld, locX, locY, locZ, yaw, pitch);
		}
	}

	
	public void setLocation(Location location) {
		Object world = ClassStorage.getMethod(handler, EntityAccessor.METHOD_GET_COMMAND_SENDER_WORLD.get()).invoke();
		World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();
		if (!location.getWorld().equals(craftWorld)) {
			ClassStorage.setField(handler, EntityAccessor.FIELD_LEVEL.get(), ClassStorage.getHandle(location.getWorld()));
		}
		
		ClassStorage.getMethod(handler, EntityAccessor.METHOD_ABS_MOVE_TO.get())
			.invoke(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public Object getHandler() {
		return handler;
	}

	public int getId() {
		return (int) ClassStorage.getMethod(handler, EntityAccessor.METHOD_GET_ID.get()).invoke();
	}

	public Object getDataWatcher() {
		return ClassStorage.getMethod(handler, EntityAccessor.METHOD_GET_ENTITY_DATA.get()).invoke();
	}

	public void setCustomName(String name) {
		InstanceMethod method = ClassStorage.getMethod(handler, EntityAccessor.METHOD_SET_CUSTOM_NAME.get());
		if (method.getReflectedMethod() != null) {
			method.invoke(ClassStorage.getMethod(TabManager.getCorrectSerializingMethod())
				.invokeStatic("{\"text\": \"" + name + "\"}"));
		} else {
			ClassStorage.getMethod(handler, EntityAccessor.METHOD_SET_CUSTOM_NAME_1.get()).invoke(name);
		}
	}

	public String getCustomName() {
		Object textComponent = ClassStorage.getMethod(handler, EntityAccessor.METHOD_GET_CUSTOM_NAME.get()).invoke();
		String text = "";
		if (ComponentAccessor.TYPE.get().isInstance(textComponent)) {
			text = (String) ClassStorage.getMethod(textComponent, ComponentAccessor.METHOD_GET_COLORED_STRING.get()).invoke();
		} else {
			text = textComponent.toString();
		}
		return text;
	}

	public void setCustomNameVisible(boolean visible) {
		ClassStorage.getMethod(handler, EntityAccessor.METHOD_SET_CUSTOM_NAME_VISIBLE.get()).invoke(visible);
	}

	public boolean isCustomNameVisible() {
		return (boolean) ClassStorage.getMethod(handler, EntityAccessor.METHOD_IS_CUSTOM_NAME_VISIBLE.get()).invoke();
	}

	public void setInvisible(boolean invisible) {
		ClassStorage.getMethod(handler, EntityAccessor.METHOD_SET_INVISIBLE.get()).invoke(invisible);
	}

	public boolean isInvisible() {
		return (boolean) ClassStorage.getMethod(handler, EntityAccessor.METHOD_IS_INVISIBLE.get()).invoke();
	}
	
	public void setGravity(boolean gravity) {
		ClassStorage.getMethod(handler, EntityAccessor.METHOD_SET_NO_GRAVITY.get()).invoke(!gravity);
	}
	
	public boolean isGravity() {
		return !((boolean) ClassStorage.getMethod(handler, EntityAccessor.METHOD_IS_NO_GRAVITY.get()).invoke());
	}
}
