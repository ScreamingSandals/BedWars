package org.screamingsandals.bedwars.lib.nms.entity;

import org.screamingsandals.bedwars.lib.nms.accessors.EntityInsentientAccessor;

public class GoalSelector extends Selector {
	
	public GoalSelector(Object handler) {
		super(handler, EntityInsentientAccessor.getFieldGoalSelector());
	}

}
