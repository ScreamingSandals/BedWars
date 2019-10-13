package org.screamingsandals.bedwars.api.special;

import org.bukkit.Location;

/**
 * @author Bedwars Team
 */
public interface Trap extends SpecialItem {
    /**
     * @return
     */
	Location getLocation();

    /**
     * @return
     */
	boolean isPlaced();
}
