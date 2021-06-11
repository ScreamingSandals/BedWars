package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.entity.LivingEntity;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

public class TargetSelector extends Selector {
	
	public TargetSelector(Object handler) {
		super(handler, "targetSelector,field_70715_bh");
	}
	
	public TargetSelector attackTarget(LivingEntity target) {
		ClassStorage.setField(handler, "goalTarget,target,field_70696_bz", target == null ? null : ClassStorage.getHandle(target));
		return this;
	}

	@Deprecated
	public TargetSelector attackNearestTarget(int a, String targetClass) {
		return attackNearestTarget(a, ClassStorage.safeGetClass("{nms}." + targetClass));
	}
	
	public TargetSelector attackNearestTarget(int a, Class<?> targetClass) {
		try {
			Object targetNear = ClassStorage.NMS.PathfinderGoalNearestAttackableTarget.getConstructor(ClassStorage.NMS.EntityInsentient, Class.class, Boolean.TYPE)
					.newInstance(handler, targetClass, false);
			registerPathfinder(a, targetNear);
		} catch (Throwable ignored) {
		}
		return this;
	}
	
	// And here add new targets if it's needed
}
