package org.screamingsandals.bedwars.api.special;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.List;

/**
 * @author Bedwars Team
 */
@ApiStatus.NonExtendable
public interface RescuePlatform<G extends Game, P extends BWPlayer, T extends Team, I extends Wrapper, M extends Wrapper, B extends Wrapper> extends SpecialItem<G, P, T> {
    /**
     * @return
     */
    int getBreakingTime();

    /**
     * @return
     */
    boolean isBreakable();

    /**
     * @return
     */
    M getMaterial();

    /**
     * @return
     */
    I getItem();

    /**
     *
     */
    void runTask();

    /**
     * @return
     */
    List<B> getPlatformBlocks();
}
