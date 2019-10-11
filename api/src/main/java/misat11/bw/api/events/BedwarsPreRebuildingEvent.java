package misat11.bw.api.events;

import misat11.bw.api.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsPreRebuildingEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Game game;

    /**
     * @param game
     */
    public BedwarsPreRebuildingEvent(Game game) {
        this.game = game;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPreRebuildingEvent.handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPreRebuildingEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

}
