package org.screamingsandals.bedwars.api.game;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 * @author Bedwars Team
 */
public interface GameStore {
    /**
     * @return shop entity
     */
    LivingEntity getEntity();

    /**
     * @return entity type used for the shop
     */
    EntityType getEntityType();

    /**
     * @return location of this store
     */
    Location getStoreLocation();

    /**
     * @return shop file
     */
    String getShopFile();

    /**
     * @return shopkeeper's name
     */
    String getShopCustomName();

    /**
     * @return true if shop file should be merged with custom shop file
     */
    boolean getUseParent();

    /**
     * @return true if shopkeeper has name
     */
    boolean isShopCustomName();

    /**
     * @return true if shopkeeper is baby
     */
    boolean isBaby();

    /**
     * @return if type is PLAYER, than returns skin, otherwise null
     */
    String getSkinName();
}
