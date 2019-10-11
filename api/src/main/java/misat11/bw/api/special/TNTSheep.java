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
	LivingEntity getEntity();

	/**
	 * @return
	 */
	Location getInitialLocation();
	
	/**
	 * @return
	 */
	TNTPrimed getTNT();
	
	/**
	 * @return
	 */
	double getSpeed();
	
	/**
	 * @return
	 */
	double getFollowRange();
}
