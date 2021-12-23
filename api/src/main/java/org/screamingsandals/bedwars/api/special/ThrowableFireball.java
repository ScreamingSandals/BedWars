package org.screamingsandals.bedwars.api.special;

import org.bukkit.Location;

public interface ThrowableFireball extends SpecialItem {

    float getDamage();

    boolean isIncendiary();

    boolean hasPerfectVelocity();

    boolean damageThrower();

    void run(Location location);

}
