package org.screamingsandals.bedwars.lib.nms.entity;

import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.bukkit.entity.LivingEntity;
import org.screamingsandals.bedwars.lib.nms.accessors.*;

import java.util.Map;
import java.util.function.Consumer;

public class EntityLivingNMS extends EntityNMS {

	public EntityLivingNMS(Object handler) {
		super(handler);
		if (!EntityInsentientAccessor.getType().isInstance(handler)) {
			throw new IllegalArgumentException("Entity must be instance of EntityInsentient!!");
		}
	}

	public EntityLivingNMS(LivingEntity entity) {
		this(getHandle(entity));
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
			Object attr0 = getMethod(handler, EntityLivingAccessor.getMethodGetAttributeInstance1())
				.invoke(attr);
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
			Object attr0 = getMethod(handler, EntityLivingAccessor.getMethodGetAttributeInstance1())
				.invoke(attr);
			return (double) getMethod(attr0, AttributeModifiableAccessor.getMethodGetValue1()).invoke();
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
				Object attr0 = getMethod(handler, EntityLivingAccessor.getMethodGetAttributeInstance1())
					.invoke(attr);
				if (attr0 == null) {
					Object attrMap = getMethod(handler, EntityLivingAccessor.getMethodGetAttributeMap1()).invoke();
					// Pre 1.16
					attr0 = getMethod(attrMap, AttributeMapBaseAccessor.getMethodFunc_111150_b1()).invoke(attr);
					if (attr0 == null || !AttributeModifiableAccessor.getType().isInstance(attr0)) {
						// 1.16
						Object provider = getField(attrMap,AttributeMapBaseAccessor.getFieldField_233777_d_());
						Map<Object, Object> all = Maps
								.newHashMap((Map<?, ?>) getField(provider, AttributeProviderAccessor.getFieldField_233802_a_()));
						attr0 = AttributeModifiableAccessor.getConstructor0().newInstance(attr, (Consumer) o -> {
							// do nothing
						});
						all.put(attr, attr0);
						setField(provider, AttributeProviderAccessor.getFieldField_233802_a_(), ImmutableMap.copyOf(all));
					}
				}
				getMethod(attr0, AttributeModifiableAccessor.getMethodSetValue1()).invoke(value);
			}
		} catch (Throwable ignore) {
		}
	}
}
