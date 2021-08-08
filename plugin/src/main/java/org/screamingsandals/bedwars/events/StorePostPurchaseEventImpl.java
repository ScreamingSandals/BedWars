package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.bedwars.api.events.StorePostPurchaseEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.simpleinventories.events.OnTradeEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class StorePostPurchaseEventImpl extends CancellableAbstractEvent implements StorePostPurchaseEvent<GameImpl, BedWarsPlayer> {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final PurchaseType type;
    private final OnTradeEvent tradeEvent; // for sba
}
