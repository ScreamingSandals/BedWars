package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.Location;
import org.bukkit.entity.Wither;

public class BossBarWither extends FakeEntityNMS<Wither> {

    public BossBarWither(Location location) {
        super(Wither.class, location);
        setInvisible(true);
        metadata(20, 890);
    }
}
