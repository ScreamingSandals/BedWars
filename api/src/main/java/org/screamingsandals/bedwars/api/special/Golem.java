package org.screamingsandals.bedwars.api.special;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
@ApiStatus.NonExtendable
public interface Golem<G extends Game, P extends BWPlayer, T extends Team, E extends Wrapper> extends SpecialItem<G, P, T> {

    /**
     * @return
     */
    E getEntity();

    /**
     * @return
     */
    double getSpeed();

    /**
     * @return
     */
    double getFollowRange();

    /**
     *
     * @return
     */
    double getHealth();

}
