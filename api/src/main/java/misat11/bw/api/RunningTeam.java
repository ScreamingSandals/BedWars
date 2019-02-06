package misat11.bw.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface RunningTeam extends Team {
	public int countConnectedPlayers();
	
	public List<Player> getConnectedPlayers();
	
	public boolean isPlayerInTeam(Player player);
	
	public boolean isDead();
	
	public boolean isAlive();
	
	public boolean isTargetBlockExists();
	
	public org.bukkit.scoreboard.Team getScoreboardTeam();
	
	public void addTeamChest(Location location);
	
	public void addTeamChest(Block block);
	
	public void removeTeamChest(Location location);
	
	public void removeTeamChest(Block block);
	
	public boolean isTeamChestRegistered(Location location);
	
	public boolean isTeamChestRegistered(Block block);
	
	public Inventory getTeamChestInventory();
}
