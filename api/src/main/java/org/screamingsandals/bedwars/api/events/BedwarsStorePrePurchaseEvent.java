package org.screamingsandals.bedwars.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.simpleinventories.events.OnTradeEvent;

@RequiredArgsConstructor
public class BedwarsStorePrePurchaseEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    @Getter private final OnTradeEvent event;
    @Getter private final PurchaseType type;
    private boolean cancelled;

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
        return BedwarsStorePrePurchaseEvent.handlerList;
    }
}
