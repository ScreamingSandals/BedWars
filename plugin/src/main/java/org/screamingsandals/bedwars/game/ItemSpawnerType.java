/*
 * Copyright (C) 2022 ScreamingSandals
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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class ItemSpawnerType implements org.screamingsandals.bedwars.api.game.ItemSpawnerType {
    private String configKey;
    private String name;
    private String translatableKey;
    private double spread;
    private Material material;
    private ChatColor color;
    private int interval;
    private int damage;

    public ItemSpawnerType(String configKey, String name, String translatableKey, double spread, Material material,
                           ChatColor color, int interval, int damage) {
        this.configKey = configKey;
        this.name = name;
        this.translatableKey = translatableKey;
        this.spread = spread;
        this.material = material;
        this.color = color;
        this.interval = interval;
        this.damage = damage;
    }

    public String getConfigKey() {
        return configKey;
    }

    public ChatColor getColor() {
        return color;
    }

    public int getInterval() {
        return interval;
    }

    public double getSpread() {
        return spread;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public String getTranslatableKey() {
        if (translatableKey != null && !translatableKey.equals("")) {
            return i18n(translatableKey, name, false);
        }
        return name;
    }

    public String getItemName() {
        return color + getTranslatableKey();
    }

    public String getItemBoldName() {
        return color.toString() + ChatColor.BOLD.toString() + getTranslatableKey();
    }

    public int getDamage() {
        return damage;
    }

    public ItemStack getStack() {
        return getStack(1);
    }

    public ItemStack getStack(int amount) {
        ItemStack stack = new ItemStack(material, amount, (short) damage);
        ItemMeta stackMeta = stack.getItemMeta();
        stackMeta.setDisplayName(getItemName());
        stack.setItemMeta(stackMeta);
        return stack;
    }
}
