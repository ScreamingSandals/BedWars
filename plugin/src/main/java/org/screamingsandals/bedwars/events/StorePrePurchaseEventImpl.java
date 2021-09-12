package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.bedwars.api.events.StorePrePurchaseEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.ItemSpawnerType;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.simpleinventories.events.OnTradeEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class StorePrePurchaseEventImpl extends CancellableAbstractEvent implements StorePrePurchaseEvent<GameImpl, BedWarsPlayer, Item, ItemSpawnerType> {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final Item materialItem;
    private final Item newItem;
    private final ItemSpawnerType spawnerType;
    private final PurchaseType type;
    private final OnTradeEvent tradeEvent; // for sba
}
