/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.lib.nms.entity;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.lib.nms.accessors.*;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class FakeEntityNMS<E extends Entity> extends EntityNMS implements Listener {
    @Getter
    protected final List<Player> viewers = new ArrayList<>();
    private E entity;
    private boolean visible;

    @SuppressWarnings("unchecked")
    protected FakeEntityNMS(final Object nmsEntity) {
        try {
            entity = (E) ClassStorage.getMethod(nmsEntity, "getBukkitEntity").invoke();
            this.handler = nmsEntity;
        } catch (Throwable t) {
            Debug.warn("Could not create FakeEntityNMS: " + t.getMessage());
        }
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;

            if (visible) {
                viewers.forEach(this::onViewerAdded);
            } else {
                viewers.forEach(viewer -> ClassStorage.sendPacket(viewer, this.createDespawnPacket()));
            }
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
            viewers.add(viewer);
            onViewerAdded(viewer);
        }
    }

    public void removeViewer(Player viewer) {
        if (viewers.contains(viewer)) {
            onViewerRemoved(viewer);
            viewers.remove(viewer);
        }

        if (viewers.isEmpty()) {
            HandlerList.unregisterAll(this);
        }
    }

    public void onViewerAdded(Player viewer) {
        if (visible) {
            ClassStorage.sendPacket(viewer, createSpawnPacket());
            teleport(viewer, createPosition(viewer));
        }
    }

    public void onViewerRemoved(Player viewer) {
        if (visible) {
            ClassStorage.sendPacket(viewer, createDespawnPacket());
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player viewer = event.getPlayer();
        if (viewers.contains(viewer) && visible) {
            teleport(viewer, createPosition(event.getTo()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        final Player viewer = event.getPlayer();
        if (viewers.contains(viewer) && visible) {
            teleport(viewer, createPosition(viewer));
        }
    }

    public Location createPosition(final Player viewer) {
        return createPosition(viewer.getLocation());
    }

    public Location createPosition(final Location position) {
        final Location clone = position.clone();
        clone.setPitch(clone.getPitch() - 30);
        clone.setYaw(clone.getYaw());
        clone.add(clone.getDirection().multiply(40));
        return clone;
    }

    public Object createSpawnPacket() {
        if (this.entity instanceof LivingEntity) {
            try {
                return ClientboundAddMobPacketAccessor.CONSTRUCTOR_0.get()
                        .newInstance(handler);
            } catch (Throwable t) {
                Debug.warn("Failed to create Spawn packet for fake entity!: " + t.getMessage());
            }
        }
        return null;
    }

    public Object createDespawnPacket() {
        try {
            return ClientboundRemoveEntitiesPacketAccessor.CONSTRUCTOR_1.get()
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
            if (ClientboundTeleportEntityPacketAccessor.CONSTRUCTOR_1.get() != null) {
                Object move = ClassStorage.getMethod(PositionMoveRotationAccessor.METHOD_OF.get()).invokeStatic(this.handler);

                return ClientboundTeleportEntityPacketAccessor.CONSTRUCTOR_1.get()
                        .newInstance(
                                this.entity.getEntityId(),
                                move,
                                Collections.emptySet(),
                                this.entity.isOnGround()
                        );
            } else {
                return ClientboundTeleportEntityPacketAccessor.CONSTRUCTOR_0.get()
                        .newInstance(
                                handler
                        );
            }
        } catch (Throwable t) {
            Debug.warn("Failed to create location packet for fake entity!: " + t.getMessage());
        }
        return null;
    }

    public void teleport(Player viewer, Location location) {
        try {
            ClassStorage.getMethod(handler, METHOD_ABS_SNAP_TO.get())
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
                SynchedEntityDataAccessor.METHOD_WATCH.get().invoke(dataWatcher, position, data);
            } catch (Throwable ignored) {
            }
            Object metadataPacket = createMetadataPacket();
            viewers.forEach(viewer -> ClassStorage.sendPacket(viewer, metadataPacket));
        }
    }

    public Object createMetadataPacket() {
        try {
            final Object dataWatcher = getDataWatcher();
            return ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_0.get()
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
