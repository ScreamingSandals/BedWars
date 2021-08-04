package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

import java.util.function.Consumer;

public interface BedDestroyedMessageSendEvent<G extends Game, P extends BWPlayer, T extends Team> extends BWCancellable {
    G getGame();

    P getVictim();

    @Nullable
    P getDestroyer();

    T getTeam();

    String getStringMessage();

    void setStringMessage(String message);

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<BedDestroyedMessageSendEvent<Game, BWPlayer, Team>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, BedDestroyedMessageSendEvent.class, (Consumer) consumer);
    }
}
