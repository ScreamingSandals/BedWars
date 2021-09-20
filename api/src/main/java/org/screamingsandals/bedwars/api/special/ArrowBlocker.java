package org.screamingsandals.bedwars.api.special;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

/**
 * @author Bedwars Team
 */
@ApiStatus.NonExtendable
public interface ArrowBlocker<G extends Game, P extends BWPlayer, T extends Team> extends SpecialItem<G, P, T> {
    /**
     * @return
     */
    int getProtectionTime();

    /**
     * @return
     */
    int getUsedTime();

    /**
     * @return
     */
    boolean isActivated();

    /**
     *
     */
    void runTask();
}
