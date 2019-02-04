package misat11.bw.api.events;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;

public class BedwarsOpenShopEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Entity clickedEntity = null;
	private Game game = null;
	private Player player = null;

	public BedwarsOpenShopEvent(Game game, Player player, Entity clickedEntity) {
		this.player = player;
		this.game = game;
		this.clickedEntity = clickedEntity;
	}

	public static HandlerList getHandlerList() {
		return BedwarsOpenShopEvent.handlers;
	}

	public Entity getEntity() {
		return this.clickedEntity;
	}

	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsOpenShopEvent.handlers;
	}

	public CommandSender getPlayer() {
		return this.player;
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
