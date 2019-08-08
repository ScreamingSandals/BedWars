package misat11.bw.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;
import misat11.bw.api.upgrades.Upgrade;
import misat11.bw.api.upgrades.UpgradeStorage;

public class BedwarsUpgradeUnregisteredEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private UpgradeStorage storage = null;
	private Upgrade upgrade = null;

	public BedwarsUpgradeUnregisteredEvent(Game game, UpgradeStorage storage, Upgrade upgrade) {
		this.game = game;
		this.storage = storage;
		this.upgrade = upgrade;
	}


	public static HandlerList getHandlerList() {
		return BedwarsUpgradeUnregisteredEvent.handlers;
	}

	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsUpgradeUnregisteredEvent.handlers;
	}
	
	public Upgrade getUpgrade() {
		return upgrade;
	}

	public UpgradeStorage getStorage() {
		return storage;
	}
}
