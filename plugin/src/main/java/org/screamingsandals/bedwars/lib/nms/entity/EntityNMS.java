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

import java.util.UUID;

public class EntityNMS implements EntityAccessor {
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
			double locX = (double) ClassStorage.getMethod(handler, METHOD_GET_X.get()).invoke();
			double locY = (double) ClassStorage.getMethod(handler, METHOD_GET_Y.get()).invoke();
			double locZ = (double) ClassStorage.getMethod(handler, METHOD_GET_Z.get()).invoke();
			float yaw, pitch;
			if (Version.isVersion(1,17)) {
				yaw = (float) ClassStorage.getMethod(handler, METHOD_GET_YROT.get()).invoke();
				pitch = (float) ClassStorage.getMethod(handler, METHOD_GET_XROT.get()).invoke();
			} else {
				yaw = (float) ClassStorage.getField(handler, FIELD_Y_ROT.get());
				pitch = (float) ClassStorage.getField(handler, FIELD_X_ROT.get());
			}

			Object world = ClassStorage.getMethod(handler, METHOD_GET_COMMAND_SENDER_WORLD.get()).invoke();
			World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();

			return new Location(craftWorld, locX, locY, locZ, yaw, pitch);
		} else {
			double locX = (double) ClassStorage.getField(handler, FIELD_X.get());
			double locY = (double) ClassStorage.getField(handler, FIELD_Y.get());
			double locZ = (double) ClassStorage.getField(handler, FIELD_Z.get());
			float yaw = (float) ClassStorage.getField(handler, FIELD_Y_ROT.get());
			float pitch = (float) ClassStorage.getField(handler, FIELD_X_ROT.get());
			Object world = ClassStorage.getMethod(handler, METHOD_GET_COMMAND_SENDER_WORLD.get()).invoke();
			World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();

			return new Location(craftWorld, locX, locY, locZ, yaw, pitch);
		}
	}

	
	public void setLocation(Location location) {
		Object world = ClassStorage.getMethod(handler, METHOD_GET_COMMAND_SENDER_WORLD.get()).invoke();
		World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();
		if (!location.getWorld().equals(craftWorld)) {
			ClassStorage.setField(handler, FIELD_LEVEL.get(), ClassStorage.getHandle(location.getWorld()));
		}
		
		ClassStorage.getMethod(handler, METHOD_ABS_MOVE_TO.get())
			.invoke(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public Object getHandler() {
		return handler;
	}

	public int getId() {
		return (int) ClassStorage.getMethod(handler, METHOD_GET_ID.get()).invoke();
	}

	public UUID getUUID() {
		return (UUID) ClassStorage.getMethod(handler, METHOD_GET_UUID.get()).invoke();
	}

	/** 1.21 */
	public double getX() {
		return (double) ClassStorage.getMethod(handler, METHOD_GET_X.get()).invoke();
	}

	/** 1.21 */
	public double getY() {
		return (double) ClassStorage.getMethod(handler, METHOD_GET_Y.get()).invoke();
	}

	/** 1.21 */
	public double getZ() {
		return (double) ClassStorage.getMethod(handler, METHOD_GET_Z.get()).invoke();
	}

	/** 1.21 */
	public float getXRot() {
		return (float) ClassStorage.getMethod(handler, METHOD_GET_XROT.get()).invoke();
	}

	/** 1.21 */
	public float getYRot() {
		return (float) ClassStorage.getMethod(handler, METHOD_GET_YROT.get()).invoke();
	}

	/** 1.21 */
	public float getYHeadRot() {
		return (float) ClassStorage.getMethod(handler, METHOD_GET_YHEAD_ROT.get()).invoke();
	}

	public Object getType() {
		return ClassStorage.getMethod(handler, METHOD_GET_TYPE.get()).invoke();
	}

	/** 1.21 */
	public Object getDelta() {
		return ClassStorage.getMethod(handler, METHOD_GET_DELTA_MOVEMENT.get()).invoke();
	}

	public Object getDataWatcher() {
		return ClassStorage.getMethod(handler, METHOD_GET_ENTITY_DATA.get()).invoke();
	}

	public void setCustomName(String name) {
		InstanceMethod method = ClassStorage.getMethod(handler, METHOD_SET_CUSTOM_NAME.get());
		if (method.getReflectedMethod() != null) {
			method.invoke(TabManager.serialize("{\"text\": \"" + name + "\"}"));
		} else {
			ClassStorage.getMethod(handler, METHOD_SET_CUSTOM_NAME_1.get()).invoke(name);
		}
	}

	public String getCustomName() {
		Object textComponent = ClassStorage.getMethod(handler, METHOD_GET_CUSTOM_NAME.get()).invoke();
		String text = "";
		if (ComponentAccessor.TYPE.get().isInstance(textComponent)) {
			text = (String) ClassStorage.getMethod(textComponent, ComponentAccessor.METHOD_GET_COLORED_STRING.get()).invoke();
		} else {
			text = textComponent.toString();
		}
		return text;
	}

	public void setCustomNameVisible(boolean visible) {
		ClassStorage.getMethod(handler, METHOD_SET_CUSTOM_NAME_VISIBLE.get()).invoke(visible);
	}

	public boolean isCustomNameVisible() {
		return (boolean) ClassStorage.getMethod(handler, METHOD_IS_CUSTOM_NAME_VISIBLE.get()).invoke();
	}

	public void setInvisible(boolean invisible) {
		ClassStorage.getMethod(handler, METHOD_SET_INVISIBLE.get()).invoke(invisible);
	}

	public boolean isInvisible() {
		return (boolean) ClassStorage.getMethod(handler, METHOD_IS_INVISIBLE.get()).invoke();
	}
	
	public void setGravity(boolean gravity) {
		ClassStorage.getMethod(handler, METHOD_SET_NO_GRAVITY.get()).invoke(!gravity);
	}
	
	public boolean isGravity() {
		return !((boolean) ClassStorage.getMethod(handler, METHOD_IS_NO_GRAVITY.get()).invoke());
	}
}
