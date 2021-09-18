package org.screamingsandals.bedwars.api.game;

import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
public interface ItemSpawnerType<T extends Wrapper, I extends Wrapper, C extends Wrapper> {
    /**
     * @return
     */
    String getConfigKey();

    /**
     * @return
     */
    int getInterval();

    /**
     * @return
     */
    double getSpread();

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    T getItemType();

    /**
     * @return
     */
    C getTranslatableKey();

    /**
     * @return
     */
    C getItemName();

    /**
     * @return
     */
    C getItemBoldName();

    /**
     * @return
     */
    int getDamage();

    /**
     * @return
     */
    I getItem();

    /**
     * @param amount
     * @return
     */
    I getItem(int amount);
}
