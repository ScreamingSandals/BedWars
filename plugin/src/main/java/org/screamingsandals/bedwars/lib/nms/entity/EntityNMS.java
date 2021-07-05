package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;
import org.screamingsandals.bedwars.lib.nms.utils.InstanceMethod;
import org.screamingsandals.bedwars.lib.nms.utils.Version;

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
			double locX = (double) ClassStorage.getMethod(handler, "locX").invoke();
			double locY = (double) ClassStorage.getMethod(handler, "locY").invoke();
			double locZ = (double) ClassStorage.getMethod(handler, "locZ").invoke();
			float yaw, pitch;
			if (Version.isVersion(1,17)) {
				yaw = (float) ClassStorage.getMethod(handler, "getYRot").invoke();
				pitch = (float) ClassStorage.getMethod(handler, "getXRot").invoke();
			} else {
				yaw = (float) ClassStorage.getField(handler, "yaw,field_70177_z");
				pitch = (float) ClassStorage.getField(handler, "pitch,field_70125_A");
			}

			Object world = ClassStorage.getMethod(handler, "getWorld,func_130014_f_").invoke();
			World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();

			return new Location(craftWorld, locX, locY, locZ, yaw, pitch);
		} else {
			double locX = (double) ClassStorage.getField(handler, "locX,field_70165_t");
			double locY = (double) ClassStorage.getField(handler, "locY,field_70163_u");
			double locZ = (double) ClassStorage.getField(handler, "locZ,field_70161_v");
			float yaw = (float) ClassStorage.getField(handler, "yaw,field_70177_z");
			float pitch = (float) ClassStorage.getField(handler, "pitch,field_70125_A");
			Object world = ClassStorage.getMethod(handler, "getWorld,func_130014_f_").invoke();
			World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();

			return new Location(craftWorld, locX, locY, locZ, yaw, pitch);
		}
	}

	
	public void setLocation(Location location) {
		Object world = ClassStorage.getMethod(handler, "getWorld,func_130014_f_").invoke();
		World craftWorld = (World) ClassStorage.getMethod(world, "getWorld").invoke();
		if (!location.getWorld().equals(craftWorld)) {
			ClassStorage.setField(handler, "world,level,field_70170_p,c", ClassStorage.getHandle(location.getWorld()));
		}
		
		ClassStorage.getMethod(handler, "setLocation,func_70080_a", double.class, double.class, double.class, float.class, float.class)
			.invoke(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public Object getHandler() {
		return handler;
	}

	public int getId() {
		return (int) ClassStorage.getMethod(handler, "getId,func_145782_y").invoke();
	}

	public Object getDataWatcher() {
		return ClassStorage.getMethod(handler, "getDataWatcher,func_184212_Q").invoke();
	}

	public void setCustomName(String name) {
		InstanceMethod method = ClassStorage.getMethod(handler, "setCustomName,func_200203_b", ClassStorage.NMS.IChatBaseComponent);
		if (method.getReflectedMethod() != null) {
			method.invoke(ClassStorage.getMethod(ClassStorage.NMS.ChatSerializer, "a,field_150700_a", String.class)
				.invokeStatic("{\"text\": \"" + name + "\"}"));
		} else {
			ClassStorage.getMethod(handler, "setCustomName,func_96094_a", String.class).invoke(name);
		}
	}

	public String getCustomName() {
		Object textComponent = ClassStorage.getMethod(handler, "getCustomName,func_200201_e,func_95999_t").invoke();
		String text = "";
		if (ClassStorage.NMS.IChatBaseComponent.isInstance(textComponent)) {
			text = (String) ClassStorage.getMethod(textComponent, "getLegacyString,func_150254_d").invoke();
		} else {
			text = textComponent.toString();
		}
		return text;
	}

	public void setCustomNameVisible(boolean visible) {
		ClassStorage.getMethod(handler, "setCustomNameVisible,func_174805_g", boolean.class).invoke(visible);
	}

	public boolean isCustomNameVisible() {
		return (boolean) ClassStorage.getMethod(handler, "getCustomNameVisible,func_174833_aM").invoke();
	}

	public void setInvisible(boolean invisible) {
		ClassStorage.getMethod(handler, "setInvisible,func_82142_c", boolean.class).invoke(invisible);
	}

	public boolean isInvisible() {
		return (boolean) ClassStorage.getMethod(handler, "isInvisible,func_82150_aj").invoke();
	}
	
	public void setGravity(boolean gravity) {
		ClassStorage.getMethod(handler, "setNoGravity,func_189654_d", boolean.class).invoke(!gravity);
	}
	
	public boolean isGravity() {
		return !((boolean) ClassStorage.getMethod(handler, "isNoGravity,func_189652_ae").invoke());
	}
}
