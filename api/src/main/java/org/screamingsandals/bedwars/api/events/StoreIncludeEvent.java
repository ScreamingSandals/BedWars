package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;

import java.nio.file.Path;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface StoreIncludeEvent extends BWCancellable {
    String getName();

    Path getPath();

    boolean isUseParent();

    // CategoryBuilder getCategoryBuilder() - just in class form, not interface

    static void handle(Object plugin, Consumer<StoreIncludeEvent> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, StoreIncludeEvent.class, consumer);
    }
}
