package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

public interface TargetBlockDestroyedEvent<G extends Game, P extends BWPlayer, T extends RunningTeam> {
    G getGame();

    @Nullable
    P getBroker();

    T getTeam();

    default boolean wasDestroyedByExplosion() {
        return getBroker() == null;
    }
}
