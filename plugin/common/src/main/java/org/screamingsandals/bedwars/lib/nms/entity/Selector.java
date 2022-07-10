/*
 * Copyright (C) 2022 ScreamingSandals
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
