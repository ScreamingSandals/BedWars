/*
 * Copyright (C) 2025 ScreamingSandals
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

package org.screamingsandals.bedwars.variants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.variants.Variant;
import org.screamingsandals.bedwars.config.GameConfigurationContainerImpl;
import org.screamingsandals.bedwars.game.ItemSpawnerTypeImpl;
import org.screamingsandals.bedwars.variants.prefab.Prefab;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class VariantImpl implements Variant {
    private final @NotNull String name;
    private final @NotNull GameConfigurationContainerImpl configurationContainer = new GameConfigurationContainerImpl();
    @Getter(AccessLevel.PROTECTED)
    private final @NotNull List<@NotNull ItemSpawnerTypeImpl> customSpawnerTypes = new ArrayList<>();
    private final @NotNull File file;
    @Getter(AccessLevel.PROTECTED)
    private final @NotNull Map<@NotNull String, Prefab> prefabMap = new HashMap<>();
    @Setter(AccessLevel.PROTECTED)
    private boolean defaultItemSpawnerTypesIncluded = true;

    @Override
    public @NotNull List<@NotNull ItemSpawnerTypeImpl> getItemSpawnerTypes() {
        if (!defaultItemSpawnerTypesIncluded) {
            return List.copyOf(customSpawnerTypes);
        }

        var nList = new ArrayList<>(customSpawnerTypes);
        for (var type : BedWarsPlugin.getInstance().getSpawnerTypes().values()) {
            if (nList.stream().noneMatch(t -> t.getConfigKey().equals(type.getConfigKey()))) {
                nList.add(type);
            }
        }
        return nList;
    }

    @Override
    public @NotNull List<@NotNull String> getItemSpawnerTypeNames() {
        var names = customSpawnerTypes.stream().map(ItemSpawnerTypeImpl::getConfigKey).collect(Collectors.toList());
        if (defaultItemSpawnerTypesIncluded) {
            for (var name : BedWarsPlugin.getInstance().getSpawnerTypes().keySet()) {
                if (!names.contains(name)) {
                    names.add(name);
                }
            }
        }
        return names;
    }

    public @Unmodifiable @NotNull List<@NotNull ItemSpawnerTypeImpl> getCustomItemSpawnerTypes() {
        return List.copyOf(customSpawnerTypes);
    }

    @Override
    public @Nullable ItemSpawnerTypeImpl getItemSpawnerType(@NotNull String name) {
        var finalName = name.toLowerCase(Locale.ROOT);
        var match = customSpawnerTypes.stream().filter(t -> t.getConfigKey().equals(finalName)).findFirst().orElse(null);
        if (match != null) {
            return match;
        }
        if (!defaultItemSpawnerTypesIncluded) {
            return null;
        }
        return BedWarsPlugin.getInstance().getSpawnerTypes().get(finalName);
    }

    public @Unmodifiable @NotNull Map<@NotNull String, Prefab> getPrefabs() {
        return Collections.unmodifiableMap(prefabMap);
    }

    public @Unmodifiable @NotNull List<@NotNull String> getPrefabNames() {
        return List.copyOf(prefabMap.keySet());
    }

    public @Nullable Prefab getPrefab(@NotNull String name) {
        return prefabMap.get(name);
    }
}
