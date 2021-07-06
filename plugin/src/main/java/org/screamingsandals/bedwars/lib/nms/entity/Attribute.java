package org.screamingsandals.bedwars.lib.nms.entity;

public enum Attribute {
	MAX_HEALTH("MAX_HEALTH,field_111267_a,a"),
	FOLLOW_RANGE("FOLLOW_RANGE,field_111265_b,b"),
	KNOCKBACK_RESISTANCE("KNOCKBACK_RESISTANCE,field_111266_c,c"),
	MOVEMENT_SPEED("MOVEMENT_SPEED,field_111263_d,d"),
	FLYING_SPEED("FLYING_SPEED,field_193334_e,e"),
	ATTACK_DAMAGE("ATTACK_DAMAGE,field_111264_e,f"),
	ATTACK_KNOCKBACK("ATTACK_KNOCKBACK,field_221120_g,g"),
	ATTACK_SPEED("ATTACK_SPEED,field_188790_f,h"),
	ARMOR("ARMOR,field_188791_g,i"),
	ARMOR_TOUGHNESS("ARMOR_TOUGHNESS,field_189429_h,j"),
	LUCK("LUCK,field_188792_h,k");
	
	private String keys;
	
	Attribute(String keys) {
		this.keys = keys;
	}
	
	public String getKeys() {
		return this.keys;
	}
}
