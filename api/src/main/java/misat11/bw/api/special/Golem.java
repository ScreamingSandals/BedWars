package misat11.bw.api.special;

import org.bukkit.entity.LivingEntity;

/**
 * @author Bedwars Team
 *
 */
public interface Golem extends SpecialItem {

	/**
	 * @return
	 */
	public LivingEntity getEntity();

	/**
	 * @return
	 */
	public double getSpeed();

	/**
	 * @return
	 */
	public double getFollowRange();

}
