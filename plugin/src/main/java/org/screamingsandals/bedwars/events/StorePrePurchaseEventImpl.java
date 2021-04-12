package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.bedwars.api.events.StorePostPurchaseEvent;
import org.screamingsandals.bedwars.api.events.StorePrePurchaseEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.ItemSpawnerType;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.lib.material.Item;
import org.screamingsandals.simpleinventories.events.OnTradeEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class StorePrePurchaseEventImpl extends CancellableAbstractEvent implements StorePrePurchaseEvent<Game, BedWarsPlayer, Item, ItemSpawnerType> {
    private final Game game;
    private final BedWarsPlayer player;
    private final Item materialItem;
    private final Item newItem;
    private final ItemSpawnerType spawnerType;
    private final PurchaseType type;
    private final OnTradeEvent tradeEvent; // for sba
}
