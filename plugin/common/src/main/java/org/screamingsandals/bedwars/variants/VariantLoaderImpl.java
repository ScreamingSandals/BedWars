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

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.variants.VariantLoader;
import org.screamingsandals.bedwars.game.ItemSpawnerTypeImpl;
import org.screamingsandals.bedwars.utils.ConfigurateUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.variants.prefab.CommandPrefab;
import org.screamingsandals.bedwars.variants.prefab.Prefab;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.logger.LoggerWrapper;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;

@Service
@RequiredArgsConstructor
public class VariantLoaderImpl implements VariantLoader {
    private final @NotNull LoggerWrapper logger;

    @Override
    public @Nullable VariantImpl loadVariant(@NotNull File file) {
        try {
            final ConfigurationNode configMap = ConfigurateUtils.loadFileAsNode(file);
            if (configMap == null) {
                return null;
            }

            VariantImpl variant;
            if ("default.yml".equalsIgnoreCase(file.getName())) {
                variant = new VariantImpl("default", file);
            } else {
                var name = configMap.node("name").getString();
                if (name == null || name.isEmpty() || "default".equalsIgnoreCase(name)) {
                    name = file.getName().split("\\.")[0];
                }
                variant = new VariantImpl(name, file);
            }

            variant.getConfigurationContainer().applyNode(configMap.node("config"));

            var spawnersNode = configMap.node("custom-spawner-types");
            if (!spawnersNode.empty() && spawnersNode.isMap()) {
                spawnersNode.childrenMap().forEach((spawnerK, node) -> {
                    var type = ItemSpawnerTypeImpl.deserialize(spawnerK.toString(), node);
                    if (type != null) {
                        variant.getCustomSpawnerTypes().add(type);
                    }
                });

                if (!variant.getCustomSpawnerTypes().isEmpty()) {
                    variant.setDefaultItemSpawnerTypesIncluded(configMap.node("default-spawner-types-included").getBoolean(true));
                }
            } else {
                variant.setDefaultItemSpawnerTypesIncluded(true);
            }

            var prefabs = configMap.node("prefabs");
            if (!prefabs.empty() && prefabs.isMap()) {
                prefabs.childrenMap().forEach((o, node) -> {
                    try {
                        var prefabType = node.node("type").getString();
                        @NotNull Prefab prefab;
                        switch (prefabType) {
                            case "command":
                                prefab = CommandPrefab.Loader.INSTANCE.load(node);
                                break;
                            default:
                                logger.error("Unknown prefab type: {}", prefabType);
                                return;
                        }
                        variant.getPrefabMap().put(o.toString(), prefab);
                    } catch (ConfigurateException exception) {
                        logger.error("Could not load a prefab from variant {}", variant.getName(), exception);
                    }
                });
            }

            Server.getConsoleSender().sendMessage(
                    MiscUtils.BW_PREFIX.withAppendix(
                            Component.text("Variant ", Color.GREEN),
                            Component.text(variant.getName() + " (" + file.getName() + ")", Color.WHITE),
                            Component.text(" loaded!", Color.GREEN)
                    )
            );

            return variant;
        } catch (Throwable throwable) {
            logger.error("Something went wrong while loading variant file {}. Please report this to our Discord or GitHub!", file.getName(), throwable);
            return null;
        }
    }
}
