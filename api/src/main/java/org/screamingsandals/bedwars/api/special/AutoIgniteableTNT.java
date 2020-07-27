package org.screamingsandals.bedwars.api.special;

import org.bukkit.Location;

/**
 * @author Bedwars Team
 */
public interface AutoIgniteableTNT extends SpecialItem {

    /**
     * @return explosion time in seconds
     */
    int getExplosionTime();

    /**
     * @return true - tnt will damage placer
     */
    boolean isAllowedDamagingPlacer();
    
    /**
     * @return spawn an entity tnt
     */
    void spawn(Location location);
    
}
