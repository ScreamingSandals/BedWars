package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.Game;

import java.util.function.Consumer;

public interface GameEndingEvent<G extends Game, T extends RunningTeam> {
    G getGame();

    @Nullable
    T getWinningTeam();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<GameEndingEvent<Game, RunningTeam>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, GameEndingEvent.class, (Consumer) consumer);
    }
}
