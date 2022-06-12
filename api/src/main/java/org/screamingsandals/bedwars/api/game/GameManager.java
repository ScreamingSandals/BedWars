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

package org.screamingsandals.bedwars.api.game;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApiStatus.NonExtendable
public interface GameManager {
    /**
     * @param name Name of game or string representation of an uuid
     * @return Optional with game or empty if game does not exist
     * @see #getGame(UUID)
     */
    Optional<? extends Game> getGame(String name);

    /**
     * @param uuid Unique id of the game
     * @return Optional with the game or empty if the game does not exist
     */
    Optional<? extends Game> getGame(UUID uuid);

    /**
     * @return List of available games
     */
    List<? extends Game> getGames();

    /**
     * @return List of names of all game
     */
    List<String> getGameNames();

    /**
     * @param name Name of game or string representation of an uuid
     * @return true if the game exists
     */
    boolean hasGame(String name);

    /**
     * @param uuid Unique id of the game
     * @return true if the game exists
     */
    boolean hasGame(UUID uuid);

    /**
     * @return Free game that has the highest players in it or empty optional
     */
    Optional<? extends Game> getGameWithHighestPlayers(boolean fee);

    /**
     * @return Free game that has the lowest players in it or empty optional
     */
    Optional<? extends Game> getGameWithLowestPlayers(boolean fee);

    /**
     * @return Game in waiting state or empty optional
     */
    Optional<? extends Game> getFirstWaitingGame(boolean fee);

    /**
     * @return Game in running state or empty optional
     */
    Optional<? extends Game> getFirstRunningGame(boolean fee);

    Optional<? extends Game> getGameWithHighestPlayers();

    Optional<? extends Game> getGameWithLowestPlayers();

    Optional<? extends Game> getFirstWaitingGame();

    Optional<? extends Game> getFirstRunningGame();
}
