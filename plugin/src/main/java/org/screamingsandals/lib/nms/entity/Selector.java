package org.screamingsandals.lib.nms.entity;

import static org.screamingsandals.lib.nms.utils.ClassStorage.*;
import static org.screamingsandals.lib.nms.utils.ClassStorage.NMS.*;

public abstract class Selector {
	protected final Object handler;
	protected final String keys;
	protected Object selector;
	
	protected Selector(Object handler, String keys) {
		if (!EntityInsentient.isInstance(handler)) {
			throw new IllegalArgumentException("Invalid mob type");
		}
		this.handler = handler;
		this.keys = keys;
		this.selector = getField(this.handler, this.keys);
	}
	
	
	public void registerPathfinder(int position, Object pathfinder) {
		getMethod(this.selector, "a,func_75776_a", Integer.TYPE, PathfinderGoal).invoke(position, pathfinder);
	}
	
	protected Object getNMSSelector() {
		return this.selector;
	}

	public void clearSelector() {
		try {
			this.selector = setField(this.handler, this.keys, obtainNewPathfinderSelector(handler));
		} catch (Throwable t) {
		}
	}
}
