package misat11.bw.api.special;

import misat11.bw.api.Game;
import misat11.bw.api.Team;
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
