package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface GameEndingEvent<G extends Game, T extends Team> {
    G getGame();

    @Nullable
    T getWinningTeam();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<GameEndingEvent<Game, Team>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, GameEndingEvent.class, (Consumer) consumer);
    }
}
