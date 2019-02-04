package misat11.bw.api;

import java.util.List;

import org.bukkit.entity.Player;

public interface RunningTeam extends Team {
	public int countConnectedPlayers();
	
	public List<Player> getConnectedPlayers();
	
	public boolean isPlayerInTeam(Player player);
	
	public boolean isDead();
	
	public boolean isAlive();
	
	public boolean isTargetBlockExists();
	
	public org.bukkit.scoreboard.Team getScoreboardTeam();
}
