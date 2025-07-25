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

package org.screamingsandals.bedwars.api.player;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.LocalGame;
import org.screamingsandals.lib.api.Wrapper;

import java.util.UUID;

@ApiStatus.NonExtendable
public interface BWPlayer extends Wrapper {
    UUID getUuid();


    /**
     * Checks if the player is a spectator
     *
     * @return true if player is spectating a game
     * @since 0.3.0
     */
    boolean isSpectator();

    @Nullable
    String getLatestGameName();

    boolean isInGame();

    boolean canJoinFullGame();

    @Nullable
    LocalGame getGame();
}
