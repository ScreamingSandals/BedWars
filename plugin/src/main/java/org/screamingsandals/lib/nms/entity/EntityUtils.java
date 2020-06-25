package org.screamingsandals.lib.nms.entity;

import static org.screamingsandals.lib.nms.utils.ClassStorage.*;
import static org.screamingsandals.lib.nms.utils.ClassStorage.NMS.*;

import org.bukkit.entity.LivingEntity;

public class EntityUtils {

	/*
	 * @return EntityLivingNMS
	 */
	public static EntityLivingNMS makeMobAttackTarget(LivingEntity mob, double speed, double follow,
		double attackDamage) {
		try {
			Object handler = getHandle(mob);
			if (!EntityInsentient.isInstance(handler)) {
				throw new IllegalArgumentException("Entity must be instance of EntityInsentient!!");
			}
			
			EntityLivingNMS entityLiving = new EntityLivingNMS(handler);
			
			GoalSelector selector = entityLiving.getGoalSelector();
			selector.clearSelector();
			selector.registerPathfinder(0, PathfinderGoalMeleeAttack
				.getConstructor(EntityCreature, double.class, boolean.class).newInstance(handler, 1.0D, false));
			
			entityLiving.setAttribute(Attribute.MOVEMENT_SPEED, speed);
			entityLiving.setAttribute(Attribute.FOLLOW_RANGE, follow);
			entityLiving.setAttribute(Attribute.ATTACK_DAMAGE, attackDamage);
			
			entityLiving.getTargetSelector().clearSelector();
			
			return entityLiving;
		} catch (Throwable ignored) {
		}
		return null;
	}

	public static void disableEntityAI(LivingEntity entity) {
		try {
			entity.setAI(false);
		} catch (Throwable t) {
			try {
				Object handler = getHandle(entity);
				Object tag = getMethod(handler, "getNBTTag").invoke(); // Can this really work? or it's always creating
																		// new
																		// one?
				if (tag == null) {
					tag = NBTTagCompound.getConstructor().newInstance();
				}
				getMethod(handler, "c,func_184198_c", NBTTagCompound).invoke(tag);
				getMethod(NBTTagCompound, "setInt,func_74768_a", String.class, int.class).invokeInstance(tag, "NoAI",
					1);
				getMethod(handler, "f,func_70020_e", NBTTagCompound).invoke(tag);
			} catch (Throwable ignored) {
			}
		}
	}
}
