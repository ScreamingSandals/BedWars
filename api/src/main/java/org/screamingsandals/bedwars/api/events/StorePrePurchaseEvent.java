package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.function.Consumer;

public interface StorePrePurchaseEvent<G extends Game, P extends BWPlayer, I extends Wrapper, T extends ItemSpawnerType> extends BWCancellable {
    G getGame();

    P getPlayer();

    I getMaterialItem();

    I getNewItem();

    T getSpawnerType();

    PurchaseType getType();

    // OnTradeEvent getTradeEvent() - just in class form, not interface

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<StorePrePurchaseEvent<Game, BWPlayer, Wrapper, ItemSpawnerType>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, StorePrePurchaseEvent.class, (Consumer) consumer);
    }
}
