package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

import java.util.function.Consumer;

public interface PlayerJoinedEvent<G extends Game, P extends BWPlayer, T extends RunningTeam> {
    G getGame();

    P getPlayer();

    @Nullable
    T getTeam();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<PlayerJoinedEvent<Game, BWPlayer, RunningTeam>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PlayerJoinedEvent.class, (Consumer) consumer);
    }
}
