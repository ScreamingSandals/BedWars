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
			double locX = (double) ClassStorage.getMethod(handler, EntityAccessor.METHOD_GETX.get()).invoke();
			double locY = (double) ClassStorage.getMethod(handler, EntityAccessor.METHOD_GETY.get()).invoke();
			double locZ = (double) ClassStorage.getMethod(handler, EntityAccessor.METHOD_GETZ.get()).invoke();
			float yaw, pitch;
			if (Version.isVersion(1,17)) {
				yaw = (float) ClassStorage.getMethod(handler, EntityAccessor.METHOD_GETYROT.get()).invoke();
				pitch = (float) ClassStorage.getMethod(handler, EntityAccessor.METHOD_GETXROT.get()).invoke();
			} else {
				yaw = (float) ClassStorage.getField(handler, EntityAccessor.FIELD_YROT.get());
				pitch = (float) ClassStorage.getField(handler, EntityAccessor.FIELD_XROT.get());
			}

			Object world = ClassStorage.getMethod(handler, EntityAccessor.METHOD_GETCOMMANDSENDERWORLD.get()).invoke();
			World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();

			return new Location(craftWorld, locX, locY, locZ, yaw, pitch);
		} else {
			double locX = (double) ClassStorage.getField(handler, EntityAccessor.FIELD_X.get());
			double locY = (double) ClassStorage.getField(handler, EntityAccessor.FIELD_Y.get());
			double locZ = (double) ClassStorage.getField(handler, EntityAccessor.FIELD_Z.get());
			float yaw = (float) ClassStorage.getField(handler, EntityAccessor.FIELD_YROT.get());
			float pitch = (float) ClassStorage.getField(handler, EntityAccessor.FIELD_XROT.get());
			Object world = ClassStorage.getMethod(handler, EntityAccessor.METHOD_GETCOMMANDSENDERWORLD.get()).invoke();
			World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();

			return new Location(craftWorld, locX, locY, locZ, yaw, pitch);
		}
	}

	
	public void setLocation(Location location) {
		Object world = ClassStorage.getMethod(handler, EntityAccessor.METHOD_GETCOMMANDSENDERWORLD.get()).invoke();
		World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();
		if (!location.getWorld().equals(craftWorld)) {
			ClassStorage.setField(handler, EntityAccessor.FIELD_LEVEL.get(), ClassStorage.getHandle(location.getWorld()));
		}
		
		ClassStorage.getMethod(handler, EntityAccessor.METHOD_ABSMOVETO.get())
			.invoke(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public Object getHandler() {
		return handler;
	}

	public int getId() {
		return (int) ClassStorage.getMethod(handler, EntityAccessor.METHOD_GETID.get()).invoke();
	}

	public Object getDataWatcher() {
		return ClassStorage.getMethod(handler, EntityAccessor.METHOD_GETENTITYDATA.get()).invoke();
	}

	public void setCustomName(String name) {
		InstanceMethod method = ClassStorage.getMethod(handler, EntityAccessor.METHOD_SETCUSTOMNAME.get());
		if (method.getReflectedMethod() != null) {
			method.invoke(ClassStorage.getMethod(TabManager.getCorrectSerializingMethod())
				.invokeStatic("{\"text\": \"" + name + "\"}"));
		} else {
			ClassStorage.getMethod(handler, EntityAccessor.METHOD_SETCUSTOMNAME_1.get()).invoke(name);
		}
	}

	public String getCustomName() {
		Object textComponent = ClassStorage.getMethod(handler, EntityAccessor.METHOD_GETCUSTOMNAME.get()).invoke();
		String text = "";
		if (ComponentAccessor.TYPE.get().isInstance(textComponent)) {
			text = (String) ClassStorage.getMethod(textComponent, ComponentAccessor.METHOD_GETCOLOREDSTRING.get()).invoke();
		} else {
			text = textComponent.toString();
		}
		return text;
	}

	public void setCustomNameVisible(boolean visible) {
		ClassStorage.getMethod(handler, EntityAccessor.METHOD_SETCUSTOMNAMEVISIBLE.get()).invoke(visible);
	}

	public boolean isCustomNameVisible() {
		return (boolean) ClassStorage.getMethod(handler, EntityAccessor.METHOD_ISCUSTOMNAMEVISIBLE.get()).invoke();
	}

	public void setInvisible(boolean invisible) {
		ClassStorage.getMethod(handler, EntityAccessor.METHOD_SETINVISIBLE.get()).invoke(invisible);
	}

	public boolean isInvisible() {
		return (boolean) ClassStorage.getMethod(handler, EntityAccessor.METHOD_ISINVISIBLE.get()).invoke();
	}
	
	public void setGravity(boolean gravity) {
		ClassStorage.getMethod(handler, EntityAccessor.METHOD_SETNOGRAVITY.get()).invoke(!gravity);
	}
	
	public boolean isGravity() {
		return !((boolean) ClassStorage.getMethod(handler, EntityAccessor.METHOD_ISNOGRAVITY.get()).invoke());
	}
}
