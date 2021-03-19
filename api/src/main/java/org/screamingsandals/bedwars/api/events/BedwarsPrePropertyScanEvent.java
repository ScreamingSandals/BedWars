package org.screamingsandals.bedwars.api.events;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.simpleinventories.events.ItemRenderEvent;

/**
 * @author Bedwars Team
 *
 */
@RequiredArgsConstructor
public class BedwarsPrePropertyScanEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final ItemRenderEvent event;
    private boolean cancelled;

    /**
     *
     * @return
     */
    public ItemRenderEvent getEvent() {
        return event;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPrePropertyScanEvent.handlerList;
    }
}
