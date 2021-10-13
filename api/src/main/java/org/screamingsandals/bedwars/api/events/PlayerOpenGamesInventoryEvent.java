package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface PlayerOpenGamesInventoryEvent<P extends Wrapper> extends BWCancellable {
    P getPlayer();

    String getType();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<PlayerOpenGamesInventoryEvent<Wrapper>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PlayerOpenGamesInventoryEvent.class, (Consumer) consumer);
    }
}
