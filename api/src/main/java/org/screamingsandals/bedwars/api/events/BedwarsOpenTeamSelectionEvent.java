package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsOpenTeamSelectionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Game game;
    private Player player;

    /**
     * @param game
     * @param player
     */
    public BedwarsOpenTeamSelectionEvent(Game game, Player player) {
        this.player = player;
        this.game = game;
    }

    public static HandlerList getHandlerList() {
        return BedwarsOpenTeamSelectionEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsOpenTeamSelectionEvent.handlers;
    }

    /**
     * @return player
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

}
