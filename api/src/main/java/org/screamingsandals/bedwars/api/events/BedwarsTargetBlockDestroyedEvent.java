package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsTargetBlockDestroyedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private Player player;
    private RunningTeam team;

    /**
     * @param game
     * @param player
     * @param team
     */
    public BedwarsTargetBlockDestroyedEvent(Game game, Player player, RunningTeam team) {
        this.player = player;
        this.team = team;
        this.game = game;
    }

    public static HandlerList getHandlerList() {
        return BedwarsTargetBlockDestroyedEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsTargetBlockDestroyedEvent.handlers;
    }

    /**
     * @return player or null if target block has been destroyed by explosion
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return team of player
     */
    public RunningTeam getTeam() {
        return this.team;
    }

    /**
     * @return true if block has been destroyed by explosion
     */
    public boolean destroyedByExplosion() {
        return this.player == null;
    }

}
