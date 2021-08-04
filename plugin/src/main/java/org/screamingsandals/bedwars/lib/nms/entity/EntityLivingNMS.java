package org.screamingsandals.bedwars.lib.nms.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.bukkit.entity.LivingEntity;
import org.screamingsandals.bedwars.nms.accessors.*;
import org.screamingsandals.lib.bukkit.utils.nms.ClassStorage;
import org.screamingsandals.lib.bukkit.utils.nms.entity.EntityNMS;
import org.screamingsandals.lib.utils.reflect.Reflect;

import java.util.Map;
import java.util.function.Consumer;

public class EntityLivingNMS extends EntityNMS {

	public EntityLivingNMS(Object handler) {
		super(handler);
		if (!MobAccessor.getType().isInstance(handler)) {
			throw new IllegalArgumentException("Entity must be instance of EntityInsentient!!");
		}
	}

	public EntityLivingNMS(LivingEntity entity) {
		this(ClassStorage.getHandle(entity));
	}

	public TargetSelector getTargetSelector() {
		return new TargetSelector(handler);
	}

	public GoalSelector getGoalSelector() {
		return new GoalSelector(handler);
	}
	
	public boolean hasAttribute(Attribute attribute) {
		return hasAttribute(attribute.getObject().get());
	}
	
	public boolean hasAttribute(Object attr) {
		try {
			Object attr0 = Reflect.fastInvoke(handler, LivingEntityAccessor.getMethodGetAttribute1(), attr);
			return attr0 != null;
		} catch (Throwable t) {
		}
		return false;
	}
	
	public double getAttribute(Attribute attribute) {
		return getAttribute(attribute.getObject().get());
	}
	
	public double getAttribute(Object attr) {
		try {
			Object attr0 = Reflect.fastInvoke(handler,LivingEntityAccessor.getMethodGetAttribute1(), attr);
			return (double) Reflect.fastInvoke(attr0, AttributeInstanceAccessor.getMethodGetValue1());
		} catch (Throwable t) {
		}
		return 0;
	}
	
	public void setAttribute(Attribute attribute, double value) {
		setAttribute(attribute.getObject().get(), value);
	}
	
	public void setAttribute(Object attr, double value) {
		try {
			if (value >= 0) {
				Object attr0 = Reflect.fastInvoke(handler, LivingEntityAccessor.getMethodGetAttribute1(), attr);
				if (attr0 == null) {
					Object attrMap = Reflect.getMethod(handler, LivingEntityAccessor.getMethodGetAttributes1()).invoke();
					// Pre 1.16
					attr0 = Reflect.fastInvoke(attrMap, AttributeMapAccessor.getMethodRegisterAttribute1(), attr);
					if (attr0 == null || !AttributeInstanceAccessor.getType().isInstance(attr0)) {
						// 1.16
						Object provider = Reflect.getField(attrMap, AttributeMapAccessor.getFieldSupplier());
						Map<Object, Object> all = Maps
								.newHashMap((Map<?, ?>) Reflect.getField(provider, AttributeSupplierAccessor.getFieldInstances()));
						attr0 = Reflect.construct(AttributeInstanceAccessor.getConstructor0(), attr, (Consumer) o -> {
							// do nothing
						});
						all.put(attr, attr0);
						Reflect.setField(provider, AttributeSupplierAccessor.getFieldInstances(), ImmutableMap.copyOf(all));
					}
				}
				Reflect.fastInvoke(attr0, AttributeInstanceAccessor.getMethodSetBaseValue1(), value);
			}
		} catch (Throwable ignore) {
		}
	}
}
