package org.screamingsandals.bedwars.api.special;

import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

/**
 * @author ScreamingSandals
 */
public interface SpecialItem<G extends Game, P extends BWPlayer, T extends Team> {
    /**
     * @return game where this special item is used
     */
    G getGame();

    /**
     * @return the player who activated this special item
     */
    P getPlayer();

    /**
     * @return the team of player who activated this item
     */
    T getTeam();
}
