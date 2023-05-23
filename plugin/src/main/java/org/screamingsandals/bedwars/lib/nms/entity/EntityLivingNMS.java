/*
 * Copyright (C) 2023 ScreamingSandals
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
		if (!MobAccessor.TYPE.get().isInstance(handler)) {
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
			Object attr0 = getMethod(handler, LivingEntityAccessor.METHOD_GETATTRIBUTE.get())
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
			Object attr0 = getMethod(handler, LivingEntityAccessor.METHOD_GETATTRIBUTE.get())
				.invoke(attr);
			return (double) getMethod(attr0, AttributeInstanceAccessor.METHOD_GETBASEVALUE.get()).invoke();
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
				Object attr0 = getMethod(handler, LivingEntityAccessor.METHOD_GETATTRIBUTE.get())
					.invoke(attr);
				if (attr0 == null) {
					Object attrMap = getMethod(handler, LivingEntityAccessor.METHOD_GETATTRIBUTES.get()).invoke();
					// Pre 1.16
					attr0 = getMethod(attrMap, AttributeMapAccessor.METHOD_REGISTERATTRIBUTE.get()).invoke(attr);
					if (attr0 == null || !AttributeInstanceAccessor.TYPE.get().isInstance(attr0)) {
						// 1.16
						Object provider = getField(attrMap, AttributeMapAccessor.FIELD_SUPPLIER.get());
						Map<Object, Object> all = Maps
								.newHashMap((Map<?, ?>) getField(provider, AttributeSupplierAccessor.FIELD_INSTANCES.get()));
						attr0 = AttributeInstanceAccessor.CONSTRUCTOR_0.get().newInstance(attr, (Consumer) o -> {
							// do nothing
						});
						all.put(attr, attr0);
						setField(provider, AttributeSupplierAccessor.FIELD_INSTANCES.get(), ImmutableMap.copyOf(all));
					}
				}
				getMethod(attr0, AttributeInstanceAccessor.METHOD_SETBASEVALUE.get()).invoke(value);
			}
		} catch (Throwable ignore) {
		}
	}
}
