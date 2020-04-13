package org.screamingsandals.lib.signmanager;

import org.bukkit.Location;

public class SignBlock {
    private final Location loc;
    private final String name;

    public SignBlock(Location loc, String name) {
        this.loc = loc;
        this.name = name;
    }

    public Location getLocation() {
        return loc;
    }

    public String getName() {
        return name;
    }
}
