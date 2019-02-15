package misat11.bw.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.RunningTeam;

public class BedwarsPlayerJoinTeamEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Player player = null;
	private RunningTeam team = null;
	private RunningTeam prevTeam = null;

	public BedwarsPlayerJoinTeamEvent(RunningTeam team, Player player, RunningTeam prevTeam) {
		this.player = player;
		this.team = team;
		this.prevTeam = prevTeam;
	}

	public static HandlerList getHandlerList() {
		return BedwarsPlayerJoinTeamEvent.handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsPlayerJoinTeamEvent.handlers;
	}

	public Player getPlayer() {
		return this.player;
	}

	public RunningTeam getTeam() {
		return this.team;
	}

	public RunningTeam getPreviousTeam() {
		return this.prevTeam;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

}
