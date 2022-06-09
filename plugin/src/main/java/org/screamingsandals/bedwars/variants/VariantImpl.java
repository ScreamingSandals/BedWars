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

package org.screamingsandals.bedwars.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.api.variants.Variant;
import org.screamingsandals.bedwars.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

@RequiredArgsConstructor
@Getter
public class VariantImpl implements Variant {
    private final String name;
    private final GameConfigurationContainer configurationContainer = new GameConfigurationContainer();

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

            var variant = new VariantImpl(configMap.node("name").getString(file.getName().split("\\.")[0]));

            variant.configurationContainer.applyNode(configMap.node("config"));

            PlayerMapper.getConsoleSender().sendMessage(
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

}
