package org.screamingsandals.bedwars.api;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
public interface Team<L extends Wrapper> {
    /**
     * @return
     */
    Game getGame();

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    TeamColor getColor();

    /**
     * @return
     */
    L getTeamSpawn();

    /**
     * @return
     */
    L getTargetBlock();

    /**
     * @return
     */
    int getMaxPlayers();
}
