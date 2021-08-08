package org.screamingsandals.bedwars.api.utils;

import org.screamingsandals.bedwars.api.game.GameParticipant;
import org.screamingsandals.bedwars.api.special.SpecialItem;

/**
 * @author ScreamingSandals
 */
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
    GameParticipant getParticipant();
}
