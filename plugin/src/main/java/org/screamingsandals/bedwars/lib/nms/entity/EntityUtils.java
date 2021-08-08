package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.entity.LivingEntity;
import org.screamingsandals.bedwars.nms.accessors.MeleeAttackGoalAccessor;
import org.screamingsandals.bedwars.nms.accessors.MobAccessor;
import org.screamingsandals.lib.bukkit.utils.nms.ClassStorage;
import org.screamingsandals.lib.entity.EntityLiving;
import org.screamingsandals.lib.utils.reflect.Reflect;

public class EntityUtils {

	/*
	 * @return EntityLivingNMS
	 */
	public static EntityLivingNMS makeMobAttackTarget(EntityLiving mob, double speed, double follow, double attackDamage) {
		try {
			var handler = ClassStorage.getHandle(mob.as(LivingEntity.class));
			if (!MobAccessor.getType().isInstance(handler)) {
				throw new IllegalArgumentException("Entity must be instance of EntityInsentient!!");
			}
			
			var entityLiving = new EntityLivingNMS(handler);
			
			var selector = entityLiving.getGoalSelector();
			selector.clearSelector();
			selector.registerPathfinder(0, Reflect.construct(MeleeAttackGoalAccessor.getConstructor0(), handler, 1.0D, false));
			
			entityLiving.setAttribute(Attribute.MOVEMENT_SPEED, speed);
			entityLiving.setAttribute(Attribute.FOLLOW_RANGE, follow);
			entityLiving.setAttribute(Attribute.ATTACK_DAMAGE, attackDamage);
			
			entityLiving.getTargetSelector().clearSelector();
			
			return entityLiving;
		} catch (Throwable ignored) {
		}
		return null;
	}
}
