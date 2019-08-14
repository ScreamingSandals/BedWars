package misat11.bw.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * @author Bedwars Team
 *
 */
public interface RunningTeam extends Team {
	/**
	 * @return
	 */
	public int countConnectedPlayers();
	
	/**
	 * @return
	 */
	public List<Player> getConnectedPlayers();
	
	/**
	 * @param player
	 * @return
	 */
	public boolean isPlayerInTeam(Player player);
	
	/**
	 * @return
	 */
	public boolean isDead();
	
	/**
	 * @return
	 */
	public boolean isAlive();
	
	/**
	 * @return
	 */
	public boolean isTargetBlockExists();
	
	/**
	 * @return
	 */
	public org.bukkit.scoreboard.Team getScoreboardTeam();
	
	/**
	 * @param location
	 */
	public void addTeamChest(Location location);
	
	/**
	 * @param block
	 */
	public void addTeamChest(Block block);
	
	/**
	 * @param location
	 */
	public void removeTeamChest(Location location);
	
	/**
	 * @param block
	 */
	public void removeTeamChest(Block block);
	
	/**
	 * @param location
	 * @return
	 */
	public boolean isTeamChestRegistered(Location location);
	
	/**
	 * @param block
	 * @return
	 */
	public boolean isTeamChestRegistered(Block block);
	
	/**
	 * @return
	 */
	public Inventory getTeamChestInventory();
	
	/**
	 * @return
	 */
	public int countTeamChests();
}
