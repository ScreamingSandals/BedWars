package misat11.bw.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;

/**
 * @author Bedwars Team
 *
 */
public class BedwarsPlayerKilledEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private Player killer = null;
	private Player player = null;

	/**
	 * @param game
	 * @param player
	 * @param killer
	 */
	public BedwarsPlayerKilledEvent(Game game, Player player, Player killer) {
		this.player = player;
		this.killer = killer;
		this.game = game;
	}

	public static HandlerList getHandlerList() {
		return BedwarsPlayerKilledEvent.handlers;
	}

	/**
	 * @return game
	 */
	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsPlayerKilledEvent.handlers;
	}

	/**
	 * @return killer
	 */
	public Player getKiller() {
		return this.killer;
	}

	/**
	 * @return victim
	 */
	public Player getPlayer() {
		return this.player;
	}

}
