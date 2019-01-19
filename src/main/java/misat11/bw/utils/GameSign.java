package misat11.bw.utils;

import org.bukkit.Location;

import misat11.bw.Main;
import misat11.bw.game.Game;

public class GameSign {
	private final Location loc;
	private final String game;
	
	public GameSign(Location loc, String game) {
		this.loc = loc;
		this.game = game;
	}
	
	public Location getLocation(){
		return loc;
	}
	
	public Game getGame() {
		return Main.getGame(game);
	}
	
	public String getGameName() {
		return game;
	}
}
