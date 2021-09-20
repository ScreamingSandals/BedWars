package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface ServerRestartEvent {

    static void handle(Object plugin, Consumer<ServerRestartEvent> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, ServerRestartEvent.class, consumer);
    }
}
