package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface TargetBlockDestroyedEvent<G extends Game, P extends BWPlayer, T extends Team> {
    G getGame();

    @Nullable
    P getBroker();

    T getTeam();

    default boolean wasDestroyedByExplosion() {
        return getBroker() == null;
    }

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<TargetBlockDestroyedEvent<Game, BWPlayer, Team>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, TargetBlockDestroyedEvent.class, (Consumer) consumer);
    }
}
