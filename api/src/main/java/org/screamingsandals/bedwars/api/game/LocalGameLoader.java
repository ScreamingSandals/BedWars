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
import java.util.concurrent.CompletableFuture;

/**
 * LocalGameLoader API to load or save a game to or from a file.
 *
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface LocalGameLoader {

    /**
     * NOTE: The plugin may postpone the game loading to the next tick if it cannot locate the world on the first try.
     * This operation runs synchronized with the game global thread, so do not try to invoke {@link CompletableFuture#get()} from that thread!
     *
     * @param file a file containing the game data
     * @return a completable future returning game instance populated from the given file or null
     */
    default @NotNull CompletableFuture<? extends @Nullable LocalGame> loadGame(@NotNull File file) {
        return loadGame(file, true);
    }

    /**
     * NOTE: The plugin may postpone the game loading to the next tick if it cannot locate the world on the first try.
     * This operation runs synchronized with the game global thread, so do not try to invoke {@link CompletableFuture#get()} from that thread!
     * If the parameter {@code firstAttempt} is {@code false}, the plugin will not try to postpone the loading and will instead return already completed future.
     *
     * @param file a file containing the game data
     * @param firstAttempt provide true to attempt a sequential load after a failed one as a fix for world loading.
     * @return a completable future returning loaded game instance from the provided file or null
     */
    @NotNull CompletableFuture<? extends @Nullable LocalGame> loadGame(@NotNull File file, boolean firstAttempt);

    /**
     *
     * @param game the game instance to be serialized into a file
     */
    void saveGame(@NotNull LocalGame game);
}