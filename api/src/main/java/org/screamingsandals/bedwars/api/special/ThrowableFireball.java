package org.screamingsandals.bedwars.api.special;

public interface ThrowableFireball extends SpecialItem {

    float getDamage();

    boolean isIncendiary();

    boolean damagesThrower();

    void run();

}
