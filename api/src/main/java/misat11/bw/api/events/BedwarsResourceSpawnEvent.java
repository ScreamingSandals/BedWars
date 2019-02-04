package misat11.bw.api.events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import misat11.bw.api.Game;

public class BedwarsResourceSpawnEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Game game = null;
	private Location location = null;
	private ItemStack resource = null;

	public BedwarsResourceSpawnEvent(Game game, Location location, ItemStack resource) {
		this.game = game;
		this.location = location;
		this.resource = resource;
	}

	public static HandlerList getHandlerList() {
		return BedwarsResourceSpawnEvent.handlers;
	}

	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsResourceSpawnEvent.handlers;
	}

	public Location getLocation() {
		return this.location;
	}

	public ItemStack getResource() {
		return this.resource;
	}

	public void setResource(ItemStack resource) {
		this.resource = resource;
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
