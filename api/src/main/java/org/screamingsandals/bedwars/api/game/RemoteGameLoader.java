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

package org.screamingsandals.bedwars.api.game;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * RemoteGameLoader API to load or save a game to or from a file.
 *
 * @author ScreamingSandals
 * @since 0.3.0
 */
@ApiStatus.NonExtendable
public interface RemoteGameLoader {

    /**
     * @param file a file containing remote games
     * @return a collection of loaded remote games
     * @since 0.3.0
     */
    @NotNull List<? extends @NotNull RemoteGame> loadGames(@NotNull File file);

    /**
     * @param game the game instance to be serialized into a file
     * @since 0.3.0
     * @throws IllegalArgumentException if the game is created as non-persistent or is a custom implementation
     */
    void saveGame(@NotNull RemoteGame game);
}