package misat11.bw.api.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import misat11.bw.api.Game;
import misat11.bw.api.RunningTeam;

public class BedwarsPlayerBuildBlock extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private Player player = null;
	private RunningTeam team = null;
	private Block block = null;
	private BlockState replaced = null;
	private boolean cancel = false;
	private ItemStack itemInHand = null;

	public BedwarsPlayerBuildBlock(Game game, Player player, RunningTeam team, Block block, ItemStack itemInHand, BlockState replaced) {
		this.game = game;
		this.player = player;
		this.team = team;
		this.block = block;
		this.replaced = replaced;
		this.itemInHand = itemInHand;
	}

	public static HandlerList getHandlerList() {
		return BedwarsPlayerBuildBlock.handlers;
	}

	public Game getGame() {
		return this.game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsPlayerBuildBlock.handlers;
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

	public BlockState getReplaced() {
		return this.replaced;
	}
	
	public ItemStack getItemInHand() {
		return this.itemInHand;
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
