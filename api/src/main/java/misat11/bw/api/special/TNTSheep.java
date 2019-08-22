package misat11.bw.api.special;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;

/**
 * @author Bedwars Team
 *
 */
public interface TNTSheep extends SpecialItem {
	/**
	 * @return
	 */
	public LivingEntity getEntity();

	/**
	 * @return
	 */
	public Location getInitialLocation();
	
	/**
	 * @return
	 */
	public TNTPrimed getTNT();
	
	/**
	 * @return
	 */
	public double getSpeed();
	
	/**
	 * @return
	 */
	public double getFollowRange();
}
