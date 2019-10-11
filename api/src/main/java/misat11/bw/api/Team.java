package misat11.bw.api;

import misat11.bw.api.game.Game;
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
