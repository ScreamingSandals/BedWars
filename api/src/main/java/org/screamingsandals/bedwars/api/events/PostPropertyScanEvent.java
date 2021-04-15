package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.BedwarsAPI;

import java.util.function.Consumer;

public interface PostPropertyScanEvent extends BWCancellable {

    // ItemRenderEvent getEvent() - just in class form, not interface

    static void handle(Object plugin, Consumer<PostPropertyScanEvent> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PostPropertyScanEvent.class, consumer);
    }
}
