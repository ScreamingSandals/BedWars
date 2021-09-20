package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface PurchaseFailedEvent<G extends Game, P extends BWPlayer> extends BWCancellable {
    G getGame();

    P getPlayer();

    PurchaseType getType();

    // OnTradeEvent getTradeEvent() - just in class form, not interface

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<PurchaseFailedEvent<Game, BWPlayer>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PurchaseFailedEvent.class, (Consumer) consumer);
    }
}