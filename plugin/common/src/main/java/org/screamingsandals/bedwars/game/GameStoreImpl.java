/*
 * Copyright (C) 2022 ScreamingSandals
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

package org.screamingsandals.bedwars.game;

import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.entity.LivingEntity;
import org.screamingsandals.lib.entity.Entities;
import org.screamingsandals.lib.entity.type.EntityType;
import org.screamingsandals.lib.npc.NPC;
import org.screamingsandals.lib.npc.NPCManager;
import org.screamingsandals.lib.npc.skin.NPCSkin;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.impl.world.Locations;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
public class GameStoreImpl implements GameStore, SerializableGameComponent {
    private static final EntityType VILLAGER = EntityType.of("villager");

    @NotNull
    private final Location storeLocation;
    @Nullable
    private String shopFile = null;
    @Nullable
    private String shopCustomName = null;
    @Nullable
    private LivingEntity entity;
    @Getter
    private NPC npc;
    @NotNull
    private EntityType entityType = VILLAGER;
    private boolean isBaby = false;
    @Nullable
    private String skinName = null;

    public GameStoreImpl(@NotNull Location storeLocation) {
        this.storeLocation = storeLocation;
    }

    public Object spawn() {
        if (entity == null) {
            var typ = entityType;
            if (typ.is("player")) {
                try {
                    npc = NPCManager
                            .npc(storeLocation)
                            .touchable(true)
                            .lookAtPlayer(true);

                    if (shopCustomName != null) {
                        npc.displayName(List.of(Component.fromLegacy(shopCustomName)));
                    }

                    NPCSkin.retrieveSkin(skinName).thenAccept(skin -> {
                        if (skin != null) {
                            npc.skin(skin);
                        }
                    });

                    return npc.show();
                } catch (Throwable ignored) {}
                typ = VILLAGER;
                npc = null;
            }

            entity = (LivingEntity) Objects.requireNonNull(Entities.spawn(typ, storeLocation));
            entity.setRemoveWhenFarAway(false);

            if (shopCustomName != null) {
                entity.setCustomName(shopCustomName);
                entity.setCustomNameVisible(true);
            }

            if (MainConfig.getInstance().node("shopkeepers-are-silent").getBoolean()) {
                try {
                    entity.setSilent(true);
                } catch (Throwable ignored) {}
            }

            if (entity.getEntityType().is("villager")) {
                entity.setMetadata("villager_profession", "farmer");
            }

            if (entity.hasMetadata("is_baby")) {
                entity.setMetadata("is_baby", isBaby);
            }
        }
        return entity;
    }

    public LivingEntity kill() {
        final var livingEntity = entity;
        if (entity != null) {
            final var chunk = entity.getLocation().getChunk();

            if (!chunk.isLoaded()) {
                chunk.load();
            }
            entity.remove();
            entity = null;
        }
        if (npc != null) {
            try {
                npc.destroy();
            } catch (Throwable ignored) {} // silently ignore if destroying of entity failed
            npc = null;
        }
        return livingEntity;
    }

    public void setEntityType(EntityType type) {
        if (type != null && type.isAlive()) {
            this.entityType = type;
        }
    }

    public void setEntityTypeNPC(String skinName) {
        this.entityType = EntityType.of("player");
        this.skinName = skinName;
    }

    @Override
    public void saveTo(@NotNull ConfigurationNode node) throws SerializationException {
        node.node("loc").set(MiscUtils.writeLocationToString(storeLocation));
        node.node("shop").set(shopFile);
        node.node("type").set(entityType.location().asString());
        if (shopCustomName != null) {
            node.node("custom-name").set(shopCustomName);
        }
        node.node("isBaby").set(isBaby ? "true" : "false");
        node.node("skin").set(skinName);
    }

    public static class Loader implements SerializableGameComponentLoader<GameStoreImpl> {
        public static final Loader INSTANCE = new Loader();

        @Override
        @NotNull
        public Optional<GameStoreImpl> load(@NotNull GameImpl game, @NotNull ConfigurationNode node) {
            if (node.isMap()) {
                var oldStr = node.node("name").getString();
                if (oldStr != null) {
                    oldStr = MiscUtils.toMiniMessage(oldStr);
                }
                var store = new GameStoreImpl(Locations.wrapLocation(MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(node.node("loc").getString()))));
                store.setShopFile(node.node("shop").getString());
                store.setEntityType(EntityType.of(node.node("type").getString("VILLAGER")));
                store.setShopCustomName(oldStr != null ? oldStr : node.node("custom-name").getString());
                store.setBaby(node.node("isBaby").getBoolean());
                store.setSkinName(node.node("skin").getString());
                return Optional.of(store);
            } else {
                return Optional.of(new GameStoreImpl(Locations.wrapLocation(MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(node.getString())))));
            }
        }
    }
}
