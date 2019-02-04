package misat11.bw.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;

public class BedwarsPostRebuildingEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private Game game;
	
	public BedwarsPostRebuildingEvent(Game game) {
		this.game = game;
	}

	public static HandlerList getHandlerList() {
		return BedwarsPostRebuildingEvent.handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsPostRebuildingEvent.handlers;
	}
	
	public Game getGame() {
		return this.game;
	}

}