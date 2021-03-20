package org.screamingsandals.bedwars.api.events;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.lib.material.Item;
import org.screamingsandals.simpleinventories.events.OnTradeEvent;

@RequiredArgsConstructor
public class BedwarsStorePrePurchaseEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final OnTradeEvent tradeEvent;
    private final PurchaseType type;
    private final Item materialItem;
    private final ItemSpawnerType spawnerType;
    private final Item newItem;

    private boolean cancelled;

    /**
     *
     * @return
     */
    public OnTradeEvent getTradeEvent() {
        return tradeEvent;
    }

    /**
     *
     * @return
     */
    public PurchaseType getType() {
        return type;
    }

    /**
     *
     * @return
     */
    public Item getMaterialItem() {
        return materialItem;
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
        return BedwarsStorePrePurchaseEvent.handlerList;
    }
}
