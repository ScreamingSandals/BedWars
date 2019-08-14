package misat11.bw.api.special;

import org.bukkit.Location;

/**
 * @author Bedwars Team
 *
 */
public interface Trap extends SpecialItem {
	/**
	 * @return
	 */
	public Location getLocation();
	
	/**
	 * @return
	 */
	public boolean isPlaced();
}
