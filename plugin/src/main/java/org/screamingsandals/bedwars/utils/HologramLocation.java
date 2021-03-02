package org.screamingsandals.bedwars.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.screamingsandals.lib.bukkit.world.BukkitWorldHolder;
import org.screamingsandals.lib.utils.Wrapper;
import org.screamingsandals.lib.world.LocationHolder;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigSerializable
public class HologramLocation implements Wrapper {
    private String world;
    private double x;
    private double y;
    private double z;

    public HologramLocation(LocationHolder location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T as(Class<T> type) {
        if (type == Location.class) {
            return (T) new Location(Bukkit.getWorld(world), x, y, z);
        }
        if (type == LocationHolder.class) {
            var holder = new LocationHolder(x, y, z);
            holder.setWorld(new BukkitWorldHolder(Bukkit.getWorld(world)));
            return (T) holder;
        }
        throw new UnsupportedOperationException("Unsupported type!");
    }
}
