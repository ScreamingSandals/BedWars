package misat11.bw.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;
import misat11.bw.api.upgrades.Upgrade;
import misat11.bw.api.upgrades.UpgradeStorage;

/**
 * @author Bedwars Team
 *
 */
public class BedwarsUpgradeUnregisteredEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private UpgradeStorage storage = null;
	private Upgrade upgrade = null;

	/**
	 * @param game
	 * @param storage
	 * @param upgrade
	 */
	public BedwarsUpgradeUnregisteredEvent(Game game, UpgradeStorage storage, Upgrade upgrade) {
		this.game = game;
		this.storage = storage;
		this.upgrade = upgrade;
	}

	public static HandlerList getHandlerList() {
		return BedwarsUpgradeUnregisteredEvent.handlers;
	}

	/**
	 * @return game
	 */
	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsUpgradeUnregisteredEvent.handlers;
	}

	/**
	 * @return upgrade
	 */
	public Upgrade getUpgrade() {
		return upgrade;
	}

	/**
	 * @return storage of this upgrades type
	 */
	public UpgradeStorage getStorage() {
		return storage;
	}
}
