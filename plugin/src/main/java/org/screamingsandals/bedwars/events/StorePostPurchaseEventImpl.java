package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.bedwars.api.events.StorePostPurchaseEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.simpleinventories.events.OnTradeEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class StorePostPurchaseEventImpl extends CancellableAbstractEvent implements StorePostPurchaseEvent<Game, BedWarsPlayer> {
    private final Game game;
    private final BedWarsPlayer player;
    private final PurchaseType type;
    private final OnTradeEvent tradeEvent; // for sba
}
