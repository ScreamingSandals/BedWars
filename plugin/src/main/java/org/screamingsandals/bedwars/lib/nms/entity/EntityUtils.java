package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.entity.LivingEntity;
import org.screamingsandals.lib.bukkit.utils.nms.ClassStorage;
import org.screamingsandals.lib.utils.reflect.Reflect;

public class EntityUtils {

	/*
	 * @return EntityLivingNMS
	 */
	public static EntityLivingNMS makeMobAttackTarget(LivingEntity mob, double speed, double follow,
		double attackDamage) {
		try {
			Object handler = ClassStorage.getHandle(mob);
			if (!ClassStorage.NMS.EntityInsentient.isInstance(handler)) {
				throw new IllegalArgumentException("Entity must be instance of EntityInsentient!!");
			}
			
			EntityLivingNMS entityLiving = new EntityLivingNMS(handler);
			
			GoalSelector selector = entityLiving.getGoalSelector();
			selector.clearSelector();
			selector.registerPathfinder(0, ClassStorage.NMS.PathfinderGoalMeleeAttack
				.getConstructor(ClassStorage.NMS.EntityCreature, double.class, boolean.class).newInstance(handler, 1.0D, false));
			
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
				Object handler = ClassStorage.getHandle(entity);
				Object tag = Reflect.getMethod(handler, "getNBTTag").invoke(); // Can this really work? or it's always creating
																		// new
																		// one?
				if (tag == null) {
					tag = ClassStorage.NMS.NBTTagCompound.getConstructor().newInstance();
				}
				Reflect.getMethod(handler, "c,func_184198_c", ClassStorage.NMS.NBTTagCompound).invoke(tag);
				Reflect.getMethod(ClassStorage.NMS.NBTTagCompound, "setInt,func_74768_a", String.class, int.class).invokeInstance(tag, "NoAI",
					1);
				Reflect.getMethod(handler, "f,func_70020_e", ClassStorage.NMS.NBTTagCompound).invoke(tag);
			} catch (Throwable ignored) {
			}
		}
	}
}
