package org.screamingsandals.bedwars.api.utils;

import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.bukkit.entity.Player;

/**
 * @author Bedwars Team
 */
public interface DelayFactory {

    /**
     * @return
     */
    boolean getDelayActive();

    /**
     * @return
     */
    SpecialItem getSpecialItem();

    /**
     * @return
     */
    int getRemainDelay();

    /**
     * @return
     */
    Player getPlayer();
}
