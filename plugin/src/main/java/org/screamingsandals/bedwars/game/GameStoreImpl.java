package org.screamingsandals.bedwars.game;

import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.*;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.lib.entity.EntityLiving;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.entity.type.EntityTypeHolder;
import org.screamingsandals.lib.npc.NPC;
import org.screamingsandals.lib.npc.NPCManager;
import org.screamingsandals.lib.npc.skin.NPCSkin;
import org.screamingsandals.lib.utils.AdventureHelper;
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
                            .setTouchable(true)
                            .setShouldLookAtPlayer(true)
                            .setDisplayName(List.of(AdventureHelper.toComponentNullable(shopCustomName)));

                    NPCSkin.retrieveSkin(skinName).thenAccept(npc::setSkin);

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
                entity.as(Villager.class).setProfession(Villager.Profession.FARMER);
            }

            if (entity.as(Entity.class) instanceof Ageable) {
                if (isBaby) {
                    entity.as(Ageable.class).setBaby();
                } else {
                    entity.as(Ageable.class).setAdult();
                }
            } else {
                // Some 1.16 mobs are not ageable but could be baby
                try {
                    var bentity = entity.as(Entity.class);
                    bentity.getClass().getMethod("setBaby", boolean.class).invoke(bentity, isBaby);
                } catch (Throwable ignored) {
                }
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
