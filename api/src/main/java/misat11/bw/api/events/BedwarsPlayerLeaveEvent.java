package misat11.bw.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;
import misat11.bw.api.RunningTeam;

public class BedwarsPlayerLeaveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private Player player = null;
	private RunningTeam team = null;

	public BedwarsPlayerLeaveEvent(Game game, Player player, RunningTeam team) {
		this.game = game;
		this.player = player;
		this.team = team;
	}

	public static HandlerList getHandlerList() {
		return BedwarsPlayerLeaveEvent.handlers;
	}

	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsPlayerLeaveEvent.handlers;
	}

	public Player getPlayer() {
		return this.player;
	}

	public RunningTeam getTeam() {
		return this.team;
	}

}
