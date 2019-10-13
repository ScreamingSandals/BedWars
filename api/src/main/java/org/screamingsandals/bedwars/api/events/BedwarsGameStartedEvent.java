package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsGameStartedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Game game;

    /**
     * @param game
     */
    public BedwarsGameStartedEvent(Game game) {
        this.game = game;
    }

    public static HandlerList getHandlerList() {
        return BedwarsGameStartedEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsGameStartedEvent.handlers;
    }

}
