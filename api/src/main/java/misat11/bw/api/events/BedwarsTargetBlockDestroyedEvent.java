package misat11.bw.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;
import misat11.bw.api.RunningTeam;

/**
 * @author Bedwars Team
 *
 */
public class BedwarsTargetBlockDestroyedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private Player player = null;
	private RunningTeam team = null;

	/**
	 * @param game
	 * @param player
	 * @param team
	 */
	public BedwarsTargetBlockDestroyedEvent(Game game, Player player, RunningTeam team) {
		this.player = player;
		this.team = team;
		this.game = game;
	}

	public static HandlerList getHandlerList() {
		return BedwarsTargetBlockDestroyedEvent.handlers;
	}

	/**
	 * @return game
	 */
	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsTargetBlockDestroyedEvent.handlers;
	}

	/**
	 * @return player
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * @return team of player
	 */
	public RunningTeam getTeam() {
		return this.team;
	}

}
