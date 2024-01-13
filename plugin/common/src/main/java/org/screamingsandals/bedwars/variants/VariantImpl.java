/*
 * Copyright (C) 2024 ScreamingSandals
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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.variants.Variant;
import org.screamingsandals.bedwars.config.GameConfigurationContainerImpl;
import org.screamingsandals.bedwars.game.ItemSpawnerTypeImpl;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class VariantImpl implements Variant {
    @NotNull
    private final String name;
    @NotNull
    private final GameConfigurationContainerImpl configurationContainer = new GameConfigurationContainerImpl();
    @NotNull
    private final List<ItemSpawnerTypeImpl> customSpawnerTypes = new ArrayList<>();
    private final @NotNull File file;
    private boolean defaultItemSpawnerTypesIncluded = true;

    public static VariantImpl loadVariant(File file) {
        try {
            if (!file.exists()) {
                return null;
            }

            final ConfigurationLoader<? extends ConfigurationNode> loader;
            if (file.getName().toLowerCase().endsWith(".yml") || file.getName().toLowerCase().endsWith(".yaml")) {
                loader = YamlConfigurationLoader.builder()
                        .file(file)
                        .build();
            } else {
                loader = GsonConfigurationLoader.builder()
                        .file(file)
                        .build();
            }

            final ConfigurationNode configMap;
            try {
                configMap = loader.load();
            } catch (ConfigurateException e) {
                e.printStackTrace();
                return null;
            }

            VariantImpl variant;
            if (file.getName().equalsIgnoreCase("default.yml")) {
                variant = new VariantImpl("default", file);
            } else {
                var name = configMap.node("name").getString();
                if (name == null || name.isEmpty() || name.equalsIgnoreCase("default")) {
                    name = file.getName().split("\\.")[0];
                }
                variant = new VariantImpl(name, file);
            }

            variant.configurationContainer.applyNode(configMap.node("config"));

            var spawnersNode = configMap.node("custom-spawner-types");
            if (!spawnersNode.empty() && spawnersNode.isMap()) {
                spawnersNode.childrenMap().forEach((spawnerK, node) -> {
                    var type = ItemSpawnerTypeImpl.deserialize(spawnerK.toString(), node);
                    if (type != null) {
                        variant.customSpawnerTypes.add(type);
                    }
                });

                if (!variant.customSpawnerTypes.isEmpty()) {
                    variant.defaultItemSpawnerTypesIncluded = configMap.node("default-spawner-types-included").getBoolean(true);
                }
            } else {
                variant.defaultItemSpawnerTypesIncluded = true;
            }

            Server.getConsoleSender().sendMessage(
                    MiscUtils.BW_PREFIX.withAppendix(
                            Component.text("Variant ", Color.GREEN),
                            Component.text(variant.name + " (" + file.getName() + ")", Color.WHITE),
                            Component.text(" loaded!", Color.GREEN)
                    )
            );

            return variant;
        } catch (Throwable throwable) {
            Debug.warn("Something went wrong while loading variant file " + file.getName() + ". Please report this to our Discord or GitHub!", true);
            throwable.printStackTrace();
            return null;
        }
    }

    @Override
    @NotNull
    public List<@NotNull ItemSpawnerTypeImpl> getItemSpawnerTypes() {
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
    @NotNull
    public List<String> getItemSpawnerTypeNames() {
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

    @NotNull
    public List<@NotNull ItemSpawnerTypeImpl> getCustomItemSpawnerTypes() {
        return List.copyOf(customSpawnerTypes);
    }

    @Override
    @Nullable
    public ItemSpawnerTypeImpl getItemSpawnerType(@NotNull String name) {
        var finalName = name.toLowerCase();
        var match = customSpawnerTypes.stream().filter(t -> t.getConfigKey().equals(finalName)).findFirst().orElse(null);
        if (match != null) {
            return match;
        }
        if (!defaultItemSpawnerTypesIncluded) {
            return null;
        }
        return BedWarsPlugin.getInstance().getSpawnerTypes().get(finalName);
    }

}
