package misat11.bw.api.events;

import misat11.bw.api.Game;
import misat11.bw.api.RunningTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsPlayerLeaveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private Player player;
    private RunningTeam team;

    /**
     * @param game
     * @param player
     * @param team
     */
    public BedwarsPlayerLeaveEvent(Game game, Player player, RunningTeam team) {
        this.game = game;
        this.player = player;
        this.team = team;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPlayerLeaveEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPlayerLeaveEvent.handlers;
    }

    /**
     * @return player
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

}
