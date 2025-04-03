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

package org.screamingsandals.bedwars.game.remote.protocol.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.game.remote.protocol.PacketUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Getter
public class GameStateSubscribePacket implements Packet {
    private final @NotNull String requestingServer;
    private final @NotNull String gameIdentifier;
    private final boolean subscribe;

    public GameStateSubscribePacket(@NotNull DataInputStream dataInputStream) throws IOException {
        gameIdentifier = PacketUtils.readStandardUTF(dataInputStream);
        requestingServer = PacketUtils.readStandardUTF(dataInputStream);
        subscribe = dataInputStream.readBoolean();
    }

    @Override
    public void write(@NotNull DataOutputStream dataOutputStream) throws IOException {
        PacketUtils.writeStandardUTF(dataOutputStream, gameIdentifier);
        PacketUtils.writeStandardUTF(dataOutputStream, requestingServer);
        dataOutputStream.writeBoolean(subscribe);
    }
}
