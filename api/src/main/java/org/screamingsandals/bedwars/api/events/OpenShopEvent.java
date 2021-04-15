package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.function.Consumer;

public interface OpenShopEvent<G extends Game, E extends Wrapper, P extends BWPlayer, S extends GameStore> extends BWCancellable {

    G getGame();

    E getEntity();

    P getPlayer();

    S getGameStore();

    Result getResult();

    void setResult(Result result);

    @Deprecated
    @Override
    default boolean isCancelled() {
        return getResult() != Result.ALLOW;
    }

    @Deprecated
    @Override
    default void setCancelled(boolean cancelled) {
        setResult(cancelled ? Result.DISALLOW_UNKNOWN : Result.ALLOW);
    }

    enum Result {
        ALLOW,
        DISALLOW_THIRD_PARTY_SHOP,
        DISALLOW_LOCKED_FOR_THIS_PLAYER,
        DISALLOW_UNKNOWN;
    }

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<OpenShopEvent<Game, Wrapper, BWPlayer, GameStore>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, OpenShopEvent.class, (Consumer) consumer);
    }
}
