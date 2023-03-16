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
