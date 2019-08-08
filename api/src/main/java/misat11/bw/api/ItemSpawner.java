package misat11.bw.api;

import org.bukkit.Location;

import misat11.bw.api.upgrades.Upgrade;

public interface ItemSpawner extends Upgrade {
	public ItemSpawnerType getItemSpawnerType();
	
	public Location getLocation();
	
	public boolean hasCustomName();
	
	public String getCustomName();
	
	public double getStartLevel();
	
	public double getCurrentLevel();
	
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
