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
import org.screamingsandals.bedwars.lib.nms.accessors.GenericAttributesAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class Attribute {
	public static final Attribute MAX_HEALTH = new Attribute(() -> ClassStorage.getField(GenericAttributesAccessor.getFieldMAX_HEALTH()));
	public static final Attribute FOLLOW_RANGE = new Attribute(() -> ClassStorage.getField(GenericAttributesAccessor.getFieldFOLLOW_RANGE()));
	public static final Attribute KNOCKBACK_RESISTANCE = new Attribute(() -> ClassStorage.getField(GenericAttributesAccessor.getFieldKNOCKBACK_RESISTANCE()));
	public static final Attribute MOVEMENT_SPEED = new Attribute(() -> ClassStorage.getField(GenericAttributesAccessor.getFieldMOVEMENT_SPEED()));
	public static final Attribute FLYING_SPEED = new Attribute(() -> ClassStorage.getField(GenericAttributesAccessor.getFieldFLYING_SPEED()));
	public static final Attribute ATTACK_DAMAGE = new Attribute(() -> ClassStorage.getField(GenericAttributesAccessor.getFieldATTACK_DAMAGE()));
	public static final Attribute ATTACK_KNOCKBACK = new Attribute(() -> ClassStorage.getField(GenericAttributesAccessor.getFieldATTACK_KNOCKBACK()));
	public static final Attribute ATTACK_SPEED = new Attribute(() -> ClassStorage.getField(GenericAttributesAccessor.getFieldATTACK_SPEED()));
	public static final Attribute ARMOR = new Attribute(() -> ClassStorage.getField(GenericAttributesAccessor.getFieldARMOR()));
	public static final Attribute ARMOR_TOUGHNESS = new Attribute(() -> ClassStorage.getField(GenericAttributesAccessor.getFieldARMOR_TOUGHNESS()));
	public static final Attribute LUCK = new Attribute(() -> ClassStorage.getField(GenericAttributesAccessor.getFieldLUCK()));

	private final Supplier<Object> object;
}
