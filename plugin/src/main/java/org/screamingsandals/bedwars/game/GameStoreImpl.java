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
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.lib.entity.EntityLiving;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.entity.type.EntityTypeHolder;
import org.screamingsandals.lib.npc.NPC;
import org.screamingsandals.lib.npc.NPCManager;
import org.screamingsandals.lib.npc.skin.NPCSkin;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.world.LocationHolder;

import java.util.List;

@Data
public class GameStoreImpl implements GameStore<EntityLiving, EntityTypeHolder, LocationHolder> {
    private final LocationHolder storeLocation;
    private final String shopFile;
    private final String shopCustomName;
    private final boolean enabledCustomName;
    private final boolean useParent;
    private EntityLiving entity;
    @Getter
    private NPC npc;
    private EntityTypeHolder entityType;
    private boolean isBaby;
    private String skinName;

    public GameStoreImpl(LocationHolder storeLocation, String shopFile, boolean useParent, String shopCustomName, boolean enabledCustomName, boolean isBaby) {
        this(storeLocation, shopFile, useParent, EntityTypeHolder.of("villager"), shopCustomName, enabledCustomName, isBaby, null);
    }

    public GameStoreImpl(LocationHolder storeLocation, String shopFile, boolean useParent, EntityTypeHolder entityType, String shopCustomName, boolean enabledCustomName, boolean isBaby, String skinName) {
        if (entityType == null || !entityType.isAlive()) {
            entityType = EntityTypeHolder.of("villager");
        }
        this.storeLocation = storeLocation;
        this.shopFile = shopFile;
        this.useParent = useParent;
        this.entityType = entityType;
        this.shopCustomName = shopCustomName;
        this.enabledCustomName = enabledCustomName;
        this.isBaby = isBaby;
        this.skinName = skinName;
    }

    public Object spawn() {
        if (entity == null) {
            var typ = entityType;
            if (typ.is("player")) {
                try {
                    npc = NPCManager
                            .npc(storeLocation)
                            .touchable(true)
                            .lookAtPlayer(true)
                            .displayName(List.of(Component.fromLegacy(shopCustomName)));

                    NPCSkin.retrieveSkin(skinName).thenAccept(skin -> {
                        if (skin != null) {
                            npc.skin(skin);
                        }
                    });

                    return npc.show();
                } catch (Throwable ignored) {}
                typ = EntityTypeHolder.of("VILLAGER");
                npc = null;
            }

            entity = EntityMapper.<EntityLiving>spawn(typ, storeLocation).orElseThrow();
            entity.setRemoveWhenFarAway(false);

            if (enabledCustomName) {
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

    public EntityLiving kill() {
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
            npc.destroy();
            npc = null;
        }
        return livingEntity;
    }

    public void setEntityType(EntityTypeHolder type) {
        if (type != null && type.isAlive()) {
            this.entityType = type;
        }
    }

    public void setEntityTypeNPC(String skinName) {
        this.entityType = EntityTypeHolder.of("player");
        this.skinName = skinName;
    }
}
