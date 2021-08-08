package org.screamingsandals.bedwars.api.special;

import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
public interface WarpPowder<G extends Game, P extends BWPlayer, T extends Team, I extends Wrapper> extends SpecialItem<G, P, T> {
    /**
     * @param unregisterSpecial
     * @param showMessage
     */
    void cancelTeleport(boolean unregisterSpecial, boolean showMessage);

    /**
     * @return
     */
    I getItem();

    /**
     *
     */
    void runTask();
}
