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
