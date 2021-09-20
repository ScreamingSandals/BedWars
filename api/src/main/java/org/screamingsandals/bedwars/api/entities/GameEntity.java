package org.screamingsandals.bedwars.api.entities;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.lib.utils.Wrapper;

@ApiStatus.NonExtendable
public interface GameEntity<G extends Game<?, ?, ?, ?, ?, ?, ?, ?, ?>, E extends Wrapper> {
    G getGame();

    E getEntity();
}
