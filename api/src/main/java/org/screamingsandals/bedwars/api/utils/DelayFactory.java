package org.screamingsandals.bedwars.api.utils;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.api.special.SpecialItem;

/**
 * @author ScreamingSandals
 */
@ApiStatus.NonExtendable
public interface DelayFactory {

    /**
     * @return is delay active
     */
    boolean isDelayActive();

    /**
     * @return special item for which this delay is used
     */
    SpecialItem<?,?,?> getSpecialItem();

    /**
     * @return remaining delay
     */
    int getRemainDelay();

    /**
     * @return the game participant using this delay factory
     */
    BWPlayer getPlayer();
}
