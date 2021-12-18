package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.bedwars.api.events.StorePrePurchaseEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.ItemSpawnerTypeImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SCancellableEvent;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.simpleinventories.events.OnTradeEvent;

@Data
public class StorePrePurchaseEventImpl implements StorePrePurchaseEvent<GameImpl, BedWarsPlayer, Item, ItemSpawnerTypeImpl>, SCancellableEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final Item materialItem;
    private final Item newItem;
    private final ItemSpawnerTypeImpl spawnerType;
    private final PurchaseType type;
    private final OnTradeEvent tradeEvent; // for sba
    private boolean cancelled;
}
