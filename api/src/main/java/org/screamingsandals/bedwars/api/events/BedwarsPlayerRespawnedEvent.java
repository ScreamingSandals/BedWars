package org.screamingsandals.bedwars.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.screamingsandals.bedwars.api.game.Game;

import java.util.List;

/**
 * @author Bedwars Team
 */
public class BedwarsPlayerRespawnedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private Player player;

    /**
     * @param game
     * @param player
     */
    public BedwarsPlayerRespawnedEvent(Game game, Player player) {
        this.player = player;
        this.game = game;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPlayerRespawnedEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPlayerRespawnedEvent.handlers;
    }


    /**
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }


}
