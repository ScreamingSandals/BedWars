package misat11.bw.api.events;

import misat11.bw.api.game.Game;
import misat11.bw.api.RunningTeam;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * @author Bedwars Team
 */
public class BedwarsPlayerBuildBlock extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private Player player;
    private RunningTeam team;
    private Block block;
    private BlockState replaced;
    private boolean cancel = false;
    private ItemStack itemInHand;

    /**
     * @param game
     * @param player
     * @param team
     * @param block
     * @param itemInHand
     * @param replaced
     */
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

    /**
     * @return
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPlayerBuildBlock.handlers;
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
    public RunningTeam getTeam() {
        return this.team;
    }

    /**
     * @return
     */
    public Block getBlock() {
        return this.block;
    }

    /**
     * @return
     */
    public BlockState getReplaced() {
        return this.replaced;
    }

    /**
     * @return
     */
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
