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

package org.screamingsandals.bedwars.bukkit.utils.nms;

import org.bukkit.entity.LivingEntity;
import org.screamingsandals.bedwars.nms.accessors.*;
import org.screamingsandals.bedwars.utils.EntityUtils;
import org.screamingsandals.lib.impl.bukkit.utils.nms.ClassStorage;
import org.screamingsandals.lib.utils.reflect.Reflect;

public class TargetSelector extends Selector implements EntityUtils.EntitySelector {
	
	public TargetSelector(Object handler) {
		super(handler, MobAccessor.getFieldTargetSelector());
	}

	public TargetSelector attackTarget(org.screamingsandals.lib.entity.LivingEntity target) {
		return attackTarget(target.as(LivingEntity.class));
	}

	@Override
	public EntityUtils.EntitySelector attackNearestPlayers(int order) {
		return attackNearestTarget(0, ServerPlayerAccessor.getType());
	}

	@Override
	public EntityUtils.EntitySelector attackNearestGolems(int order) {
		return attackNearestTarget(0, IronGolemAccessor.getType());
	}

	public TargetSelector attackTarget(LivingEntity target) {
		Reflect.setField(handler, MobAccessor.getFieldTarget(), target == null ? null : ClassStorage.getHandle(target));
		return this;
	}
	
	public TargetSelector attackNearestTarget(int a, Class<?> targetClass) {
		try {
			Object targetNear;
			if (NearestAttackableTargetGoalAccessor.getConstructor0() != null) {
				targetNear = Reflect.construct(NearestAttackableTargetGoalAccessor.getConstructor0(), handler, targetClass, false);
			} else {
				targetNear = Reflect.construct(NearestAttackableTargetGoalAccessor.getConstructor1(), handler, targetClass, false);
			}
			registerPathfinder(a, targetNear);
		} catch (Throwable ignored) {
		}
		return this;
	}

	public TargetSelector hurtByTarget(int a) {
		try {
			Object target;
			if (HurtByTargetGoalAccessor.getConstructor0() != null) {
				target = Reflect.construct(HurtByTargetGoalAccessor.getConstructor0(), handler, true, new Class[0]);
			} else {
				target = Reflect.construct(HurtByTargetGoalAccessor.getConstructor1(), handler, new Class[0]);
			}
			registerPathfinder(a, target);
		} catch (Throwable ignored) {
		}
		return this;
	}
	
	// And here add new targets if it's needed
}
