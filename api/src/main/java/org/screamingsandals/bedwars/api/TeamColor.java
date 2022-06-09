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

import org.bukkit.inventory.ItemStack;

/**
 * @author Bedwars Team
 */
public abstract class TeamColor {

    public static HashMap<String, TeamColor> values = new HashMap<>();
    public final String key;

    public TeamColor(String key) {
        this.key = key;
    }

    public String name() {
        return key;
    }

    public void register() {
        values.put(key,this);
    }
    public void unregister() {
        values.remove(key);
    }
    public static void unregisterAll() {
        values.clear();
    }

    public abstract ItemStack getWool(ItemStack stack);
    public abstract ItemStack getWool();

    public static TeamColor valueOf(String s) {
        if (values.containsKey(s))
            return values.get(s);
        return null;
    }
    public static List<TeamColor> apiValues()
    {
        return new ArrayList<>(values.values());
    }
}
