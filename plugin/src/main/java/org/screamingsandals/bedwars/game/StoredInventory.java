package org.screamingsandals.bedwars.game;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class StoredInventory {
    public ItemStack[] armor = null;
    public String displayName = null;
    public Collection<PotionEffect> effects = null;
    public int foodLevel = 0;
    public ItemStack[] inventory = null;
    public Location leftLocation = null;
    public int level = 0;
    public String listName = null;
    public GameMode mode = null;
    public float xp = 0.0F;
}
