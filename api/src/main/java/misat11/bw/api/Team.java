package misat11.bw.api;

import org.bukkit.Location;

public interface Team {
	public String getName();
	
	public TeamColor getColor();
	
	public Location getTeamSpawn();
	
	public Location getTargetBlock();
	
	public int getMaxPlayers();
}
