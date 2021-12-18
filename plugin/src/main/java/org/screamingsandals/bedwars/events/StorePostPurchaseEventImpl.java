package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.bedwars.api.events.StorePostPurchaseEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SCancellableEvent;
import org.screamingsandals.simpleinventories.events.OnTradeEvent;

@Data
public class StorePostPurchaseEventImpl implements StorePostPurchaseEvent<GameImpl, BedWarsPlayer>, SCancellableEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final PurchaseType type;
    private final OnTradeEvent tradeEvent; // for sba
    private boolean cancelled;
}
