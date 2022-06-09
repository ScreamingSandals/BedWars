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

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;

public class TeamColor extends org.screamingsandals.bedwars.api.TeamColor {

    static {
        new TeamColor("WHITE", ChatColor.WHITE, 0x0, "WHITE", Color.WHITE).register();
        new TeamColor("ORANGE", ChatColor.GOLD, 0x1, "ORANGE", Color.fromRGB(255, 170, 0)).register();
        new TeamColor("MAGENTA", ChatColor.LIGHT_PURPLE, 0x2, "MAGENTA", Color.fromRGB(170, 0, 170)).register();
        new TeamColor("LIGHT_BLUE", ChatColor.AQUA, 0x3, "LIGHT_BLUE", Color.fromRGB(85, 85, 255)).register();
        new TeamColor("YELLOW", ChatColor.YELLOW, 0x4, "YELLOW", Color.fromRGB(255, 255, 85)).register();
        new TeamColor("LIME", ChatColor.GREEN, 0x5, "LIME", Color.fromRGB(85, 255, 85)).register();
        new TeamColor("PINK", ChatColor.LIGHT_PURPLE, 0x6, "PINK", Color.fromRGB(255, 85, 255)).register();
        new TeamColor("GRAY", ChatColor.DARK_GRAY, 0x7, "GRAY", Color.fromRGB(85, 85, 85)).register();
        new TeamColor("LIGHT_GRAY", ChatColor.GRAY, 0x8, "LIGHT_GRAY", Color.fromRGB(170, 170, 170)).register();
        new TeamColor("CYAN", ChatColor.DARK_AQUA, 0x9, "CYAN", Color.fromRGB(85, 255, 255)).register();
        new TeamColor("PURPLE", ChatColor.DARK_PURPLE, 0xA, "PURPLE", Color.PURPLE).register();
        new TeamColor("BLUE", ChatColor.BLUE, 0xB, "BLUE", Color.fromRGB(0, 0, 170)).register();
        new TeamColor("BROWN", ChatColor.DARK_RED, 0xC, "BROWN", Color.fromRGB(139, 69, 19)).register();
        new TeamColor("GREEN", ChatColor.DARK_GREEN, 0xD, "GREEN", Color.fromRGB(0, 170, 0)).register();
        new TeamColor("RED", ChatColor.RED, 0xE, "RED", Color.fromRGB(255, 85, 85)).register();
        new TeamColor("BLACK", ChatColor.BLACK, 0xF, "BLACK", Color.BLACK).register();
    }

    public final ChatColor chatColor;
    public final String material1_13;
    public final int woolData;
    public final Color leatherColor;

    TeamColor(String key, ChatColor chatColor, int woolData, String material1_13, Color leatherColor) {
        super(key);
        this.chatColor = chatColor;
        this.woolData = woolData;
        this.material1_13 = material1_13;
        this.leatherColor = leatherColor;

    }

    @Override
    public ItemStack getWool() {
        if (Main.isLegacy()) {
            return new ItemStack(Material.valueOf("WOOL"), 1, (short) woolData);
        } else {
            return new ItemStack(Material.valueOf(material1_13 + "_WOOL"));
        }

    }

    @Override
    public ItemStack getWool(ItemStack stack) {
        if (Main.isLegacy()) {
            stack.setType(Material.valueOf("WOOL"));
            stack.setDurability((short) woolData);
        } else {
            stack.setType(Material.valueOf(material1_13 + "_WOOL"));
        }
        return stack;

    }

    public org.screamingsandals.bedwars.api.TeamColor toApiColor() {
        return this;
    }

    public static TeamColor fromApiColor(org.screamingsandals.bedwars.api.TeamColor color) {
        if (color instanceof TeamColor)
            return (TeamColor) color;
        return null;
    }

    public static TeamColor valueOf(String s) {
        org.screamingsandals.bedwars.api.TeamColor apiColor = org.screamingsandals.bedwars.api.TeamColor.valueOf(s);
        if (apiColor != null)
            return fromApiColor(apiColor);
        return null;
    }

    public static List<TeamColor> values() {
        return org.screamingsandals.bedwars.api.TeamColor.apiValues().stream().map(TeamColor::fromApiColor).collect(Collectors.toList());
    }
}
