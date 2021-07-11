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
