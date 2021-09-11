package org.screamingsandals.bedwars.game;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.CitizensUtils;

public class GameStore implements org.screamingsandals.bedwars.api.game.GameStore {
    private final Location loc;
    private final String shop;
    private final String shopName;
    private final boolean enableCustomName;
    private final boolean useParent;
    private LivingEntity entity;
    private EntityType type;
    private boolean isBaby;
    private String skinName;

    public GameStore(Location loc, String shop, boolean useParent, String shopName, boolean enableCustomName, boolean isBaby) {
        this(loc, shop, useParent, EntityType.VILLAGER, shopName, enableCustomName, isBaby, null);
    }

    public GameStore(Location loc, String shop, boolean useParent, EntityType type, String shopName, boolean enableCustomName, boolean isBaby, String skinName) {
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
        this.skinName = skinName;
    }

    public LivingEntity spawn() {
        if (entity == null) {
            EntityType typ = type;
            if (typ == EntityType.PLAYER) {
                if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
                    LivingEntity npc = CitizensUtils.spawnNPC(this);
                    if (npc != null) {
                        this.entity = npc;
                        return npc;
                    }
                }
                typ = EntityType.VILLAGER;
            }

            try {
                entity = (LivingEntity) loc.getWorld().spawnEntity(loc, typ, CreatureSpawnEvent.SpawnReason.CUSTOM);
            } catch (Throwable throwable) {
                entity = (LivingEntity) loc.getWorld().spawnEntity(loc, typ);
            }

            entity.setRemoveWhenFarAway(false);
            try {
                entity.setSilent(Main.getConfigurator().config.getBoolean("shopkeepers-are-silent"));
            } catch (Throwable ignored) {
            }

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

    public LivingEntity kill() {
        final LivingEntity livingEntity = entity;
        if (entity != null) {
            final Chunk chunk = entity.getLocation().getChunk();

            if (!chunk.isLoaded()) {
                chunk.load();
            }
            entity.remove();
            if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
                CitizensUtils.remove(entity);
            }
            entity = null;
        }
        return livingEntity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public EntityType getEntityType() {
        return type;
    }

    public void setEntityType(EntityType type) {
        if (type != null && type.isAlive()) {
            this.type = type;
        }
    }

    public void setEntityTypeNPC(String skinName) {
        this.type = EntityType.PLAYER;
        this.skinName = skinName;
    }

    public Location getStoreLocation() {
        return loc;
    }

    public String getShopFile() {
        return shop;
    }

    public String getShopCustomName() {
        return shopName;
    }

    public boolean getUseParent() {
        return useParent && shop != null;
    }

    public boolean isShopCustomName() {
        return enableCustomName;
    }

    public boolean isBaby() {
        return isBaby;
    }

    public void setBaby(boolean isBaby) {
        this.isBaby = isBaby;
    }

    public String getSkinName() {
        return skinName;
    }
}
