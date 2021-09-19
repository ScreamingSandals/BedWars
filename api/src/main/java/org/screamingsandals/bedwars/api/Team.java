package org.screamingsandals.bedwars.api;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
public interface Team<L extends Wrapper, C extends TeamColor, G extends Game<?,?,?,?,?,?>> {
    /**
     * @return
     */
    G getGame();

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    C getColor();

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
