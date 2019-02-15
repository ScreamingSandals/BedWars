package misat11.bw.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;
import misat11.bw.api.RunningTeam;

public class BedwarsTeamChestOpenEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private Player player = null;
	private RunningTeam team = null;
	private boolean cancelled = false;

	public BedwarsTeamChestOpenEvent(Game game, Player player, RunningTeam team) {
		this.player = player;
		this.team = team;
		this.game = game;
	}

	public static HandlerList getHandlerList() {
		return BedwarsTeamChestOpenEvent.handlers;
	}

	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsTeamChestOpenEvent.handlers;
	}

	public Player getPlayer() {
		return this.player;
	}

	public RunningTeam getTeam() {
		return this.team;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
