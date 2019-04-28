package misat11.bw.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;
import misat11.bw.api.RunningTeam;

public class BedwarsPlayerBreakBlock extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private Player player = null;
	private RunningTeam team = null;
	private Block block = null;
	private boolean cancel = false;
	private boolean drops = true;

	public BedwarsPlayerBreakBlock(Game game, Player player, RunningTeam team, Block block) {
		this.game = game;
		this.player = player;
		this.team = team;
		this.block = block;
	}

	public static HandlerList getHandlerList() {
		return BedwarsPlayerBreakBlock.handlers;
	}

	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsPlayerBreakBlock.handlers;
	}

	public Player getPlayer() {
		return this.player;
	}

	public RunningTeam getTeam() {
		return this.team;
	}

	public Block getBlock() {
		return this.block;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public boolean isDrops() {
		return drops;
	}

	public void setDrops(boolean drops) {
		this.drops = drops;
	}

}
