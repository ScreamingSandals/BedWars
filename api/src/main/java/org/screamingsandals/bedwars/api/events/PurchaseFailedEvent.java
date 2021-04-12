package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

public interface PurchaseFailedEvent<G extends Game, P extends BWPlayer> extends BWCancellable {
    G getGame();

    P getPlayer();

    PurchaseType getType();

    // OnTradeEvent getTradeEvent() - just in class form, not interface
}