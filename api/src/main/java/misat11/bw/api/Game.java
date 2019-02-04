package misat11.bw.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface Game {
	public String getName();
	
	public GameStatus getStatus();
	
	// Activate and deactivate arena
	
	public void start();
	
	public void stop();
	
	default boolean isActivated() {
		return getStatus() != GameStatus.DISABLED;
	}
	
	// PLAYER MANAGEMENT
	
	public void joinToGame(Player player);
	
	public void leaveFromGame(Player player);
	
	public void selectPlayerTeam(Player player, Team team);
	
	// INGAME
	
	public World getGameWorld();
	
	public Location getPos1();
	
	public Location getPos2();
	
	public Location getSpectatorSpawn();
	
	public int getGameTime();
	
	public int getMinPlayers();
	
	public int countConnectedPlayers();
	
	public List<Player> getConnectedPlayers();
	
	public List<GameStore> getGameStores();
	
	public List<Team> getAvailableTeams();
	
	public List<RunningTeam> getRunningTeams();
	
	public RunningTeam getTeamOfPlayer(Player player);
	
	public boolean isLocationInArena(Location location);
	
	public boolean isBlockAddedDuringGame(Location location);
	
	// LOBBY
	
	public World getLobbyWorld();
	
	public Location getLobbySpawn();
	
	public int getLobbyCountdown();
	
	
}
