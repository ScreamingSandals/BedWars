package org.screamingsandals.bedwars.lib.nms.entity;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.lib.nms.accessors.*;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FakeEntityNMS<E extends Entity> extends EntityNMS implements Listener {
    @Getter
    protected final List<Player> viewers = new ArrayList<>();
    private E entity;

    @Setter
    private boolean visible;

    @SuppressWarnings("unchecked")
    protected FakeEntityNMS(final Class<E> entityClass, final Location location) {
        try {
            final Object nmsEntity = ClassStorage.getMethod(
                    location.getWorld(),
                    "createEntity",
                    Location.class,
                    Class.class
            ).invoke(
                    location,
                    entityClass
            );

            entity = (E) ClassStorage.getMethod(nmsEntity, "getBukkitEntity").invoke();
            this.handler = ClassStorage.getHandle(entity);
        } catch (Throwable t) {
            Debug.warn("Could not create FakeEntityNMS: " + t.getMessage());
        }
    }

    public void setHealth(double health) {
        if (entity instanceof Damageable) {
            Damageable entity = (Damageable) this.entity;
            entity.setHealth(health * (entity.getMaxHealth() - 0.1D) + 0.1D);
            Object metadataPacket = createMetadataPacket();
            viewers.forEach(viewer -> ClassStorage.sendPacket(viewer, metadataPacket));
        }
    }

    public void addViewer(Player viewer) {
        if (viewers.isEmpty()) {
            Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
        }

        if (!viewers.contains(viewer)) {
            if (visible) {
                ClassStorage.sendPacket(viewer, createSpawnPacket());
            }
            viewers.add(viewer);
            onViewerAdded(viewer);
        }
    }

    public void removeViewer(Player viewer) {
        if (viewers.contains(viewer)) {
            onViewerRemoved(viewer);
        }

        if (viewers.isEmpty()) {
            HandlerList.unregisterAll(this);
        }
    }

    public void onViewerAdded(Player viewer) {
        if (visible) {
            teleport(viewer, viewer.getLocation());
        }
    }

    public void onViewerRemoved(Player viewer) {
        if (visible) {
            teleport(viewer, null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player viewer = event.getPlayer();
        if (viewers.contains(viewer) && visible) {
            teleport(viewer, createPosition(viewer));
        }
    }

    public Location createPosition(final Player viewer) {
        final Location position = viewer.getLocation();
        position.setPitch(position.getPitch() - 30);
        position.setYaw(position.getYaw());
        position.add(position.getDirection().multiply(40));
        return position;
    }

    public Object createSpawnPacket() {
        if (this.entity instanceof LivingEntity) {
            try {
                return PacketPlayOutSpawnEntityLivingAccessor.getConstructor0()
                        .newInstance(handler);
            } catch (Throwable t) {
                Debug.warn("Failed to create Spawn packet for fake entity!: " + t.getMessage());
            }
        }
        return null;
    }

    public Object createDespawnPacket() {
        try {
            return PacketPlayOutEntityDestroyAccessor.getConstructor1()
                    .newInstance(
                            new int[]{this.entity.getEntityId()}
                    );
        } catch (Throwable t) {
            Debug.warn("Failed to create De-spawn packet for fake entity!: " + t.getMessage());
        }
        return null;
    }

    public Object createLocationPacket() {
        try {
            return PacketPlayOutEntityTeleportAccessor.getConstructor0()
                    .newInstance(
                            handler
                    );
        } catch (Throwable t) {
            Debug.warn("Failed to create location packet for fake entity!: " + t.getMessage());
        }
        return null;
    }

    public void teleport(Player viewer, Location location) {
        if (location == null) {
            viewers.remove(viewer);
            ClassStorage.sendPacket(viewer, this.createDespawnPacket());
            return;
        }

        try {
            ClassStorage.getMethod(handler, EntityAccessor.getMethodSetLocation1())
                    .invoke(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
        } catch (Throwable t) {
            Debug.warn("Failed to set location for fake entity!: " + t.getMessage());
        }

        ClassStorage.sendPacket(viewer, createLocationPacket());
    }

    @Override
    public String getCustomName() {
        return entity.getCustomName();
    }

    @Override
    public void setCustomName(String name) {
        entity.setCustomName(name);
        Object metadataPacket = createMetadataPacket();
        viewers.forEach(viewer -> ClassStorage.sendPacket(viewer, metadataPacket));
    }

    public void metadata(final int position, final Object data) {
        final Object dataWatcher = getDataWatcher();
        if (dataWatcher != null) {
            try {
                // 1.8.8 ONLY
                DataWatcherAccessor.getMethodWatch1().invoke(dataWatcher, position, data);
            } catch (Throwable ignored) {
            }
            Object metadataPacket = createMetadataPacket();
            viewers.forEach(viewer -> ClassStorage.sendPacket(viewer, metadataPacket));
        }
    }

    public Object createMetadataPacket() {
        try {
            final Object dataWatcher = getDataWatcher();
            return PacketPlayOutEntityMetadataAccessor.getConstructor0()
                    .newInstance(entity.getEntityId(), dataWatcher, false);
        } catch (Throwable t) {
            Debug.warn("Failed to create metadata packet for fake entity!: " + t.getMessage());
        }
        return null;
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
        for (final Player viewer : new ArrayList<>(viewers)) {
            teleport(viewer, null);
        }
    }
}
