package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;

public interface PreRebuildingEvent<G extends Game> {
    G getGame();
}
