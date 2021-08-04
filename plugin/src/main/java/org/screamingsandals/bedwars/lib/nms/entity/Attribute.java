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
