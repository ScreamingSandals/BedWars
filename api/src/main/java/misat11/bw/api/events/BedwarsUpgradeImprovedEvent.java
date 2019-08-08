package misat11.bw.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;
import misat11.bw.api.upgrades.Upgrade;
import misat11.bw.api.upgrades.UpgradeStorage;

public class BedwarsUpgradeImprovedEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private UpgradeStorage storage = null;
	private Upgrade upgrade = null;
	private double oldLevel = 0;
	private double newLevel = 0;

	public BedwarsUpgradeImprovedEvent(Game game, UpgradeStorage storage, Upgrade upgrade, double oldLevel, double newLevel) {
		this.game = game;
		this.storage = storage;
		this.upgrade = upgrade;
		this.oldLevel = oldLevel;
		this.newLevel = newLevel;
		upgrade.setLevel(newLevel);
	}

	public static HandlerList getHandlerList() {
		return BedwarsUpgradeImprovedEvent.handlers;
	}

	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsUpgradeImprovedEvent.handlers;
	}

	@Override
	public boolean isCancelled() {
		return upgrade.getLevel() == oldLevel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		upgrade.setLevel(cancel ? oldLevel : newLevel);
	}
	
	public Upgrade getUpgrade() {
		return upgrade;
	}
	
	public UpgradeStorage getStorage() {
		return storage;
	}
	
	public double getNewLevel() {
		return upgrade.getLevel();
	}
	
	public double getOldLevel() {
		return oldLevel;
	}
	
	public double getOriginalNewLevel() {
		return newLevel;
	}
	
	public void setNewLevel(double newLevel) {
		upgrade.setLevel(newLevel);
	}

}
