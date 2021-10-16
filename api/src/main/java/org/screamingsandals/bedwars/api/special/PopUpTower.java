package org.screamingsandals.bedwars.api.special;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

@ApiStatus.NonExtendable
public interface PopUpTower<G extends Game, P extends BWPlayer, T extends Team> extends SpecialItem<G, P, T> {

    /**
     * <p>Gets the bridge material.</p>
     *
     * @return the bridge material
     */
    Wrapper getMaterial();

    Wrapper getCenterPoint();

    /**
     * <p>Runs the placing task.</p>
     */
    void runTask();
}
