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

import lombok.experimental.UtilityClass;
import me.kcra.takenaka.accessor.platform.MapperPlatform;
import me.kcra.takenaka.accessor.platform.MapperPlatforms;
import org.bukkit.Bukkit;
import org.screamingsandals.bedwars.lib.nms.accessors.MinecraftServerAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.MinecraftServerMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@UtilityClass
public class VersionFallback {
    public static void applyFallbackIfNeeded() {
        String[] mappingNamespaces = MapperPlatforms.getCurrentPlatform().getMappingNamespaces();
        String version = MapperPlatforms.getCurrentPlatform().getVersion();

        if (MinecraftServerAccessor.TYPE.get() != null) {
            // everything is fine, we support this version
            Bukkit.getLogger().info("[BedWars] Loaded NMS modules for " + version + " in namespaces " + Arrays.toString(mappingNamespaces));
            return;
        }

        List<String> usedNamespaces = Arrays.asList(mappingNamespaces);

        String latest = MinecraftServerMapping.MAPPING.getMappings().entrySet().stream()
                .filter(entry -> usedNamespaces.stream().anyMatch(e -> entry.getValue().containsKey(e)))
                .map(Map.Entry::getKey)
                .max(VersionFallback::compareVersions)
                .orElse(null);

        if (latest == null) {
            // so, everything is broken ig
            return;
        }

        if (compareVersions(version, latest) <= 0) {
            // not newer, unsupported!
            Bukkit.getLogger().severe("[BedWars] Version " + version + " is incompatible with Screaming BedWars!");
            return;
        }

        MapperPlatforms.setCurrentPlatform(MapperPlatform.create(latest, MapperPlatforms.getCurrentPlatform().getClassLoader(), mappingNamespaces));
        Bukkit.getLogger().warning("[BedWars] ==========================");
        Bukkit.getLogger().warning("[BedWars] This Minecraft version (" + version + ") is newer than the latest supported version (" + latest + ").");
        Bukkit.getLogger().warning("[BedWars] Attempting to use the latest known mappings. This is only safe if the newer Minecraft version is a bugfix release.");
        Bukkit.getLogger().warning("[BedWars] Unless we confirm compatibility, do NOT run this version and wait for an update.");
        Bukkit.getLogger().warning("[BedWars] ==========================");
        Bukkit.getLogger().info("[BedWars] Loaded fallback NMS modules for " + latest + " in namespaces " + Arrays.toString(mappingNamespaces));
    }

    private static int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        return 0;
    }
}
