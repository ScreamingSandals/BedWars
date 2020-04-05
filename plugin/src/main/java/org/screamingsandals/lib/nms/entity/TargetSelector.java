package org.screamingsandals.lib.nms.entity;

import org.bukkit.entity.LivingEntity;

import static org.screamingsandals.lib.nms.utils.ClassStorage.NMS.*;
import static org.screamingsandals.lib.nms.utils.ClassStorage.*;

public class TargetSelector extends Selector {
	
	public TargetSelector(Object handler) {
		super(handler, "targetSelector,field_70715_bh");
	}
	
	public TargetSelector attackTarget(LivingEntity target) {
		setField(handler, "goalTarget,field_70696_bz", target == null ? null : getHandle(target));
		return this;
	}
	
	public TargetSelector attackNearestTarget(int a, String targetClass) {
		return attackNearestTarget(a, safeGetClass("{nms}." + targetClass));
	}
	
	public TargetSelector attackNearestTarget(int a, Class<?> targetClass) {
		try {
			Object targetNear = PathfinderGoalNearestAttackableTarget.getConstructor(EntityInsentient, Class.class, Boolean.TYPE)
					.newInstance(handler, targetClass, false);
			registerPathfinder(a, targetNear);
		} catch (Throwable ignored) {
		}
		return this;
	}
	
	// And here add new targets if it's needed
}
