package misat11.bw.api.events;

import misat11.bw.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsPlayerJoinEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private String cancelMessage = null;
    private Game game;
    private Player player;

    /**
     * @param game
     * @param player
     */
    public BedwarsPlayerJoinEvent(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPlayerJoinEvent.handlers;
    }

    /**
     * @return
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPlayerJoinEvent.handlers;
    }

    /**
     * @return
     */
    public Player getPlayer() {
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

    /**
     * @return
     */
    public String getCancelMessage() {
        return cancelMessage;
    }

    /**
     * @param cancelMessage
     */
    public void setCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
    }

}
