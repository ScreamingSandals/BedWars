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

package org.screamingsandals.bedwars.game.remote.protocol.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.remote.protocol.PacketUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Getter
public class GameListRequestPacket implements Packet {
    private final @Nullable String requestingServer;

    public GameListRequestPacket(@NotNull DataInputStream dataInputStream) throws IOException {
        if (dataInputStream.readBoolean()) {
            this.requestingServer = PacketUtils.readStandardUTF(dataInputStream);
        } else {
            this.requestingServer = null;
        }
    }

    @Override
    public void write(@NotNull DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeBoolean(requestingServer != null);
        if (requestingServer != null) {
            PacketUtils.writeStandardUTF(dataOutputStream, requestingServer);
        }
    }
}
