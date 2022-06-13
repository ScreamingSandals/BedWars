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

package org.screamingsandals.bedwars.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

/**
 * @author Bedwars Team
 */
public final class TeamColor {

    static Map<String, TeamColor> VALUES = new HashMap<>();

    public static final TeamColor BLACK = new TeamColor("BLACK").register();
    public static final TeamColor BLUE = new TeamColor("BLUE").register();
    public static final TeamColor GREEN = new TeamColor("GREEN").register();
    public static final TeamColor RED = new TeamColor("RED").register();
    public static final TeamColor MAGENTA = new TeamColor("MAGENTA").register();
    public static final TeamColor ORANGE = new TeamColor("ORANGE").register();
    public static final TeamColor LIGHT_GRAY = new TeamColor("LIGHT_GRAY").register();
    public static final TeamColor GRAY = new TeamColor("GRAY").register();
    public static final TeamColor LIGHT_BLUE = new TeamColor("LIGHT_BLUE").register();
    public static final TeamColor LIME = new TeamColor("LIME").register();
    public static final TeamColor CYAN = new TeamColor("CYAN").register();
    public static final TeamColor PINK = new TeamColor("PINK").register();
    public static final TeamColor YELLOW = new TeamColor("YELLOW").register();
    public static final TeamColor WHITE = new TeamColor("WHITE").register();
    public static final TeamColor PURPLE = new TeamColor("PURPLE").register();
    public static final TeamColor BROWN = new TeamColor("BROWN").register();
    
    public static void initDefaults()
    {
        unregisterAll();
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

    private TeamColor register()
    {
        register(this);
        return this;
    }
    private String key;
    public TeamColor(String key)
    {
        this.key=key;
    }
    
    public String name()
    {
        return key;
    }

    public int ordinal()
    {
        return VALUES.keySet().stream().toList().indexOf(name());
    }

    public static TeamColor valueOf(String s) {
        return VALUES.get(s.toUpperCase());
    }

    public static void register(TeamColor color) {
        VALUES.put(color.name().toUpperCase(), color);
    }

    public static void unregister(TeamColor color) {
        VALUES.remove(color.name().toUpperCase());
    }

    public static void unregisterAll() {
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
