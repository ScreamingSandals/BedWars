package misat11.bw.api.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;
import misat11.bw.api.GameStore;

public class BedwarsOpenShopEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Entity clickedEntity = null;
	private Game game = null;
	private Player player = null;
	private GameStore store = null;
	private Result result = Result.ALLOW;
	

	public BedwarsOpenShopEvent(Game game, Player player, GameStore store, Entity clickedEntity) {
		this.player = player;
		this.game = game;
		this.clickedEntity = clickedEntity;
		this.store = store;
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

	public Player getPlayer() {
		return this.player;
	}
	
	public GameStore getStore() {
		return this.store;
	}

	@Deprecated
	@Override
	public boolean isCancelled() {
		return result != Result.ALLOW;
	}

	@Deprecated
	@Override
	public void setCancelled(boolean cancel) {
		result = cancel ? Result.DISALLOW_UNKNOWN : Result.ALLOW;
	}
	
	public Result getResult() {
		return result;
	}
	
	public void setResult(Result result) {
		this.result = result;
	}
	
	public static enum Result {
		ALLOW,
		DISALLOW_THIRD_PARTY_SHOP,
		DISALLOW_LOCKED_FOR_THIS_PLAYER,
		DISALLOW_UNKNOWN;
	}

}
