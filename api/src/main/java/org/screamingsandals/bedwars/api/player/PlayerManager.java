package org.screamingsandals.bedwars.api.player;

import org.screamingsandals.bedwars.api.game.Game;

import java.util.Optional;
import java.util.UUID;

public interface PlayerManager<P extends BWPlayer, G extends Game<P>> {
    Optional<P> getPlayer(UUID uuid);

    boolean isPlayerInGame(UUID uuid);

    boolean isPlayerRegistered(UUID uuid);

    Optional<G> getGameOfPlayer(UUID uuid);
}
