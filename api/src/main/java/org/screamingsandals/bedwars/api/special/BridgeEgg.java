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

package org.screamingsandals.bedwars.api.special;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.lib.api.Wrapper;

@ApiStatus.NonExtendable
public interface BridgeEgg extends SpecialItem {
    /**
     * <p>Gets the bridge egg projectile.</p>
     *
     * @return the bridge egg projectile
     */
    Wrapper getProjectile();

    /**
     * <p>Gets the bridge material.</p>
     *
     * @return the bridge material
     */
    Wrapper getMaterial();

    /**
     * <p>Gets the bridge's max distance.</p>
     *
     * @return the bridge max distance
     */
    double getDistance();

    /**
     * <p>Runs the placing task.</p>
     */
    void runTask();
}
