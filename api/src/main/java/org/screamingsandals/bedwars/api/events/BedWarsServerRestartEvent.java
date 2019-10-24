package org.screamingsandals.bedwars.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedWarsServerRestartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return BedWarsServerRestartEvent.handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return BedWarsServerRestartEvent.handlers;
    }
}
