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

package org.screamingsandals.bedwars.game.remote.protocol.sockets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Action {
    /* KEEP ORDER! DO NOT REMOVE ANY ACTION */
    BROADCAST_PACKET,
    SEND_PACKET,
    IGNORE_INCOMING_GAME_STATE,
    STOP_IGNORING_INCOMING_GAME_STATE;

    private static final @NotNull Action @NotNull [] VALUES = values();

    public static @Nullable Action byId(byte id) {
        if (id < 0 || id > VALUES.length) {
            return null;
        }

        return VALUES[id];
    }

    public byte getId() {
        return (byte) ordinal();
    }
}
