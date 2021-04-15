package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;

import java.util.function.Consumer;

public interface GameStartedEvent<G extends Game> {
    G getGame();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<GameStartedEvent<Game>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, GameStartedEvent.class, (Consumer) consumer);
    }
}
