package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;

public interface PostRebuildingEvent<G extends Game> {
    G getGame();
}
