package org.screamingsandals.bedwars.game;

import org.screamingsandals.bedwars.api.Team;

import misat11.lib.nms.Hologram;

import static misat11.lib.lang.I.i18nonly;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Item;

public class ItemSpawner implements org.screamingsandals.bedwars.api.game.ItemSpawner {
    public Location loc;
    public ItemSpawnerType type;
    public String customName;
    public double startLevel;
    public double currentLevel;
    public int maxSpawnedResources;
    public boolean hologramEnabled;
    public Team team;
    public List<Item> spawnedItems;
    public boolean spawnerIsFullHologram = false;
    public boolean rerenderHologram = false;
    public double currentLevelOnHologram = -1;

    public ItemSpawner(Location loc, ItemSpawnerType type, String customName, boolean hologramEnabled, double startLevel, Team team, int maxSpawnedResources) {
        this.loc = loc;
        this.type = type;
        this.customName = customName;
        this.currentLevel = this.startLevel = startLevel;
        this.hologramEnabled = hologramEnabled;
        this.team = team;
        this.spawnedItems = new ArrayList<>();
        this.maxSpawnedResources = maxSpawnedResources;
    }

    @Override
    public org.screamingsandals.bedwars.api.game.ItemSpawnerType getItemSpawnerType() {
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

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public void setTeam(Team team) {
        this.team = team;
    }
    
    public int getMaxSpawnedResources() {
    	return maxSpawnedResources;
    }
    
    public int nextMaxSpawn(int calculated, Hologram countdown) {
    	if (currentLevel <= 0) {
    		if (!spawnerIsFullHologram || currentLevelOnHologram != currentLevel) {
    			spawnerIsFullHologram = true;
    			currentLevelOnHologram = currentLevel; 
                        if (countdown != null)
    			countdown.setLine(1, i18nonly("spawner_not_enough_level").replace("%levels%", String.valueOf((currentLevelOnHologram * (-1)) + 1)));
    		}
    		return 0;
    	}
    	
    	if (maxSpawnedResources <= 0) {
    		if (spawnerIsFullHologram && !rerenderHologram) {
    			spawnerIsFullHologram = false;
    			rerenderHologram = true;
    		}
    		return calculated;
    	}
    	
    	/* Update spawned items */
    	for (Item item : new ArrayList<>(spawnedItems)) {
    		if (item.isDead()) {
    			spawnedItems.remove(item);
    		}
    	}
    	
    	int spawned = spawnedItems.size();
    	
    	if (spawned >= maxSpawnedResources) {
    		if (countdown != null && !spawnerIsFullHologram) {
        		spawnerIsFullHologram = true;
                        if (countdown != null)
    			countdown.setLine(1, i18nonly("spawner_is_full"));
    		}
    		return 0;
    	}
    	
    	if ((maxSpawnedResources - spawned) >= calculated) {
    		if (spawnerIsFullHologram && !rerenderHologram) {
    			rerenderHologram = true;
    			spawnerIsFullHologram = false;
    		} else if ((calculated + spawned) == maxSpawnedResources) {
        		spawnerIsFullHologram = true;
                        if (countdown != null)
    			countdown.setLine(1, i18nonly("spawner_is_full"));
    		}
    		return calculated;
    	}
    	
		if (countdown != null && !spawnerIsFullHologram) {
    		spawnerIsFullHologram = true;
			countdown.setLine(1, i18nonly("spawner_is_full"));
		}
    	
    	return maxSpawnedResources - spawned;
    }
    
    public void add(Item item) {
    	if (maxSpawnedResources > 0 && !spawnedItems.contains(item)) {
    		spawnedItems.add(item);
    	}
    }
    
    public void remove(Item item) {
    	if (maxSpawnedResources > 0 && spawnedItems.contains(item)) {
    		spawnedItems.remove(item);
    		if (spawnerIsFullHologram && maxSpawnedResources > spawnedItems.size()) {
    			spawnerIsFullHologram = false;
    			rerenderHologram = true;
    		}
    	}
    }
}
