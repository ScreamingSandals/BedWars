package misat11.bw.api.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;
import misat11.bw.api.GameStore;

/**
 * @author Bedwars Team
 *
 */
public class BedwarsOpenShopEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Entity clickedEntity = null;
	private Game game = null;
	private Player player = null;
	private GameStore store = null;
	private Result result = Result.ALLOW;
	

	/**
	 * @param game
	 * @param player
	 * @param store
	 * @param clickedEntity
	 */
	public BedwarsOpenShopEvent(Game game, Player player, GameStore store, Entity clickedEntity) {
		this.player = player;
		this.game = game;
		this.clickedEntity = clickedEntity;
		this.store = store;
	}

	/**
	 * @return
	 */
	public static HandlerList getHandlerList() {
		return BedwarsOpenShopEvent.handlers;
	}

	/**
	 * @return
	 */
	public Entity getEntity() {
		return this.clickedEntity;
	}

	/**
	 * @return
	 */
	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsOpenShopEvent.handlers;
	}

	/**
	 * @return
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * @return
	 */
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
	
	/**
	 * @return
	 */
	public Result getResult() {
		return result;
	}
	
	/**
	 * @param result
	 */
	public void setResult(Result result) {
		this.result = result;
	}
	
	/**
	 * @author Bedwars Team
	 *
	 */
	public static enum Result {
		ALLOW,
		DISALLOW_THIRD_PARTY_SHOP,
		DISALLOW_LOCKED_FOR_THIS_PLAYER,
		DISALLOW_UNKNOWN;
	}

}
