package misat11.bw.api.events;

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
    private Player player;
    private RunningTeam team;
    private RunningTeam prevTeam;

    /**
     * @param team
     * @param player
     * @param prevTeam
     */
    public BedwarsPlayerJoinTeamEvent(RunningTeam team, Player player, RunningTeam prevTeam) {
        this.player = player;
        this.team = team;
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

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
