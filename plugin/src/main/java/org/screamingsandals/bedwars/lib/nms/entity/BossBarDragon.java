package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.lib.nms.accessors.EntityAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.EntityEnderDragonAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

public class BossBarDragon extends FakeEntityNMS<EnderDragon> {

    public BossBarDragon(Location location) {
        super(construct(location));
        setInvisible(true);
    }

    public static Object construct(Location location) {
        try {
            final Object nmsEntity = EntityEnderDragonAccessor.getConstructor0()
                    .newInstance(ClassStorage.getHandle(location.getWorld()));
            ClassStorage.getMethod(EntityAccessor.getMethodSetLocation1()).invokeInstance(
                    nmsEntity,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getYaw(),
                    location.getPitch()
            );
            return nmsEntity;
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public Location createPosition(Player viewer) {
       final Location position = viewer.getLocation();
        position.setPitch(0);
        position.setYaw(0);
        position.setY(position.getY() - 500);
        return position;
    }
}
