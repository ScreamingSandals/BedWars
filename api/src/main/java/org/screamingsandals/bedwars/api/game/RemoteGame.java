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

import java.util.UUID;

/**
 * A remote game the players can connect to. Can be either identified only by the server or also by the specific arena
 * if the server supports arena selecting.
 *
 * @author ScreamingSandals
 * @since 0.3.0
 */
@ApiStatus.NonExtendable
public interface RemoteGame extends Game {
    /**
     * This methods allows you to save the arena to config (useful when using custom config options)
     *
     * @since 0.3.0
     */
    void saveToConfig();

    /**
     * Gets a name of the remote bungeecord or velocity server
     *
     * @return a name of the server
     * @since 0.3.0
     */
    @NotNull String getRemoteServer();

    /**
     * Sets a new name of the remote bungeecord or velocity server
     *
     * @param remoteServer a name of the server
     * @since 0.3.0
     */
    void setRemoteServer(@NotNull String remoteServer);

    /**
     * Gets unique identifier of the remote game
     *
     * @return a name of the remote game or its uuid
     * @since 0.3.0
     */
    @Nullable String getRemoteGameIdentifier();


    /**
     * Sets a unique identifier of the remote game
     *
     * @param remoteGameIdentifier a name of the remote game or its uuid or null
     * @since 0.3.0
     */
    void setRemoteGameIdentifier(@Nullable String remoteGameIdentifier);

    /**
     * Gets a game name as it is called on the remote server.
     *
     * @return remote game name or null if unknown
     * @since 0.3.0
     */
    @Nullable String getNameOnRemoteServer();

    /**
     * Gets a uuid used on the remote server to identify the game.
     *
     * @return remote game uuid or null if unknown
     * @since 0.3.0
     */
    @Nullable UUID getUuidOnRemoteServer();

    /**
     * Gets the max time based on the current game state.
     *
     * @return max time or null if unknown
     * @since 0.3.0
     */
    @Nullable Integer getMaxTimeInTheCurrentState();

    /**
     * Gets the currently elapsed time in the current state.
     *
     * @return elapsed time or null if unknown
     * @since 0.3.0
     */
    @Nullable Integer getElapsedTimeInCurrentState();

    /**
     * Gets a time left in the current state. This value is computed
     * using {@link #getMaxTimeInTheCurrentState()} and {@link #getElapsedTimeInCurrentState()}.
     *
     * @return time left or null if unknown
     * @since 0.3.0
     */
    @Nullable Integer getTimeLeftInCurrentState();
}
