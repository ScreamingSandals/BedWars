package org.screamingsandals.bedwars.lib.nms.entity;

import org.screamingsandals.bedwars.lib.nms.accessors.EntityInsentientAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PathfinderGoalSelectorAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

import java.lang.reflect.Field;

public abstract class Selector {
	protected final Object handler;
	protected final Field field;
	protected Object selector;
	
	protected Selector(Object handler, Field field) {
		if (!EntityInsentientAccessor.getType().isInstance(handler)) {
			throw new IllegalArgumentException("Invalid mob type");
		}
		this.handler = handler;
		this.field = field;
		this.selector = ClassStorage.getField(this.handler, this.field);
	}
	
	
	public void registerPathfinder(int position, Object pathfinder) {
		ClassStorage.getMethod(this.selector, PathfinderGoalSelectorAccessor.getMethodFunc_75776_a1()).invoke(position, pathfinder);
	}
	
	protected Object getNMSSelector() {
		return this.selector;
	}

	public void clearSelector() {
		try {
			this.selector = ClassStorage.setField(this.handler, this.field, ClassStorage.obtainNewPathfinderSelector(handler));
		} catch (Throwable t) {
		}
	}
}
