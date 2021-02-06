package org.screamingsandals.bedwars.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.screamingsandals.lib.utils.Wrapper;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigSerializable
public class PreparedLocation implements Wrapper {
    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public PreparedLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T as(Class<T> type) {
        if (type == Location.class) {
            return (T) new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        }
        throw new UnsupportedOperationException("Unsupported type!");
    }
}
