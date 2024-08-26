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

package org.screamingsandals.bedwars.lib.signmanager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.screamingsandals.bedwars.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignManager {

    private final FileConfiguration config = new YamlConfiguration();
    private final File configFile;
    private final Map<Location, SignBlock> signs = new HashMap<>();
    private boolean modify = false;
    private SignOwner owner;

    public SignManager(SignOwner owner, File configFile) {
        this.owner = owner;
        this.configFile = configFile;

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::loadConfig, 5L);
    }

    public void loadConfig() {
        try {
            config.load(configFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (config.isSet("sign")) {
            List<Map<String, Object>> conf = (List<Map<String, Object>>) config.getList("sign");
            if (conf != null) {
                for (Map<String, Object> c : conf) {
                    String name = (String) c.get("name");
                    if (name == null || name.trim().isEmpty()) {
                        name = (String) c.get("game"); // Compatibility with old BedWars sign.yml
                    }
                    Location loc = (Location) c.get("location");
                    if (loc.getWorld() == null) {
                        Main.getInstance().getLogger().warning("A sign on location " + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + " is in unknown world! It was removed from the configuration");
                        continue; // Skip invalid locations
                    }
                    signs.put(loc, new SignBlock(loc, name));
                    owner.updateSign(signs.get(loc));
                }
            }
        }
    }

    public boolean isSignRegistered(Location location) {
        return signs.containsKey(location);
    }

    public void unregisterSign(Location location) {
        if (signs.containsKey(location)) {
            signs.remove(location);
            modify = true;
        }
    }

    public boolean registerSign(Location location, String game) {
        if (owner.isNameExists(game)) {
        	SignBlock block = new SignBlock(location, game);
            signs.put(location, block);
            modify = true;
            owner.updateSign(block);
            return true;
        }
        return false;
    }

    public SignBlock getSign(Location location) {
        return signs.get(location);
    }

    public List<SignBlock> getSignsForName(String name) {
        List<SignBlock> list = new ArrayList<>();
        for (SignBlock sign : signs.values()) {
            if (sign.getName().equals(name)) {
                list.add(sign);
            }
        }
        return list;
    }

    public void save() {
        save(false);
    }

    public void save(boolean force) {
        if (modify || force) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (SignBlock sign : signs.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("location", sign.getLocation());
                map.put("name", sign.getName());
                list.add(map);
            }

            config.set("sign", list);

            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
