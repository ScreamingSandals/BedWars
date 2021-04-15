package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.BedwarsAPI;

import java.util.function.Consumer;

public interface ServerRestartEvent {

    static void handle(Object plugin, Consumer<ServerRestartEvent> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, ServerRestartEvent.class, consumer);
    }
}
