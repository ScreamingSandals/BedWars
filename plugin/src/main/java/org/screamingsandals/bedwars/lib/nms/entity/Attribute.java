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
import org.screamingsandals.bedwars.lib.nms.accessors.AttributesMapping;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class Attribute {
	public static final Attribute MAX_HEALTH = new Attribute(AttributesMapping.FIELD_MAX_HEALTH::getConstantValue);
	public static final Attribute FOLLOW_RANGE = new Attribute(AttributesMapping.FIELD_FOLLOW_RANGE::getConstantValue);
	public static final Attribute KNOCKBACK_RESISTANCE = new Attribute(AttributesMapping.FIELD_KNOCKBACK_RESISTANCE::getConstantValue);
	public static final Attribute MOVEMENT_SPEED = new Attribute(AttributesMapping.FIELD_MOVEMENT_SPEED::getConstantValue);
	public static final Attribute FLYING_SPEED = new Attribute(AttributesMapping.FIELD_FLYING_SPEED::getConstantValue);
	public static final Attribute ATTACK_DAMAGE = new Attribute(AttributesMapping.FIELD_ATTACK_DAMAGE::getConstantValue);
	public static final Attribute ATTACK_KNOCKBACK = new Attribute(AttributesMapping.FIELD_ATTACK_KNOCKBACK::getConstantValue);
	public static final Attribute ATTACK_SPEED = new Attribute(AttributesMapping.FIELD_ATTACK_SPEED::getConstantValue);
	public static final Attribute ARMOR = new Attribute(AttributesMapping.FIELD_ARMOR::getConstantValue);
	public static final Attribute ARMOR_TOUGHNESS = new Attribute(AttributesMapping.FIELD_ARMOR_TOUGHNESS::getConstantValue);
	public static final Attribute LUCK = new Attribute(AttributesMapping.FIELD_LUCK::getConstantValue);

	private final Supplier<Object> object;
}
