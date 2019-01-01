package misat11.bw.game;

import org.bukkit.Location;

public class ItemSpawner {
	public Location loc;
	public ItemSpawnerType type;
	
	public ItemSpawner(Location loc, ItemSpawnerType type) {
		this.loc = loc;
		this.type = type;
	}
}
