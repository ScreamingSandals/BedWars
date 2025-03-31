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

package org.screamingsandals.bedwars.api.game;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface GroupManager {
    /**
     *
     * @param group name of the group
     * @param game the game that should be added to the group
     * @return true if successful
     * @since 0.3.0
     */
    boolean addToGroup(@Pattern("[a-zA-Z\\d\\-_]+") @NotNull String group, @NotNull Game game);

    /**
     *
     * @param group name of the group
     * @param game the game that should be removed from the group
     * @return true if successful
     * @since 0.3.0
     */
    boolean removeFromGroup(@Pattern("[a-zA-Z\\d\\-_]+") @NotNull String group, @NotNull Game game);

    /**
     *
     * @param group name of the group
     * @return list of all games inside the group
     * @since 0.3.0
     */
    @NotNull
    List<? extends @NotNull Game> getGamesInGroup(@Pattern("[a-zA-Z\\d\\-_]+") @NotNull String group);

    /**
     *
     * @return all groups that have at least one game
     * @since 0.3.0
     */
    List<String> getExistingGroups();
}
