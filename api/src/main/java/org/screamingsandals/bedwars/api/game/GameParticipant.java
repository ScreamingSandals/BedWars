package org.screamingsandals.bedwars.api.game;

import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * TODO: add isBot method and test mode where you fight against bots
 */
public interface GameParticipant extends Wrapper {
    default boolean isPlayer() {
        return this instanceof BWPlayer;
    }
}
