package org.screamingsandals.bedwars.lib.nms.entity;

import org.screamingsandals.bedwars.nms.accessors.GoalSelectorAccessor;
import org.screamingsandals.bedwars.nms.accessors.MobAccessor;
import org.screamingsandals.lib.bukkit.utils.nms.ClassStorage;
import org.screamingsandals.lib.utils.reflect.Reflect;

import java.lang.reflect.Field;

public abstract class Selector {
	protected final Object handler;
	protected final Field field;
	protected Object selector;
	
	protected Selector(Object handler, Field field) {
		if (!MobAccessor.getType().isInstance(handler)) {
			throw new IllegalArgumentException("Invalid mob type");
		}
		this.handler = handler;
		this.field = field;
		this.selector = Reflect.getField(this.handler, this.field);
	}
	
	
	public void registerPathfinder(int position, Object pathfinder) {
		Reflect.fastInvoke(this.selector, GoalSelectorAccessor.getMethodAddGoal1(), position, pathfinder);
	}
	
	protected Object getNMSSelector() {
		return this.selector;
	}

	public void clearSelector() {
		try {
			this.selector = Reflect.setField(this.handler, this.field, ClassStorage.obtainNewPathfinderSelector(handler));
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
