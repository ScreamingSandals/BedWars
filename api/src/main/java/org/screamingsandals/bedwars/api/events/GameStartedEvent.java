package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;

public interface GameStartedEvent<G extends Game> {
    G getGame();
}
