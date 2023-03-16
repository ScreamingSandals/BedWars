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

package org.screamingsandals.bedwars.utils;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.TeamColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.screamingsandals.bedwars.lib.debug.Debug;

public class ColorChanger implements org.screamingsandals.bedwars.api.utils.ColorChanger {
    public static ItemStack changeLegacyStackColor(ItemStack itemStack, TeamColor teamColor) {
        Material material = itemStack.getType();
        String materialName = material.name();

        if (Main.autoColoredMaterials.contains(materialName)) {
            itemStack.setDurability((short) teamColor.woolData);
        } else if (material.toString().contains("GLASS_PANE")) {
            itemStack.setType(Material.getMaterial("STAINED_GLASS_PANE"));
            itemStack.setDurability((short) teamColor.woolData);
        } else if (material.toString().contains("GLASS")) {
            itemStack.setType(Material.getMaterial("STAINED_GLASS"));
            itemStack.setDurability((short) teamColor.woolData);
        }
        return itemStack;
    }

    public static Material changeMaterialColor(Material material, TeamColor teamColor) {
        String materialName = material.name();

        try {
            materialName = material.toString().substring(material.toString().indexOf("_") + 1);
        } catch (StringIndexOutOfBoundsException ignored) {
        }

        String teamMaterialColor = teamColor.material1_13;

        if (Main.autoColoredMaterials.contains(materialName)) {
            return Material.getMaterial(teamMaterialColor + "_" + materialName);
        } else if (material.toString().contains("GLASS_PANE")) {
            return Material.getMaterial(teamMaterialColor + "_STAINED_GLASS_PANE");
        } else if (material.toString().contains("GLASS")) {
            return Material.getMaterial(teamMaterialColor + "_STAINED_GLASS");
        }
        return material;

    }

    public static ItemStack changeLeatherArmorColor(ItemStack itemStack, TeamColor color) {
        Material material = itemStack.getType();

        if (material.toString().contains("LEATHER_") && !material.toString().contains("LEATHER_HORSE_")) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();

            meta.setColor(color.leatherColor);
            itemStack.setItemMeta(meta);

            return itemStack;
        }
        return itemStack;
    }

    @Override
    public ItemStack applyColor(org.screamingsandals.bedwars.api.TeamColor apiColor, ItemStack stack) {
        try {
            TeamColor color = TeamColor.fromApiColor(apiColor);
            Material material = stack.getType();
            if (Main.isLegacy()) {
                stack = changeLegacyStackColor(stack, color);
            } else {
                stack.setType(changeMaterialColor(material, color));
            }
            stack = changeLeatherArmorColor(stack, color);
            return stack;
        } catch (NullPointerException e) {
            Debug.warn("DEFINED ITEM DOES NOT EXISTS. CHECK YOUR CONFIG.");
            e.printStackTrace();
            return new ItemStack(Material.BLACK_WOOL);
        }
    }
}
