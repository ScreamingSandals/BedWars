package misat11.bw.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;
import misat11.bw.api.ItemSpawner;
import misat11.bw.api.Team;

@Deprecated
public class BedwarsApplyUpgradeEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private Team team = null;
	private Player player = null;
	private ItemSpawner spawner = null;
	private int newLevel = 1;
	private boolean cancel = false;

	public BedwarsApplyUpgradeEvent(Game game, Player player, Team team, ItemSpawner spawner, int newLevel) {
		this.game = game;
		this.team = team;
		this.player = player;
		this.spawner = spawner;
		this.newLevel = newLevel;
	}

	public static HandlerList getHandlerList() {
		return BedwarsApplyUpgradeEvent.handlers;
	}

	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsApplyUpgradeEvent.handlers;
	}

	public int getNewLevel() {
		return newLevel;
	}

	public void setNewLevel(int newLevel) {
		this.newLevel = newLevel;
	}

	public Team getTeam() {
		return team;
	}

	public Player getPlayer() {
		return player;
	}

	public ItemSpawner getSpawner() {
		return spawner;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	

}
