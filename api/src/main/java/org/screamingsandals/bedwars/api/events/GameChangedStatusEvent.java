package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;

public interface GameChangedStatusEvent<G extends Game> {
    G getGame();
}
