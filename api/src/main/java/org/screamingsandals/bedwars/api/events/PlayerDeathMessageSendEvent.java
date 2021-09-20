package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface PlayerDeathMessageSendEvent<G extends Game, P extends BWPlayer> extends BWCancellable {
    G getGame();

    P getVictim();

    String getStringMessage();

    void setStringMessage(String message);

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<PlayerDeathMessageSendEvent<Game, BWPlayer>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PlayerDeathMessageSendEvent.class, (Consumer) consumer);
    }
}
