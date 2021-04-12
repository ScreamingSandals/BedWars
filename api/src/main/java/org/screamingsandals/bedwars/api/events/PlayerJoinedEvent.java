package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

public interface PlayerJoinedEvent<G extends Game, P extends BWPlayer, T extends RunningTeam> {
    G getGame();

    P getPlayer();

    @Nullable
    T getTeam();
}
