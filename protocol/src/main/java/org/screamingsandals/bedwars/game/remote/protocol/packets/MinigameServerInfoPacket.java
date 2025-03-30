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
import org.screamingsandals.bedwars.game.remote.Constants;
import org.screamingsandals.bedwars.game.remote.protocol.PacketUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Getter
public class MinigameServerInfoPacket implements Packet {
    private final @NotNull String server;
    private final @NotNull String plugin;
    private final @NotNull String version;
    private final int protocolVersion;

    public MinigameServerInfoPacket(@NotNull String server, @NotNull String plugin, @NotNull String version) {
        this(server, plugin, version, Constants.PROTOCOL_VERSION);
    }

    public MinigameServerInfoPacket(@NotNull DataInputStream dataInputStream) throws IOException {
        server = PacketUtils.readStandardUTF(dataInputStream);
        plugin = PacketUtils.readStandardUTF(dataInputStream);
        version = PacketUtils.readStandardUTF(dataInputStream);
        protocolVersion = dataInputStream.readInt();
    }

    @Override
    public void write(@NotNull DataOutputStream dataOutputStream) throws IOException {
        PacketUtils.writeStandardUTF(dataOutputStream, server);
        PacketUtils.writeStandardUTF(dataOutputStream, plugin);
        PacketUtils.writeStandardUTF(dataOutputStream, version);
        dataOutputStream.writeInt(protocolVersion);
    }
}
