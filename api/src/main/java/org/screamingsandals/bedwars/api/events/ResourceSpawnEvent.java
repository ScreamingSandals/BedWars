package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.ItemSpawner;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.lib.utils.Wrapper;

public interface ResourceSpawnEvent<G extends Game, S extends ItemSpawner, T extends ItemSpawnerType, I extends Wrapper, L extends Wrapper> extends BWCancellable {
    G getGame();

    S getItemSpawner();

    L getLocation();

    I getResource();

    T getType();

    /**
     *
     * @param resource wrapper or platform ItemStack
     */
    void setResource(Object resource);
}
