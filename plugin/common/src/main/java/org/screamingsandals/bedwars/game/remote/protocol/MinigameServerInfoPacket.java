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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.game.remote.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class MinigameServerInfoPacket implements Packet {
    private @NotNull String server;
    private @NotNull String plugin;
    private @NotNull String version;
    private int protocolVersion = Constants.PROTOCOL_VERSION;

    @Override
    public int packetId() {
        return Constants.MINIGAME_SERVER_INFO_PACKET_ID;
    }

    @Override
    public void write(@NotNull DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(server);
        dataOutputStream.writeUTF(plugin);
        dataOutputStream.writeUTF(version);
        dataOutputStream.writeInt(protocolVersion);
    }

    @Override
    public void read(@NotNull DataInputStream dataInputStream) throws IOException {
        server = dataInputStream.readUTF();
        plugin = dataInputStream.readUTF();
        version = dataInputStream.readUTF();
        protocolVersion = dataInputStream.readInt();
    }
}
