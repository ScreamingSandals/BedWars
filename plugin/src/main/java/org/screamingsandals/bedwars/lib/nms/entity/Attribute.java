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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.lib.nms.accessors.AttributesAccessor;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class Attribute {
	public static final Attribute MAX_HEALTH = new Attribute(AttributesAccessor.FIELD_MAX_HEALTH::get);
	public static final Attribute FOLLOW_RANGE = new Attribute(AttributesAccessor.FIELD_FOLLOW_RANGE::get);
	public static final Attribute KNOCKBACK_RESISTANCE = new Attribute(AttributesAccessor.FIELD_KNOCKBACK_RESISTANCE::get);
	public static final Attribute MOVEMENT_SPEED = new Attribute(AttributesAccessor.FIELD_MOVEMENT_SPEED::get);
	public static final Attribute FLYING_SPEED = new Attribute(AttributesAccessor.FIELD_FLYING_SPEED::get);
	public static final Attribute ATTACK_DAMAGE = new Attribute(AttributesAccessor.FIELD_ATTACK_DAMAGE::get);
	public static final Attribute ATTACK_KNOCKBACK = new Attribute(AttributesAccessor.FIELD_ATTACK_KNOCKBACK::get);
	public static final Attribute ATTACK_SPEED = new Attribute(AttributesAccessor.FIELD_ATTACK_SPEED::get);
	public static final Attribute ARMOR = new Attribute(AttributesAccessor.FIELD_ARMOR::get);
	public static final Attribute ARMOR_TOUGHNESS = new Attribute(AttributesAccessor.FIELD_ARMOR_TOUGHNESS::get);
	public static final Attribute LUCK = new Attribute(AttributesAccessor.FIELD_LUCK::get);

	private final Supplier<Object> object;
}
