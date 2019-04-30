package misat11.bw.api;

import org.bukkit.Location;

public interface ItemSpawner {
	public ItemSpawnerType getItemSpawnerType();
	
	public Location getLocation();
	
	public boolean hasCustomName();
	
	public String getCustomName();
	
	public int getStartLevel();
	
	public int getCurrentLevel();
}
