package misat11.bw.api.special;

import org.bukkit.entity.Player;

import misat11.bw.api.Game;
import misat11.bw.api.Team;

public interface SpecialItem {
	public Game getGame();
	
	public Player getPlayer();
	
	public Team getTeam();
}
