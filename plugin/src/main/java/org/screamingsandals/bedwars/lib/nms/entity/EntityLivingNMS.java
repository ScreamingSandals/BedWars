package org.screamingsandals.bedwars.lib.nms.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.bukkit.entity.LivingEntity;
import org.screamingsandals.lib.bukkit.utils.nms.ClassStorage;
import org.screamingsandals.lib.bukkit.utils.nms.entity.EntityNMS;
import org.screamingsandals.lib.utils.reflect.Reflect;

import java.util.Map;
import java.util.function.Consumer;

public class EntityLivingNMS extends EntityNMS {

	public EntityLivingNMS(Object handler) {
		super(handler);
		if (!ClassStorage.NMS.EntityInsentient.isInstance(handler)) {
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
		return hasAttribute(attribute.getKeys());
	}
	
	public boolean hasAttribute(String keys) {
		try {
			Object attr = Reflect.getField(ClassStorage.NMS.GenericAttributes, keys);
			Object attr0 = Reflect.getMethod(handler, "getAttributeInstance,func_110148_a", ClassStorage.NMS.IAttribute)
				.invoke(attr);
			return attr0 != null;
		} catch (Throwable t) {
		}
		return false;
	}
	
	public double getAttribute(Attribute attribute) {
		return getAttribute(attribute.getKeys());
	}
	
	public double getAttribute(String keys) {
		try {
			Object attr = Reflect.getField(ClassStorage.NMS.GenericAttributes, keys);
			Object attr0 = Reflect.getMethod(handler, "getAttributeInstance,func_110148_a", ClassStorage.NMS.IAttribute)
				.invoke(attr);
			return (double) Reflect.getMethod(attr0, "getValue,func_111126_e").invoke();
		} catch (Throwable t) {
		}
		return 0;
	}
	
	public void setAttribute(Attribute attribute, double value) {
		setAttribute(attribute.getKeys(), value);
	}
	
	public void setAttribute(String keys, double value) {
		try {
			if (value >= 0) {
				Object attr = Reflect.getField(ClassStorage.NMS.GenericAttributes, keys);
				Object attr0 = Reflect.getMethod(handler, "getAttributeInstance,func_110148_a", ClassStorage.NMS.IAttribute)
					.invoke(attr);
				if (attr0 == null) {
					Object attrMap = Reflect.getMethod(handler, "getAttributeMap,func_110140_aT").invoke();
					// Pre 1.16
					attr0 = Reflect.getMethod(attrMap, "b,func_111150_b", ClassStorage.NMS.IAttribute).invoke(attr);
					if (attr0 instanceof Boolean) {
						// 1.16
						Object provider = Reflect.getField(attrMap,"d,field_233777_d_");
						Map<Object, Object> all = Maps
								.newHashMap((Map<?, ?>) Reflect.getField(provider, "a,field_233802_a_"));
						attr0 = ClassStorage.NMS.AttributeModifiable.getConstructor(ClassStorage.NMS.IAttribute, Consumer.class).newInstance(attr, (Consumer) o -> {
							// do nothing
						});
						all.put(attr, attr0);
						Reflect.setField(provider, "a,field_233802_a_", ImmutableMap.copyOf(all));
					}
				}
				Reflect.getMethod(attr0, "setValue,func_111128_a", double.class).invoke(value);
			}
		} catch (Throwable ignore) {
		}
	}
}
