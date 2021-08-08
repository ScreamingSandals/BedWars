package org.screamingsandals.bedwars.api.entities;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.lib.utils.Wrapper;

public interface GameEntity<G extends Game<?>, E extends Wrapper> {
    G getGame();

    E getEntity();
}
