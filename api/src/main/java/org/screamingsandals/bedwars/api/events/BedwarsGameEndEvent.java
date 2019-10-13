package org.screamingsandals.bedwars.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.screamingsandals.bedwars.api.game.Game;

/**
 * @author Bedwars Team
 *
 */
public class BedwarsGameEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Game game;

	/**
	 * @param game
	 */
	public BedwarsGameEndEvent(Game game) {
		this.game = game;
	}

	public static HandlerList getHandlerList() {
		return BedwarsGameEndEvent.handlers;
	}

	/**
	 * @return game
	 */
	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsGameEndEvent.handlers;
	}

}
