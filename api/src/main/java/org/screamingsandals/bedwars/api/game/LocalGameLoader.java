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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * LocalGameLoader API to load or save a game to or from a file.
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface LocalGameLoader {

    /**
     *
     * @param file a file containing the game data
     * @return a game instance populated from the given file, null otherwise
     */
    @Nullable
    default LocalGame loadGame(File file) {
        return loadGame(file, true);
    }

    /**
     *
     * @param file a file containing the game data
     * @param firstAttempt provide true to attempt a sequential load after a failed one as a fix for world loading.
     * @return a loaded game instance from the provided file, null otherwise
     */
    @Nullable
    LocalGame loadGame(File file, boolean firstAttempt);

    /**
     *
     * @param game the game instance to be serialized into a file
     */
    void saveGame(@NotNull LocalGame game);
}