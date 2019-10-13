package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsGameStartEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Game game;

    /**
     * @param game
     */
    public BedwarsGameStartEvent(Game game) {
        this.game = game;
    }

    public static HandlerList getHandlerList() {
        return BedwarsGameStartEvent.handlers;
    }

    /**
     * @return
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsGameStartEvent.handlers;
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
