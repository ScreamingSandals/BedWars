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
public class Attribute implements AttributesAccessor {
	public static final Attribute MAX_HEALTH = new Attribute(FIELD_MAX_HEALTH_1);
	public static final Attribute FOLLOW_RANGE = new Attribute(FIELD_FOLLOW_RANGE_1);
	public static final Attribute KNOCKBACK_RESISTANCE = new Attribute(FIELD_KNOCKBACK_RESISTANCE_1);
	public static final Attribute MOVEMENT_SPEED = new Attribute(FIELD_MOVEMENT_SPEED_1);
	public static final Attribute FLYING_SPEED = new Attribute(FIELD_FLYING_SPEED_1);
	public static final Attribute ATTACK_DAMAGE = new Attribute(FIELD_ATTACK_DAMAGE_1);
	public static final Attribute ATTACK_KNOCKBACK = new Attribute(FIELD_ATTACK_KNOCKBACK_1);
	public static final Attribute ATTACK_SPEED = new Attribute(FIELD_ATTACK_SPEED_1);
	public static final Attribute ARMOR = new Attribute(FIELD_ARMOR_1);
	public static final Attribute ARMOR_TOUGHNESS = new Attribute(FIELD_ARMOR_TOUGHNESS_1);
	public static final Attribute LUCK = new Attribute(FIELD_LUCK_1);

	private final Supplier<Object> object;
}
