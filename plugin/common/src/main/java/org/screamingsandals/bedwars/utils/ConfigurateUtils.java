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

package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.screamingsandals.lib.configurate.SLibSerializers;
import org.screamingsandals.lib.utils.config.ConfigurationLoaderBuilderSupplier;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.File;

@UtilityClass
public class ConfigurateUtils {

    public ConfigurationNode loadFileAsNode(File file) {
        if (!file.exists()) {
            return null;
        }

        final ConfigurationNode configMap;
        try {
            configMap = getConfigurationLoaderForFile(file).load();
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return null;
        }

        return configMap;
    }


    public ConfigurationLoader<? extends ConfigurationNode> getConfigurationLoaderForFile(File file) throws ConfigurateException {
        var fileName = file.getName();
        var fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

        var configurationLoaderBuilder = ConfigurationLoaderBuilderSupplier.getForExtension(fileExtension);
        if (configurationLoaderBuilder == null) {
            throw new ConfigurateException("No configuration loader found for ext:" + fileExtension);
        }

        return configurationLoaderBuilder
                .get()
                .file(file)
                .defaultOptions(options -> options.serializers(SLibSerializers::appendSerializers))
                .build();
    }
}
