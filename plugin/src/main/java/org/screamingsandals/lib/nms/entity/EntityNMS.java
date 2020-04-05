package org.screamingsandals.lib.nms.entity;

import static org.screamingsandals.lib.nms.utils.ClassStorage.*;
import static org.screamingsandals.lib.nms.utils.ClassStorage.NMS.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.screamingsandals.lib.nms.utils.InstanceMethod;

public class EntityNMS {
	protected Object handler;

	public EntityNMS(Object handler) {
		this.handler = handler;
	}
	
	public EntityNMS(Entity entity) {
		this(getHandle(entity));
	}
	
	public Location getLocation() {
		double locX = (double) getField(handler, "locX,field_70165_t");
		double locY = (double) getField(handler, "locY,field_70163_u");
		double locZ = (double) getField(handler, "locZ,field_70161_v");
		float yaw = (float) getField(handler, "yaw,field_70177_z");
		float pitch = (float) getField(handler, "pitch,field_70125_A");
		Object world = getMethod(handler, "getWorld,func_130014_f_").invoke();
		World craftWorld = (World) getMethod(world, "getWorld").invoke();
		
		return new Location(craftWorld, locX, locY, locZ, yaw, pitch);
	}
	
	public void setLocation(Location location) {
		Object world = getMethod(handler, "getWorld,func_130014_f_").invoke();
		World craftWorld = (World) getMethod(world, "getWorld").invoke();
		if (!location.getWorld().equals(craftWorld)) {
			setField(handler, "world,field_70170_p", getHandle(location.getWorld()));
		}
		
		getMethod(handler, "setLocation,func_70080_a", double.class, double.class, double.class, float.class, float.class)
			.invoke(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public Object getHandler() {
		return handler;
	}

	public int getId() {
		return (int) getMethod(handler, "getId,func_145782_y").invoke();
	}

	public Object getDataWatcher() {
		return getMethod(handler, "getDataWatcher,func_184212_Q").invoke();
	}

	public void setCustomName(String name) {
		InstanceMethod method = getMethod(handler, "setCustomName,func_200203_b", IChatBaseComponent);
		if (method.getReflectedMethod() != null) {
			method.invoke(getMethod(ChatSerializer, "a,field_150700_a", String.class)
				.invokeStatic("{\"text\": \"" + name + "\"}"));
		} else {
			getMethod(handler, "setCustomName,func_96094_a", String.class).invoke(name);
		}
	}

	public String getCustomName() {
		Object textComponent = getMethod(handler, "getCustomName,func_200201_e,func_95999_t").invoke();
		String text = "";
		if (IChatBaseComponent.isInstance(textComponent)) {
			text = (String) getMethod(textComponent, "getLegacyString,func_150254_d").invoke();
		} else {
			text = textComponent.toString();
		}
		return text;
	}

	public void setCustomNameVisible(boolean visible) {
		getMethod(handler, "setCustomNameVisible,func_174805_g", boolean.class).invoke(visible);
	}

	public boolean isCustomNameVisible() {
		return (boolean) getMethod(handler, "getCustomNameVisible,func_174833_aM").invoke();
	}

	public void setInvisible(boolean invisible) {
		getMethod(handler, "setInvisible,func_82142_c", boolean.class).invoke(invisible);
	}

	public boolean isInvisible() {
		return (boolean) getMethod(handler, "isInvisible,func_82150_aj").invoke();
	}
	
	public void setGravity(boolean gravity) {
		getMethod(handler, "setNoGravity,func_189654_d", boolean.class).invoke(!gravity);
	}
	
	public boolean isGravity() {
		return !((boolean) getMethod(handler, "isNoGravity,func_189652_ae").invoke());
	}
}
