package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;

public interface GameStartEvent<G extends Game> extends BWCancellable {
    G getGame();
}
