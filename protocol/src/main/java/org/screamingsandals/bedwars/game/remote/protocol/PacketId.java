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

package org.screamingsandals.bedwars.game.remote.protocol;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;

public enum PacketId {
    // When editing, do NOT: change order, add new elements between existing elements, remove existing elements
    JOIN_GAME(JoinGamePacket.class, JoinGamePacket::new),
    GAME_STATE(GameStatePacket.class, GameStatePacket::new),
    GAME_LIST(GameListPacket.class, GameListPacket::new),
    GAME_STATE_REQUEST(GameStateRequestPacket.class, GameStateRequestPacket::new),
    GAME_LIST_REQUEST(GameListRequestPacket.class, GameListRequestPacket::new),
    MINIGAME_SERVER_INFO(MinigameServerInfoPacket.class, MinigameServerInfoPacket::new),
    MINIGAME_SERVER_INFO_REQUEST(MinigameServerInfoRequestPacket.class, MinigameServerInfoRequestPacket::new);

    private static final @NotNull PacketId @NotNull [] VALUES = values();

    private final @NotNull Class<? extends Packet> packetClass;
    private final @NotNull PacketConstructor<? extends Packet> constructor;

    <T extends Packet> PacketId(@NotNull Class<T> packetClass, @NotNull PacketConstructor<T> constructor) {
        this.packetClass = packetClass;
        this.constructor = constructor;
    }

    public static @Nullable PacketId byId(int id) {
        if (id < 0 || id > VALUES.length) {
            return null;
        }

        return VALUES[id];
    }
    public static @Nullable PacketId byClass(@NotNull Class<? extends Packet> packetClass) {
        for (var value : VALUES) {
            if (value.packetClass == packetClass) {
                return value;
            }
        }

        return null;
    }

    public int getId() {
        return ordinal();
    }

    public Packet read(@NotNull DataInputStream in) throws IOException {
        return constructor.construct(in);
    }

    @FunctionalInterface
    private interface PacketConstructor<T extends Packet> {
        @NotNull T construct(@NotNull DataInputStream in) throws IOException;
    }
}
