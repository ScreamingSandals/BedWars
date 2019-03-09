package misat11.bw.special;

import org.bukkit.entity.Player;

import misat11.bw.api.Game;
import misat11.bw.api.Team;

public abstract class SpecialItem implements misat11.bw.api.special.SpecialItem {
	
	protected Game game;
	protected Player player;
	protected Team team;
	
	public SpecialItem(Game game, Player player, Team team) {
		this.game = game;
		this.player = player;
		this.team = team;
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public Team getTeam() {
		return team;
	}

}
