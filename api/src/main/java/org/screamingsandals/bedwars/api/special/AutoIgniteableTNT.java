package org.screamingsandals.bedwars.api.special;

import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

/**
 * @author Bedwars Team
 */
public interface AutoIgniteableTNT<G extends Game, P extends BWPlayer, T extends Team> extends SpecialItem<G, P, T> {

    /**
     * @return explosion time in seconds
     */
    int getExplosionTime();

    /**
     * @return true - tnt will damage placer
     */
    boolean isAllowedDamagingPlacer();
    
    /**
     * spawn an entity tnt
     */
    void spawn(Object location);
    
}
