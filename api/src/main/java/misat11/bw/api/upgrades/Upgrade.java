package misat11.bw.api.upgrades;

import misat11.bw.api.Game;
import misat11.bw.api.Team;

/**
 * @author Bedwars Team
 *
 */
public interface Upgrade {
	
	/**
	 * 
	 * @return registered name of this upgrade
	 */
	public String getName();
	
	/**
	 * 
	 * @return identificator of this upgrade instance
	 */
	public String getInstanceName();
	
	/**
	 * 
	 * @return current level of upgrade
	 */
	public double getLevel();
	
	/**
	 * Sets level of this upgrade
	 * 
	 * @param level Current level
	 */
	public void setLevel(double level);
	
	/**
	 * Add levels to this upgrade
	 * 
	 * @param level Levels that will be added to current level
	 */
	public void increaseLevel(double level);
	
	/**
	 * 
	 * @return initial level of upgrade
	 */
	public double getInitialLevel();

	/**
	 * Sets team of this upgrade
	 *
	 * @param team current team
	 */
	void setTeam(Team team);


	/**
	 *
	 * @return registered team for this upgrade
	 */
	Team getTeam();

	/**
	 * Called when upgrade is registered
	 * 
	 * @param game Game when upgrade is activated
	 */
	default void onUpgradeRegistered(Game game) {
		
	}
	
	/**
	 * Called when upgrade is unregistered
	 * 
	 * @param game Game when upgrade is deactivated
	 */
	default void onUpgradeUnregistered(Game game) {
		
	}
}
