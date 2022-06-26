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

package org.screamingsandals.bedwars.api.game.target;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface TargetCountdown extends Target {

    /**
     * Gets the set countdown to the target invalidation.
     *
     * @return the countdown in seconds
     * @since 0.3.0
     */
    int getCountdown();

    /**
     * Gets the current remaining time until the target is invalidated.
     *
     * @return the current remaining time in seconds
     * @since 0.3.0
     */
    int getRemainingTime();
}
