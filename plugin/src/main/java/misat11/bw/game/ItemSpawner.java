package misat11.bw.game;

import org.bukkit.Location;

public class ItemSpawner implements misat11.bw.api.ItemSpawner {
	public Location loc;
	public ItemSpawnerType type;
	public String customName;
	public double startLevel;
	public double currentLevel;
	public boolean hologramEnabled;
	
	public ItemSpawner(Location loc, ItemSpawnerType type, String customName, boolean hologramEnabled, double startLevel) {
		this.loc = loc;
		this.type = type;
		this.customName = customName;
		this.currentLevel = this.startLevel = startLevel;
		this.hologramEnabled = hologramEnabled;
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
	public double getStartLevel() {
		return startLevel;
	}

	@Override
	public double getCurrentLevel() {
		return currentLevel;
	}

	@Override
	public boolean getHologramEnabled() {
		return hologramEnabled;
	}

	@Override
	public void setCurrentLevel(double level) {
		currentLevel = level;
	}
}
