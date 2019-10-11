package misat11.bw.api.events;

import misat11.bw.api.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Bedwars Team
 */
public class BedwarsPlayerKilledEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private Player killer;
    private Player player;
    private List<ItemStack> drops;

    /**
     * @param game
     * @param player
     * @param killer
     * @param drops
     */
    public BedwarsPlayerKilledEvent(Game game, Player player, Player killer, List<ItemStack> drops) {
        this.player = player;
        this.killer = killer;
        this.game = game;
        this.drops = drops;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPlayerKilledEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPlayerKilledEvent.handlers;
    }

    /**
     * @return killer
     */
    public Player getKiller() {
        return this.killer;
    }

    /**
     * @return victim
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return victim's drops
     */
    public List<ItemStack> getDrops() {
        return this.drops;
    }

}
