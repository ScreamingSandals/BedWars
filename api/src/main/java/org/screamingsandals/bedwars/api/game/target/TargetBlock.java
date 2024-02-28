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

package org.screamingsandals.bedwars.api.game.target;

import org.jetbrains.annotations.NotNull;
import org.screamingsandals.lib.api.types.server.LocationHolder;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface TargetBlock extends Target {

    /**
     * Gets the team's target block (e.g. bed) location.
     *
     * @return the target block location
     * @since 0.3.0
     */
    @NotNull LocationHolder getTargetBlock();

    /**
     * Checks if the block is Respawn Anchor (or another chargeable block supported by the plugin) and if it's empty (not charged).
     *
     * @return true if the block is empty; false otherwise
     * @since 0.3.0
     */
    boolean isEmpty();

    /**
     * Gets charge of the Respawn Anchor or other supported chargeable block; if the block is not considered to be chargeable, 1 is returned if the block is intact, 0 otherwise.
     *
     * @return the charge
     * @since 0.3.0
     */
    int getCharge();
}
