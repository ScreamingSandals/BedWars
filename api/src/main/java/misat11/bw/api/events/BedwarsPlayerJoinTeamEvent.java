package misat11.bw.api.events;

import misat11.bw.api.game.Game;
import misat11.bw.api.RunningTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsPlayerJoinTeamEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
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
    public BedwarsPlayerJoinTeamEvent(RunningTeam team, Player player, Game game, RunningTeam prevTeam) {
        this.team = team;
        this.player = player;
        this.game = game;
        this.prevTeam = prevTeam;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPlayerJoinTeamEvent.handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPlayerJoinTeamEvent.handlers;
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

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
