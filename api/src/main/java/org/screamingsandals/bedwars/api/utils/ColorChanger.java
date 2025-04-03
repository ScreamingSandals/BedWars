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

package org.screamingsandals.bedwars.api.utils;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.lib.api.types.server.ItemStackHolder;

/**
 * Utility class used for coloring items
 *
 * @author ScreamingSandals
 */
@ApiStatus.NonExtendable
public interface ColorChanger {

    /**
     * Apply color of team to ItemStack
     *
     * @param color Color of team
     * @param stack ItemStack that should be colored
     * @return colored ItemStack or normal ItemStack if ItemStack can't be colored
     */
    ItemStackHolder applyColor(TeamColor color, ItemStackHolder stack);
}
