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

package org.screamingsandals.bedwars.utils;

import org.screamingsandals.bedwars.game.Team;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorStandUtils {
    public static void equipArmorStand(ArmorStand stand, Team team) {
        if (stand == null || team == null) {
            return;
        }

        // helmet
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(team.color.leatherColor);
        helmet.setItemMeta(meta);

        // chestplate
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        meta = (LeatherArmorMeta) chestplate.getItemMeta();
        meta.setColor(team.color.leatherColor);
        chestplate.setItemMeta(meta);

        // leggings
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        meta = (LeatherArmorMeta) leggings.getItemMeta();
        meta.setColor(team.color.leatherColor);
        leggings.setItemMeta(meta);

        // boots
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
        meta = (LeatherArmorMeta) boots.getItemMeta();
        meta.setColor(team.color.leatherColor);
        boots.setItemMeta(meta);

        stand.setHelmet(helmet);
        stand.setChestplate(chestplate);
        stand.setLeggings(leggings);
        stand.setBoots(boots);
    }
}
