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

import lombok.Data;
import org.screamingsandals.bedwars.nms.accessors.AttributesAccessor;
import org.screamingsandals.lib.utils.reflect.Reflect;

import java.util.function.Supplier;

@Data
public class Attribute {
	public static final Attribute MAX_HEALTH = new Attribute(() -> Reflect.getField(AttributesAccessor.getFieldMAX_HEALTH()));
	public static final Attribute FOLLOW_RANGE = new Attribute(() -> Reflect.getField(AttributesAccessor.getFieldFOLLOW_RANGE()));
	public static final Attribute KNOCKBACK_RESISTANCE = new Attribute(() -> Reflect.getField(AttributesAccessor.getFieldKNOCKBACK_RESISTANCE()));
	public static final Attribute MOVEMENT_SPEED = new Attribute(() -> Reflect.getField(AttributesAccessor.getFieldMOVEMENT_SPEED()));
	public static final Attribute FLYING_SPEED = new Attribute(() -> Reflect.getField(AttributesAccessor.getFieldFLYING_SPEED()));
	public static final Attribute ATTACK_DAMAGE = new Attribute(() -> Reflect.getField(AttributesAccessor.getFieldATTACK_DAMAGE()));
	public static final Attribute ATTACK_KNOCKBACK = new Attribute(() -> Reflect.getField(AttributesAccessor.getFieldATTACK_KNOCKBACK()));
	public static final Attribute ATTACK_SPEED = new Attribute(() -> Reflect.getField(AttributesAccessor.getFieldATTACK_SPEED()));
	public static final Attribute ARMOR = new Attribute(() -> Reflect.getField(AttributesAccessor.getFieldARMOR()));
	public static final Attribute ARMOR_TOUGHNESS = new Attribute(() -> Reflect.getField(AttributesAccessor.getFieldARMOR_TOUGHNESS()));
	public static final Attribute LUCK = new Attribute(() -> Reflect.getField(AttributesAccessor.getFieldLUCK()));

	private final Supplier<Object> object;
}
