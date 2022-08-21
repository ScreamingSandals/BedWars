/*
 * Copyright (C) 2022 ScreamingSandals
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

import org.bukkit.entity.LivingEntity;
import org.screamingsandals.bedwars.lib.nms.accessors.EntityInsentientAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PathfinderGoalHurtByTargetAccessor;
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
			Object targetNear;
			if (PathfinderGoalNearestAttackableTargetAccessor.getConstructor0() != null) {
				targetNear = PathfinderGoalNearestAttackableTargetAccessor.getConstructor0()
						.newInstance(handler, targetClass, false);
			} else {
				targetNear = PathfinderGoalNearestAttackableTargetAccessor.getConstructor1()
						.newInstance(handler, targetClass, false);
			}
			registerPathfinder(a, targetNear);
		} catch (Throwable ignored) {
		}
		return this;
	}

	public TargetSelector hurtByTarget(int a) {
		try {
			Object target;
			if (PathfinderGoalHurtByTargetAccessor.getConstructor0() != null) {
				target = PathfinderGoalHurtByTargetAccessor.getConstructor0()
						.newInstance(handler, true, new Class[0]);
			} else {
				target = PathfinderGoalHurtByTargetAccessor.getConstructor1()
						.newInstance(handler, new Class[0]);
			}
			registerPathfinder(a, target);
		} catch (Throwable ignored) {
		}
		return this;
	}
	
	// And here add new targets if it's needed
}
