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
import org.screamingsandals.bedwars.lib.nms.accessors.*;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

public class EntityUtils {

	/*
	 * @return EntityLivingNMS
	 */
	public static EntityLivingNMS makeMobAttackTarget(LivingEntity mob, double speed, double follow,
		double attackDamage) {
		try {
			Object handler = ClassStorage.getHandle(mob);
			if (!EntityInsentientAccessor.getType().isInstance(handler)) {
				throw new IllegalArgumentException("Entity must be instance of EntityInsentient!!");
			}
			
			EntityLivingNMS entityLiving = new EntityLivingNMS(handler);
			
			GoalSelector selector = entityLiving.getGoalSelector();
			selector.clearSelector();
			selector.registerPathfinder(0, PathfinderGoalMeleeAttackAccessor.getConstructor0().newInstance(handler, 1.0D, false));
			
			entityLiving.setAttribute(Attribute.MOVEMENT_SPEED, speed);
			entityLiving.setAttribute(Attribute.FOLLOW_RANGE, follow);
			entityLiving.setAttribute(Attribute.ATTACK_DAMAGE, attackDamage);
			
			entityLiving.getTargetSelector().clearSelector();
			
			return entityLiving;
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
		return null;
	}

	/*
	 * includes random movement
	 * @return EntityLivingNMS
	 */
	public static EntityLivingNMS makeMobAttackTarget2(LivingEntity mob, double speed, double follow,
													  double attackDamage) {
		try {
			Object handler = ClassStorage.getHandle(mob);
			if (!EntityInsentientAccessor.getType().isInstance(handler)) {
				throw new IllegalArgumentException("Entity must be instance of EntityInsentient!!");
			}

			EntityLivingNMS entityLiving = new EntityLivingNMS(handler);

			GoalSelector selector = entityLiving.getGoalSelector();
			selector.clearSelector();
			selector.registerPathfinder(0, PathfinderGoalFloatAccessor.getConstructor0().newInstance(handler));
			selector.registerPathfinder(1, PathfinderGoalMeleeAttackAccessor.getConstructor0().newInstance(handler, 1.0D, false));
			selector.registerPathfinder(2, PathfinderGoalRandomStrollAccessor.getConstructor0().newInstance(handler, 1.0D));
			selector.registerPathfinder(3, PathfinderGoalRandomLookaroundAccessor.getConstructor0().newInstance(handler));

			entityLiving.setAttribute(Attribute.MOVEMENT_SPEED, speed);
			entityLiving.setAttribute(Attribute.FOLLOW_RANGE, follow);
			entityLiving.setAttribute(Attribute.ATTACK_DAMAGE, attackDamage);

			entityLiving.getTargetSelector().clearSelector();

			return entityLiving;
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
		return null;
	}

	public static void disableEntityAI(LivingEntity entity) {
		try {
			entity.setAI(false);
		} catch (Throwable t) {
			// this is not needed anymore, some 1.8 bullshit
			try {
				Object handler = ClassStorage.getHandle(entity);
				Object tag = ClassStorage.getMethod(handler, "getNBTTag").invoke();
				if (tag == null) {
					tag = NBTTagCompoundAccessor.getConstructor0().newInstance();
				}
				ClassStorage.getMethod(handler, "c,func_184198_c", NBTTagCompoundAccessor.getType()).invoke(tag);
				ClassStorage.getMethod(NBTTagCompoundAccessor.getMethodFunc_74768_a1()).invokeInstance(tag, "NoAI", 1);
				ClassStorage.getMethod(handler, EntityAccessor.getMethodFunc_70020_e1()).invoke(tag);
			} catch (Throwable ignored) {
			}
		}
	}
}
