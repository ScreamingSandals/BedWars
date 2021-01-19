package org.screamingsandals.bedwars.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.Game;

/**
 * @author Bedwars Team
 */
public class BedwarsPlayerJoinedTeamEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private RunningTeam team;
    private Player player;
    private Game game;
    private RunningTeam prevTeam;

    /**
     * @param team
     * @param player
     * @param game
     * @param prevTeam
     */
    public BedwarsPlayerJoinedTeamEvent(RunningTeam team, Player player, Game game, RunningTeam prevTeam) {
        this.team = team;
        this.player = player;
        this.game = game;
        this.prevTeam = prevTeam;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPlayerJoinedTeamEvent.handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPlayerJoinedTeamEvent.handlers;
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
    public RunningTeam getPreviousTeam() {
        return this.prevTeam;
    }

    /**
     * @return
     */
    public Game getGame() {
        return game;
    }

}
