package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface PostRebuildingEvent<G extends Game> {
    G getGame();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<PostRebuildingEvent<Game>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PostRebuildingEvent.class, (Consumer) consumer);
    }
}
