package org.screamingsandals.bedwars.api.special;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.Team;
import org.bukkit.entity.Player;

/**
 * @author Bedwars Team
 */
public interface SpecialItem {
    /**
     * @return
     */
    Game getGame();

    /**
     * @return
     */
    Player getPlayer();

    /**
     * @return
     */
    Team getTeam();
}
