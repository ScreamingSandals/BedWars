package misat11.bw.api;

import org.bukkit.Location;

import misat11.bw.api.upgrades.Upgrade;

/**
 * @author Bedwars Team
 *
 */
public interface ItemSpawner extends Upgrade {
	/**
	 * @return
	 */
	public ItemSpawnerType getItemSpawnerType();
	
	/**
	 * @return
	 */
	public Location getLocation();
	
	/**
	 * @return
	 */
	public boolean hasCustomName();
	
	/**
	 * @return
	 */
	public String getCustomName();
	
	/**
	 * @return
	 */
	public double getStartLevel();
	
	/**
	 * @return
	 */
	public double getCurrentLevel();

	/**
	 * @return
	 */
	public boolean getHologramEnabled();
	
	/**
	 * @param level
	 */
	public void setCurrentLevel(double level);
	
	default void addToCurrentLevel(double level) {
		setCurrentLevel(getCurrentLevel() + level);
	}
	
	default String getName() {
		return "spawner";
	}
	
	default String getInstanceName() {
		return getCustomName();
	}
	
	default double getLevel() {
		return getCurrentLevel();
	}
	
	default void setLevel(double level) {
		setCurrentLevel(level);
	} 
	
	default void increaseLevel(double level) {
		addToCurrentLevel(level);
	}
	
	default double getInitialLevel() {
		return getStartLevel();
	}
}
