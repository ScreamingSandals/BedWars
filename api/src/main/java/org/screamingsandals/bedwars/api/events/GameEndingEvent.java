package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.Game;

public interface GameEndingEvent<G extends Game, T extends RunningTeam> {
    G getGame();

    @Nullable
    T getWinningTeam();
}
