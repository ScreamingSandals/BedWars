package misat11.bw.api;

import java.util.List;

import org.bukkit.entity.Player;

public interface BedwarsAPI {
	public List<Game> getGames();
	
	public boolean isGameWithNameExists(String name);
	
	public Game getGameByName(String name);
	
	public Game getGameOfPlayer(Player player);
}
