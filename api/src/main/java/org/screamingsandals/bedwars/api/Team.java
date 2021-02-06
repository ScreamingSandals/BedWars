package org.screamingsandals.bedwars.api;

import org.screamingsandals.bedwars.api.game.Game;
import org.bukkit.Location;

/**
 * @author Bedwars Team
 */
public interface Team {
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
    Location getTeamSpawn();

    /**
     * @return
     */
    Location getTargetBlock();

    /**
     * @return
     */
    int getMaxPlayers();
}
