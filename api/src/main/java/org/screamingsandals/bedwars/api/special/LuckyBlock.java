package org.screamingsandals.bedwars.api.special;

import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
public interface LuckyBlock<G extends Game, P extends BWPlayer, T extends Team, L extends Wrapper> extends SpecialItem<G, P, T> {
    /**
     * @return
     */
    boolean isPlaced();

    /**
     * @return
     */
    L getBlockLocation();
}
