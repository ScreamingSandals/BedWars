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
import org.jetbrains.annotations.Nullable;

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
     * @return List of available local games
     */
    List<? extends LocalGame> getLocalGames();

    /**
     * @return List of available remote games
     */
    List<? extends RemoteGame> getRemoteGames();

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

    /**
     * Registers provided remote game. This method should be used only with custom implementations of {@link RemoteGame}.
     *
     * @param remoteGame custom instance implementing {@link RemoteGame}
     * @throws IllegalStateException if {@link RemoteGame#getUuid()} returns an already registered UUID
     * @see #createNewRemoteGame(boolean, String, String, String)
     * @see #createNewRemoteGame(boolean, UUID, String, String, String)
     * @since 0.3.0
     */
    void registerRemoteGame(@NotNull RemoteGame remoteGame);

    /**
     * Unregisters a remote game from the game manager.
     *
     * @param remoteGame remote game instance
     * @since 0.3.0
     */
    void unregisterRemoteGame(@NotNull RemoteGame remoteGame);

    /**
     * Creates a new {@link RemoteGame} with randomly generated {@link UUID}.
     *
     * @param persistent true if the game should be saved in the config file
     * @param name local name of the remote game
     * @param remoteServer name of the server hosting the game
     * @param remoteGameIdentifier name or uuid (as hyphenated string) of the game on that server or null. If this parameter is null, the created instance represents a remote game in legacy mode.
     * @return new remote game instance
     * @see #createNewRemoteGame(boolean, UUID, String, String, String)
     * @since 0.3.0
     */
    @NotNull RemoteGame createNewRemoteGame(boolean persistent, @NotNull String name, @NotNull String remoteServer, @Nullable String remoteGameIdentifier);

    /**
     * Creates a new {@link RemoteGame}.
     *
     * @param persistent true if the game should be saved in the config file
     * @param uuid local uuid of the remote game
     * @param name local name of the remote game
     * @param remoteServer name of the server hosting the game
     * @param remoteGameIdentifier name or uuid (as hyphenated string) of the game on that server or null. If this parameter is null, the created instance represents a remote game in legacy mode.
     * @return new remote game instance
     * @throws IllegalStateException if the parameter {@code uuid} is not unique
     * @see #createNewRemoteGame(boolean, String, String, String)
     * @since 0.3.0
     */
    @NotNull RemoteGame createNewRemoteGame(boolean persistent, @NotNull UUID uuid, @NotNull String name, @NotNull String remoteServer, @Nullable String remoteGameIdentifier);


}
