package org.screamingsandals.bedwars.api.special;

public interface ThrowableFireball extends SpecialItem {

    float getDamage();

    boolean isIncendiary();

    boolean hasPerfectVelocity();

    boolean damagesThrower();

    void run();

}
