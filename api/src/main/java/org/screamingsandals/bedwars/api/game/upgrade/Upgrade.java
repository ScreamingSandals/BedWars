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

package org.screamingsandals.bedwars.api.game.upgrade;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * EXPERIMENTAL: Upgrade API is subject to change.
 *
 * @author ScreamingSandals
 * @since 0.3.0
 */
@ApiStatus.NonExtendable
@ApiStatus.Experimental
public interface Upgrade {
    /**
     * Gets the current level of this upgrade.
     *
     * @return current level of upgrade
     * @since 0.3.0
     */
    double getLevel();

    /**
     * Sets level of this upgrade
     *
     * @param level Current level
     * @since 0.3.0
     */
    void setLevel(double level);

    /**
     * Add levels to this upgrade
     *
     * @param level Levels that will be added to current level
     * @since 0.3.0
     */
    void increaseLevel(double level);

    /**
     * Gets the initial level of this upgrade.
     *
     * @return the initial level of the upgrade
     * @since 0.3.0
     */
    double getInitialLevel();

    /**
     * Gets the maximal level to which a player can upgrade. Plugins can bypass this level using {@link #setLevel(double)}, or {@link #increaseLevel(double)}
     *
     * @return the maximal level of the upgrade
     * @since 0.3.0
     */
    @Nullable Double getMaximalLevel();
}
