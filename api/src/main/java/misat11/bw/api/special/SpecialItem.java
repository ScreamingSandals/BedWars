package misat11.bw.api.special;

import org.bukkit.entity.Player;

import misat11.bw.api.Game;
import misat11.bw.api.Team;

/**
 * @author Bedwars Team
 *
 */
public interface SpecialItem {
	/**
	 * @return
	 */
	public Game getGame();
	
	/**
	 * @return
	 */
	public Player getPlayer();
	
	/**
	 * @return
	 */
	public Team getTeam();
}
