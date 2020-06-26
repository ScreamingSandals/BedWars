package org.screamingsandals.bedwars.api.game;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * @author Bedwars Team
 */
public class GameStore {
    private final Location loc;
    private final String shop;
    private final String shopName;
    private final boolean enableCustomName;
    private final boolean useParent;
    private LivingEntity entity;
    private EntityType type;
    private boolean isBaby;

    /**
     * @param loc
     * @param shop
     * @param useParent
     * @param shopName
     * @param enableCustomName
     * @param isBaby
     */
    public GameStore(Location loc, String shop, boolean useParent, String shopName, boolean enableCustomName, boolean isBaby) {
        this(loc, shop, useParent, EntityType.VILLAGER, shopName, enableCustomName, isBaby);
    }

    /**
     * @param loc
     * @param shop
     * @param useParent
     * @param type
     * @param shopName
     * @param enableCustomName
     * @param isBaby
     */
    public GameStore(Location loc, String shop, boolean useParent, EntityType type, String shopName, boolean enableCustomName, boolean isBaby) {
        if (type == null || !type.isAlive()) {
            type = EntityType.VILLAGER;
        }
        this.loc = loc;
        this.shop = shop;
        this.useParent = useParent;
        this.type = type;
        this.shopName = shopName;
        this.enableCustomName = enableCustomName;
        this.isBaby = isBaby;
    }

    /**
     * @return
     */
    public LivingEntity spawn() {
        if (entity == null) {
            try {
                entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type, CreatureSpawnEvent.SpawnReason.CUSTOM);
            } catch (Throwable throwable) {
                entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
            }

            entity.setRemoveWhenFarAway(false);

            if (enableCustomName) {
                entity.setCustomName(shopName);
                entity.setCustomNameVisible(true);
            }

            if (entity instanceof Villager) {
                ((Villager) entity).setProfession(Villager.Profession.FARMER);
            }

            if (entity instanceof Ageable) {
                if (isBaby) {
                    ((Ageable) entity).setBaby();
                } else {
                    ((Ageable) entity).setAdult();
                }
            } else {
                // Some 1.16 mobs are not ageable but could be baby
                try {
                    entity.getClass().getMethod("setBaby", boolean.class).invoke(entity, isBaby);
                } catch (Throwable ignored) {
                }
            }
        }
        return entity;
    }

    /**
     * @return killed entity
     */
    public LivingEntity kill() {
        final LivingEntity livingEntity = entity;
        if (entity != null) {
            final Chunk chunk = entity.getLocation().getChunk();

            if (!chunk.isLoaded()) {
                chunk.load();
            }
            entity.remove();
            entity = null;
        }
        return livingEntity;
    }

    /**
     * @return shop entity
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * @return entity type used for the shop
     */
    public EntityType getEntityType() {
        return type;
    }

    /**
     * @param type
     */
    public void setEntityType(EntityType type) {
        if (type != null && type.isAlive()) {
            this.type = type;
        }
    }

    /**
     * @return
     */
    public Location getStoreLocation() {
        return loc;
    }

    /**
     * @return
     */
    public String getShopFile() {
        return shop;
    }

    /**
     * @return
     */
    public String getShopCustomName() {
        return shopName;
    }

    /**
     * @return
     */
    public boolean getUseParent() {
        return useParent && shop != null;
    }

    /**
     * @return
     */
    public boolean isShopCustomName() {
        return enableCustomName;
    }

    public boolean isBaby() {
        return isBaby;
    }

    public void setBaby(boolean isBaby) {
        this.isBaby = isBaby;
    }
}
