package org.screamingsandals.lib.nms.entity;

import static org.screamingsandals.lib.nms.utils.ClassStorage.*;
import static org.screamingsandals.lib.nms.utils.ClassStorage.NMS.*;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class ArmorStandNMS extends EntityNMS {

	public ArmorStandNMS(Object handler) {
		super(handler);
		if (!EntityArmorStand.isInstance(handler)) {
			throw new IllegalArgumentException("Entity must be instance of EntityArmorStand!!");
		}
	}
	
	public ArmorStandNMS(ArmorStand stand) {
		this(getHandle(stand));
	}
	
	public ArmorStandNMS(Location loc) throws Throwable {
		this(EntityArmorStand.getConstructor(World, double.class, double.class, double.class)
					.newInstance(getMethod(loc.getWorld(), "getHandle").invoke(), loc.getX(), loc.getY(), loc.getZ()));
		this.setLocation(loc); // Update rotation
	}
	
	public void setSmall(boolean small) {
		getMethod(handler, "setSmall,func_175420_a", boolean.class).invoke(small);
	}
	
	public boolean isSmall() {
		return (boolean) getMethod(handler, "isSmall,func_175410_n").invoke();
	}
	
	public void setArms(boolean arms) {
		getMethod(handler, "setArms,func_175413_k", boolean.class).invoke(arms);
	}
	
	public boolean isArms() {
		return (boolean) getMethod(handler, "hasArms,func_175402_q").invoke();
	}
	
	public void setBasePlate(boolean basePlate) {
		getMethod(handler, "setBasePlate,func_175426_l", boolean.class).invoke(basePlate);
	}
	
	public boolean isBasePlate() {
		return (boolean) getMethod(handler, "hasBasePlate,func_175414_r").invoke();
	}
	
	public void setMarker(boolean marker) {
		getMethod(handler, "setMarker,func_181027_m,n", boolean.class).invoke(marker);
	}
	
	public boolean isMarker() {
		return (boolean) getMethod(handler, "isMarker,func_175426_l,s").invoke();
	}

}
