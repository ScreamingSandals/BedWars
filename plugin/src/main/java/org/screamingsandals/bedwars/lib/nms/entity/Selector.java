package org.screamingsandals.bedwars.lib.nms.entity;

import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

public abstract class Selector {
	protected final Object handler;
	protected final String keys;
	protected Object selector;
	
	protected Selector(Object handler, String keys) {
		if (!ClassStorage.NMS.EntityInsentient.isInstance(handler)) {
			throw new IllegalArgumentException("Invalid mob type");
		}
		this.handler = handler;
		this.keys = keys;
		this.selector = ClassStorage.getField(this.handler, this.keys);
	}
	
	
	public void registerPathfinder(int position, Object pathfinder) {
		ClassStorage.getMethod(this.selector, "a,func_75776_a", Integer.TYPE, ClassStorage.NMS.PathfinderGoal).invoke(position, pathfinder);
	}
	
	protected Object getNMSSelector() {
		return this.selector;
	}

	public void clearSelector() {
		try {
			this.selector = ClassStorage.setField(this.handler, this.keys, ClassStorage.obtainNewPathfinderSelector(handler));
		} catch (Throwable t) {
		}
	}
}
