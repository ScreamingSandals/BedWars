package misat11.bw.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;
import misat11.bw.api.upgrades.Upgrade;
import misat11.bw.api.upgrades.UpgradeStorage;

public class BedwarsUpgradeRegisteredEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private UpgradeStorage storage = null;
	private Upgrade upgrade = null;

	public BedwarsUpgradeRegisteredEvent(Game game, UpgradeStorage storage, Upgrade upgrade) {
		this.game = game;
		this.storage = storage;
		this.upgrade = upgrade;
	}

	public static HandlerList getHandlerList() {
		return BedwarsUpgradeRegisteredEvent.handlers;
	}

	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsUpgradeRegisteredEvent.handlers;
	}
	
	public Upgrade getUpgrade() {
		return upgrade;
	}
	
	public UpgradeStorage getStorage() {
		return storage;
	}

}
