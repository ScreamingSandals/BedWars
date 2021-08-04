package org.screamingsandals.bedwars.lib.nms.entity;

import org.screamingsandals.bedwars.nms.accessors.MobAccessor;

public class GoalSelector extends Selector {
	
	public GoalSelector(Object handler) {
		super(handler, MobAccessor.getFieldGoalSelector());
	}

}
