package org.screamingsandals.bedwars.api.special;

import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

public interface BridgeEgg<G extends Game, P extends BWPlayer, T extends Team, E extends Wrapper, M extends Wrapper> extends SpecialItem<G, P, T> {
    /**
     * <p>Gets the bridge egg projectile.</p>
     *
     * @return the bridge egg projectile
     */
    E getProjectile();

    /**
     * <p>Gets the bridge material.</p>
     *
     * @return the bridge material
     */
    M getMaterial();

    /**
     * <p>Gets the bridge's max distance.</p>
     *
     * @return the bridge max distance
     */
    double getDistance();

    /**
     * <p>Runs the placing task.</p>
     */
    void runTask();
}
