/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.game;

import org.bukkit.entity.Entity;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.lib.nms.holograms.Hologram;

import static org.screamingsandals.bedwars.lib.lang.I.i18nonly;

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

    public boolean spawnerLockedFull;
    public int countdownDelay;
    public int currentCycle;

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
    		if (countdown != null && (!spawnerIsFullHologram || currentLevelOnHologram != currentLevel)) {
    			spawnerIsFullHologram = true;
    			currentLevelOnHologram = currentLevel; 
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
        spawnedItems.removeIf(Entity::isDead);
    	
    	int spawned = getSpawnedItemsCount();
    	
    	if (spawned >= maxSpawnedResources) {
            spawnerLockedFull = true;
    		if (countdown != null && !spawnerIsFullHologram) {
        		spawnerIsFullHologram = true;
    			countdown.setLine(1, i18nonly("spawner_is_full"));
    		}
    		return 0;
    	}
    	
    	if ((maxSpawnedResources - spawned) >= calculated) {
    		if (spawnerIsFullHologram && !rerenderHologram) {
    			rerenderHologram = true;
    			spawnerIsFullHologram = false;
    		} else if ((calculated + spawned) == maxSpawnedResources) {
                spawnerLockedFull = true;
                if (countdown != null) {
                    spawnerIsFullHologram = true;
                    countdown.setLine(1, i18nonly("spawner_is_full"));
                }
    		}
    		return calculated;
    	}

        spawnerLockedFull = true;
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
    		if (spawnerIsFullHologram && maxSpawnedResources > getSpawnedItemsCount()) {
    			spawnerIsFullHologram = false;
    			rerenderHologram = true;
    		}
    	}
    }

    /**
     * Works only if maxSpawnedResources > 0
     */
    public int getSpawnedItemsCount() {
        return spawnedItems.stream().mapToInt(i -> i.getItemStack().getAmount()).sum();
    }
}
