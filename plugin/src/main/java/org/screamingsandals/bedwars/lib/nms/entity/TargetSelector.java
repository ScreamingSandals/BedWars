package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.entity.LivingEntity;
import org.screamingsandals.bedwars.lib.nms.accessors.EntityInsentientAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PathfinderGoalNearestAttackableTargetAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

public class TargetSelector extends Selector {
	
	public TargetSelector(Object handler) {
		super(handler, EntityInsentientAccessor.getFieldTargetSelector());
	}
	
	public TargetSelector attackTarget(LivingEntity target) {
		ClassStorage.setField(handler, EntityInsentientAccessor.getFieldGoalTarget(), target == null ? null : ClassStorage.getHandle(target));
		return this;
	}

	@Deprecated
	public TargetSelector attackNearestTarget(int a, String targetClass) {
		return attackNearestTarget(a, ClassStorage.safeGetClass("{nms}." + targetClass));
	}
	
	public TargetSelector attackNearestTarget(int a, Class<?> targetClass) {
		try {
			Object targetNear = PathfinderGoalNearestAttackableTargetAccessor.getConstructor0()
					.newInstance(handler, targetClass, false);
			registerPathfinder(a, targetNear);
		} catch (Throwable ignored) {
		}
		return this;
	}
	
	// And here add new targets if it's needed
}
