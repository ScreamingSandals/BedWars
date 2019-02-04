package misat11.bw.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Team;

public class BedwarsPlayerJoinTeamEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Player player = null;
	private Team team = null;
	private Team prevTeam = null;

	public BedwarsPlayerJoinTeamEvent(Team team, Player player, Team prevTeam) {
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

	public Team getTeam() {
		return this.team;
	}

	public Team getPreviousTeam() {
		return this.prevTeam;
	}

	public void setTeam(Team team) {
		this.team = team;
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
