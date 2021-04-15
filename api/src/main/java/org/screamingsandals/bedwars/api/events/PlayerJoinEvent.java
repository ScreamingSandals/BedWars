package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

import java.util.function.Consumer;

public interface PlayerJoinEvent<G extends Game, P extends BWPlayer> extends BWCancellable {
    G getGame();

    P getPlayer();

    String getCancelMessage();

    void setCancelMessage(String cancelMessage);

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<PlayerJoinEvent<Game, BWPlayer>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PlayerJoinEvent.class, (Consumer) consumer);
    }
}
