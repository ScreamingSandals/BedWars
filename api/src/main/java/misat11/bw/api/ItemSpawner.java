package misat11.bw.api;

import org.bukkit.Location;

public interface ItemSpawner {
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
}
