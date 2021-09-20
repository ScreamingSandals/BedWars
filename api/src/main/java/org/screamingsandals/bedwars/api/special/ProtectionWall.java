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
public interface ProtectionWall<G extends Game, P extends BWPlayer, T extends Team, M extends Wrapper, B extends Wrapper> extends SpecialItem<G, P, T> {
    /**
     * @return
     */
    int getBreakingTime();

    /**
     * @return
     */
    int getWidth();

    /**
     * @return
     */
    int getHeight();

    /**
     * @return
     */
    int getDistance();

    /**
     * @return
     */
    boolean isBreakable();

    /**
     * @return
     */
    M getMaterial();

    /**
     *
     */
    void runTask();

    /**
     * @return
     */
    List<B> getWallBlocks();
}
