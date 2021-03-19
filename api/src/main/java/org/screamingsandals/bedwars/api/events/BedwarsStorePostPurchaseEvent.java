package org.screamingsandals.bedwars.api.events;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.simpleinventories.events.OnTradeEvent;

/**
 * @author Bedwars Team
 *
 */
@RequiredArgsConstructor
public class BedwarsStorePostPurchaseEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final OnTradeEvent event;
    private final PurchaseType type;
    private boolean cancelled;

    /**
     *
     * @return
     */
    public OnTradeEvent getEvent() {
        return event;
    }

    /**
     *
     * @return
     */
    public PurchaseType getType() {
        return type;
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
        return BedwarsStorePostPurchaseEvent.handlerList;
    }
}
