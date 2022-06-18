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

package org.screamingsandals.bedwars.config;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.config.Configuration;
import org.screamingsandals.bedwars.api.config.ConfigurationKey;

@AllArgsConstructor
public class ConfigurationImpl<T> implements Configuration<T> {
    private final ConfigurationKey<T> key;
    private final ConfigurationContainerImpl configurationContainer;
    private final T implicitValue;

    @Override
    public T get() {
        if (!isSet()) {
            if (configurationContainer.getParentContainer() != null) {
                return configurationContainer.getParentContainer().getOrDefault(key, implicitValue);
            }
            return implicitValue;
        }
        return configurationContainer.getSaved(key);
    }

    @Override
    @Nullable
    public T getExactValue() {
        return configurationContainer.getSaved(key);
    }

    @Override
    public boolean isSet() {
        return configurationContainer.has(key.getKey());
    }

    @Override
    public void set(T value) {
        configurationContainer.update(key.getKey(), value);
    }

    @Override
    public void clear() {
        configurationContainer.remove(key.getKey());
    }
}
