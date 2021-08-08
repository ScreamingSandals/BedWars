package org.screamingsandals.bedwars.api.game;

import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
public interface GameStore<E extends Wrapper, T extends Wrapper, L extends Wrapper> {
    /**
     * @return shop entity
     */
    E getEntity();

    /**
     * @return entity type used for the shop
     */
    T getEntityType();

    /**
     * @return location of this store
     */
    L getStoreLocation();

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
    boolean isUseParent();

    /**
     * @return true if shopkeeper has name
     */
    boolean isEnabledCustomName();

    /**
     * @return true if shopkeeper is baby
     */
    boolean isBaby();

    /**
     * @return if type is PLAYER, than returns skin, otherwise null
     */
    String getSkinName();
}
