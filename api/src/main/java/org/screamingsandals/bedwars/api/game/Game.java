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
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.api.types.ComponentHolder;

import java.util.UUID;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 * @see LocalGame for locally configured games
 * @see RemoteGame for registered games on another bungeecord servers
 */
@ApiStatus.NonExtendable
public interface Game {
    /**
     *
     * @return arena's unique id
     */
    @NotNull UUID getUuid();

    /**
     * @return Arena name
     */
    @NotNull String getName();

    /**
     * @return GameStatus of the arena
     */
    @NotNull GameStatus getStatus();

    /**
     * @return true if GameStatus is different than DISABLED
     */
    default boolean isActivated() {
        return getStatus() != GameStatus.DISABLED;
    }

    /**
     * @return configured minimal players to start the game
     */
    int getMinPlayers();

    /**
     * @return configured maximal players of the arena
     */
    int getMaxPlayers();

    @NotNull ComponentHolder getDisplayNameComponent();

    // PLAYER MANAGEMENT

    /**
     * @param player
     */
    void joinToGame(@NotNull BWPlayer player);

    /**
     * @return players in game
     */
    int countConnectedPlayers();

    /**
     * @return count of players currently playing in any team or waiting in lobby before the game starts
     */
    int countAlive();

    // TEAMS

    /**
     * @return
     */
    int countAvailableTeams();

    /**
     * @return
     */
    int countActiveTeams();
}
