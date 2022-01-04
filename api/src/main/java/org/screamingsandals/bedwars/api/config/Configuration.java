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

package org.screamingsandals.bedwars.api.config;

import org.jetbrains.annotations.ApiStatus;

/**
 * Saves and retrieves configuration object
 *
 * @param <T> type of saved object
 * @author ScreamingSandals
 * @since 0.3.0
 */
@ApiStatus.NonExtendable
public interface Configuration<T> {
    /**
     * Gets current value, can be inherited from another configuration
     *
     * @return Current value
     */
    T get();

    /**
     * Check if current configuration object contains custom value
     *
     * @return true if value is set
     */
    boolean isSet();

    /**
     * Sets new value for that configuration
     *
     * @param value Value you wish to save
     */
    void set(T value);

    /**
     * Resets current value of this configuration
     */
    void clear();
}
