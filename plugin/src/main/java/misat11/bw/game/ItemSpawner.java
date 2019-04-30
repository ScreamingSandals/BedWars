package misat11.bw.game;

import org.bukkit.Location;

public class ItemSpawner implements misat11.bw.api.ItemSpawner {
	public Location loc;
	public ItemSpawnerType type;
	public String customName;
	public int startLevel = 1;
	public int currentLevel = startLevel;
	
	public ItemSpawner(Location loc, ItemSpawnerType type, String customName, int startLevel) {
		this.loc = loc;
		this.type = type;
		this.customName = customName;
		this.startLevel = startLevel;
	}

	@Override
	public misat11.bw.api.ItemSpawnerType getItemSpawnerType() {
		return type;
	}

	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public boolean hasCustomName() {
		return customName != null;
	}

	@Override
	public String getCustomName() {
		return customName;
	}

	@Override
	public int getStartLevel() {
		return startLevel;
	}

	@Override
	public int getCurrentLevel() {
		return currentLevel;
	}
}
