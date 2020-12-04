package org.screamingsandals.bedwars.game;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.screamingsandals.bedwars.Main;
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
    public boolean floatingEnabled;
    public Team team;
    public List<Item> spawnedItems;
    public boolean spawnerIsFullHologram = false;
    public boolean rerenderHologram = false;
    public double currentLevelOnHologram = -1;
    private ArmorStand floatingGenStand;
    public final static String ARMOR_STAND_DISPLAY_NAME_HIDDEN = "BEDWARS_FLOATING_ROT_ENTITY";

    public ItemSpawner(Location loc, ItemSpawnerType type, String customName,
                       boolean hologramEnabled, double startLevel, Team team,
                       int maxSpawnedResources, boolean floatingEnabled) {
        this.loc = loc;
        this.type = type;
        this.customName = customName;
        this.currentLevel = this.startLevel = startLevel;
        this.hologramEnabled = hologramEnabled;
        this.team = team;
        this.spawnedItems = new ArrayList<>();
        this.maxSpawnedResources = maxSpawnedResources;
        this.floatingEnabled = floatingEnabled;
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
    public boolean getFloatingEnabled() {
        return floatingEnabled;
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
    	
    	int spawned = spawnedItems.size();
    	
    	if (spawned >= maxSpawnedResources) {
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
    		} else if (countdown != null && (calculated + spawned) == maxSpawnedResources) {
        		spawnerIsFullHologram = true;
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

    public void spawnFloatingStand(){
        if (floatingEnabled) {
            floatingGenStand = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0,
                    Main.getConfigurator().config.getDouble("floating-generator.generator-height",
                            0.25), 0), EntityType.ARMOR_STAND
            );

            ItemStack helmetStack;
            try{
                //try to get block of item
                final String name = type.getMaterial().name().substring(0, type.getMaterial().name().indexOf("_"));
                helmetStack = new ItemStack(Material.valueOf(name.toUpperCase() + "_BLOCK"));
            } catch (Throwable t){
                helmetStack = new ItemStack(type.getMaterial());
            }
            floatingGenStand.setDisabledSlots(EquipmentSlot.HEAD);
            floatingGenStand.setHelmet(helmetStack);
            floatingGenStand.setGravity(false);
            floatingGenStand.setVisible(false);
            floatingGenStand.setCustomName(ARMOR_STAND_DISPLAY_NAME_HIDDEN);
        }
    }

    public void destroy() {
        if (floatingGenStand != null)
            floatingGenStand.remove();
    }




}
