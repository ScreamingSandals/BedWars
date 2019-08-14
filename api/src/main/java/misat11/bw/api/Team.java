package misat11.bw.api;

import org.bukkit.Location;

/**
 * @author Bedwars Team
 *
 */
public interface Team {
	/**
	 * @return
	 */
	public Game getGame();
	
	/**
	 * @return
	 */
	public String getName();
	
	/**
	 * @return
	 */
	public TeamColor getColor();
	
	/**
	 * @return
	 */
	public Location getTeamSpawn();
	
	/**
	 * @return
	 */
	public Location getTargetBlock();
	
	/**
	 * @return
	 */
	public int getMaxPlayers();
}
