package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.entity.LivingEntity;
import org.screamingsandals.bedwars.nms.accessors.MobAccessor;
import org.screamingsandals.bedwars.nms.accessors.NearestAttackableTargetGoalAccessor;
import org.screamingsandals.lib.bukkit.utils.nms.ClassStorage;
import org.screamingsandals.lib.utils.reflect.Reflect;

public class TargetSelector extends Selector {
	
	public TargetSelector(Object handler) {
		super(handler, MobAccessor.getFieldTargetSelector());
	}
	
	public TargetSelector attackTarget(LivingEntity target) {
		Reflect.setField(handler, MobAccessor.getFieldTarget(), target == null ? null : ClassStorage.getHandle(target));
		return this;
	}
	
	public TargetSelector attackNearestTarget(int a, String targetClass) {
		return attackNearestTarget(a, ClassStorage.safeGetClass("{nms}." + targetClass));
	}
	
	public TargetSelector attackNearestTarget(int a, Class<?> targetClass) {
		try {
			Object targetNear = Reflect.construct(NearestAttackableTargetGoalAccessor.getConstructor0(),handler, targetClass, false);
			registerPathfinder(a, targetNear);
		} catch (Throwable ignored) {
		}
		return this;
	}
	
	// And here add new targets if it's needed
}
