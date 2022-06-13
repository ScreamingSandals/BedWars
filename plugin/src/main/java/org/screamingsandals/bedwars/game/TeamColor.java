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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;

import lombok.Getter;

public final class TeamColor {


    static Map<String, TeamColor> VALUES = new HashMap<>();

    static {
        initDefaults();
    }
    @Getter
    private final String key;
    @Getter
    public final ChatColor chatColor;
    @Getter
    public final String material1_13;
    @Getter
    public final int woolData;
    @Getter
    public final Color leatherColor;

    public static final TeamColor WHITE = new TeamColor("WHITE", ChatColor.WHITE, 0x0, "WHITE", Color.WHITE);
    public static final TeamColor ORANGE = new TeamColor("ORANGE", ChatColor.GOLD, 0x1, "ORANGE", Color.fromRGB(255, 170, 0));
    public static final TeamColor MAGENTA = new TeamColor("MAGENTA", ChatColor.LIGHT_PURPLE, 0x2, "MAGENTA", Color.fromRGB(170, 0, 170));
    public static final TeamColor LIGHT_BLUE = new TeamColor("LIGHT_BLUE", ChatColor.AQUA, 0x3, "LIGHT_BLUE", Color.fromRGB(85, 85, 255));
    public static final TeamColor YELLOW = new TeamColor("YELLOW", ChatColor.YELLOW, 0x4, "YELLOW", Color.fromRGB(255, 255, 85));
    public static final TeamColor LIME = new TeamColor("LIME", ChatColor.GREEN, 0x5, "LIME", Color.fromRGB(85, 255, 85));
    public static final TeamColor PINK = new TeamColor("PINK", ChatColor.LIGHT_PURPLE, 0x6, "PINK", Color.fromRGB(255, 85, 255));
    public static final TeamColor GRAY = new TeamColor("GRAY", ChatColor.DARK_GRAY, 0x7, "GRAY", Color.fromRGB(85, 85, 85));
    public static final TeamColor LIGHT_GRAY = new TeamColor("LIGHT_GRAY", ChatColor.GRAY, 0x8, "LIGHT_GRAY", Color.fromRGB(170, 170, 170));
    public static final TeamColor CYAN = new TeamColor("CYAN", ChatColor.DARK_AQUA, 0x9, "CYAN", Color.fromRGB(85, 255, 255));
    public static final TeamColor PURPLE = new TeamColor("PURPLE", ChatColor.DARK_PURPLE, 0xA, "PURPLE", Color.PURPLE);
    public static final TeamColor BLUE = new TeamColor("BLUE", ChatColor.BLUE, 0xB, "BLUE", Color.fromRGB(0, 0, 170));
    public static final TeamColor BROWN = new TeamColor("BROWN", ChatColor.DARK_RED, 0xC, "BROWN", Color.fromRGB(139, 69, 19));
    public static final TeamColor GREEN = new TeamColor("GREEN", ChatColor.DARK_GREEN, 0xD, "GREEN", Color.fromRGB(0, 170, 0));
    public static final TeamColor RED = new TeamColor("RED", ChatColor.RED, 0xE, "RED", Color.fromRGB(255, 85, 85));
    public static final TeamColor BLACK = new TeamColor("BLACK", ChatColor.BLACK, 0xF, "BLACK", Color.BLACK);
    
    public static void initDefaults()
    {
        unregisterAll();
        org.screamingsandals.bedwars.api.TeamColor.initDefaults();
        register(WHITE);
        register(ORANGE);
        register(MAGENTA);
        register(LIGHT_BLUE);
        register(YELLOW);
        register(LIME);
        register(PINK);
        register(GRAY);
        register(LIGHT_GRAY);
        register(CYAN);
        register(PURPLE);
        register(BLUE);
        register(BROWN);
        register(GREEN);
        register(RED);
        register(BLACK);
    }
    
    public TeamColor(String key, ChatColor chatColor, int woolData, String material1_13, Color leatherColor) {
        this.key = key;
        this.chatColor = chatColor;
        this.woolData = woolData;
        this.material1_13 = material1_13;
        this.leatherColor = leatherColor;
    }

    public String name() {
        return key;
    }

    public ItemStack getWool() {
        if (Main.isLegacy()) {
            return new ItemStack(Material.valueOf("WOOL"), 1, (short) woolData);
        } else {
            return new ItemStack(Material.valueOf(material1_13 + "_WOOL"));
        }

    }


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
        return org.screamingsandals.bedwars.api.TeamColor.valueOf(name());
    }

    public static TeamColor fromApiColor(org.screamingsandals.bedwars.api.TeamColor color) {
        return valueOf(color.name());
    }

    public int ordinal()
    {
        return VALUES.keySet().stream().toList().indexOf(name());
    }

    public static TeamColor valueOf(String s) {
        return VALUES.get(s.toUpperCase());
    }

    public static void register(TeamColor color) {
        org.screamingsandals.bedwars.api.TeamColor.register(new org.screamingsandals.bedwars.api.TeamColor(color.name()));
        VALUES.put(color.name().toUpperCase(), color);
    }

    public static void unregister(TeamColor color) {
        org.screamingsandals.bedwars.api.TeamColor.unregister(color.toApiColor());
        VALUES.remove(color.name().toUpperCase());
    }

    public static void unregisterAll() {
        org.screamingsandals.bedwars.api.TeamColor.unregisterAll();
        VALUES.clear();
    }

    public static List<TeamColor> values() {
        return new ArrayList<>(VALUES.values());
    }
    public Class<TeamColor> getDeclaringClass()
    {
        return TeamColor.class;
    }
    @Override
    public String toString() {
        return name();
    }
}
