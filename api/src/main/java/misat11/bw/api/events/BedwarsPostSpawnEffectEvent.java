package misat11.bw.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;

/**
 * @author Bedwars Team
 *
 */
public class BedwarsPostSpawnEffectEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private Player player = null;
	private String effectsGroupName = null;

	/**
	 * @param game
	 * @param player
	 * @param effectsGroupName
	 */
	public BedwarsPostSpawnEffectEvent(Game game, Player player, String effectsGroupName) {
		this.game = game;
		this.player = player;
		this.effectsGroupName = effectsGroupName;
	}

	public static HandlerList getHandlerList() {
		return BedwarsPostSpawnEffectEvent.handlers;
	}

	/**
	 * @return game
	 */
	public Game getGame() {
		return this.game;
	}
	
	/**
	 * @return player
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * @return effects group
	 */
	public String getEffectsGroupName() {
		return this.effectsGroupName;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsPostSpawnEffectEvent.handlers;
	}

}
