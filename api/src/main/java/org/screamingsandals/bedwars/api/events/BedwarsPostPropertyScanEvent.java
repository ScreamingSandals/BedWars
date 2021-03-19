package org.screamingsandals.bedwars.api.events;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.simpleinventories.events.ItemRenderEvent;

/**
 * @author Bedwars Team
 *
 */
@RequiredArgsConstructor
public class BedwarsPostPropertyScanEvent extends Event  {
    private static final HandlerList handlerList = new HandlerList();
    private final ItemRenderEvent event;

    /**
     *
     * @return
     */
    public ItemRenderEvent getEvent() {
        return event;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPostPropertyScanEvent.handlerList;
    }
}
