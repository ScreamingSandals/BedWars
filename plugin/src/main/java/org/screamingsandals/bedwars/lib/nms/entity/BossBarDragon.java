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
    public Location createPosition(Location position) {
        final Location clone = position.clone();
        clone.setPitch(0);
        clone.setYaw(0);
        clone.setY(clone.getY() - 500);
        return clone;
    }
}
