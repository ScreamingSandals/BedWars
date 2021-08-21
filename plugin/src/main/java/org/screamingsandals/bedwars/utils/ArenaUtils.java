package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.chunk.ChunkHolder;

@UtilityClass
public class ArenaUtils {
    public boolean isInArea(LocationHolder l, LocationHolder p1, LocationHolder p2) {
        if (!p1.getWorld().equals(l.getWorld())) {
            return false;
        }

        var min = new LocationHolder(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()),
                Math.min(p1.getZ(), p2.getZ()), 0, 0, p1.getWorld());
        var max = new LocationHolder(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()),
                Math.max(p1.getZ(), p2.getZ()), 0, 0, p1.getWorld());
        return (min.getX() <= l.getX() && min.getY() <= l.getY() && min.getZ() <= l.getZ() && max.getX() >= l.getX()
                && max.getY() >= l.getY() && max.getZ() >= l.getZ());
    }

    @Deprecated
    public boolean isInArea(LocationHolder l, Location p1, Location p2) {
        return isInArea(l.as(Location.class), p1, p2);
    }

    @Deprecated
    public boolean isInArea(Location l, Location p1, Location p2) {
        if (!p1.getWorld().equals(l.getWorld())) {
            return false;
        }

        Location min = new Location(p1.getWorld(), Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()),
                Math.min(p1.getZ(), p2.getZ()));
        Location max = new Location(p1.getWorld(), Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()),
                Math.max(p1.getZ(), p2.getZ()));
        return (min.getX() <= l.getX() && min.getY() <= l.getY() && min.getZ() <= l.getZ() && max.getX() >= l.getX()
                && max.getY() >= l.getY() && max.getZ() >= l.getZ());
    }

    public boolean isChunkInArea(Chunk l, Location p1, Location p2) {
        if (!p1.getWorld().equals(l.getWorld())) {
            return false;
        }

        Chunk min = new Location(p1.getWorld(), Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()),
                Math.min(p1.getZ(), p2.getZ())).getChunk();
        Chunk max = new Location(p1.getWorld(), Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()),
                Math.max(p1.getZ(), p2.getZ())).getChunk();
        return (min.getX() <= l.getX() && min.getZ() <= l.getZ() && max.getX() >= l.getX() && max.getZ() >= l.getZ());
    }

    public boolean isChunkInArea(ChunkHolder l, Location p1, Location p2) {
        if (!p1.getWorld().equals(l.getWorld().as(World.class))) {
            return false;
        }

        var min = new Location(p1.getWorld(), Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()),
                Math.min(p1.getZ(), p2.getZ())).getChunk();
        var max = new Location(p1.getWorld(), Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()),
                Math.max(p1.getZ(), p2.getZ())).getChunk();
        return (min.getX() <= l.getX() && min.getZ() <= l.getZ() && max.getX() >= l.getX() && max.getZ() >= l.getZ());
    }
}
