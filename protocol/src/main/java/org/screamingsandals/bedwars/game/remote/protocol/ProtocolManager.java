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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// TODO: custom sockets and redis
public abstract class ProtocolManager {
    protected void processIncoming(@NotNull DataInputStream input) throws Exception {
        int packetIdInt = input.readUnsignedByte();
        var packetId = PacketId.byId(packetIdInt);
        if (packetId == null) {
            throw new RuntimeException("Unknown BedWars packet ID " + packetIdInt);
        }
        var packet = packetId.read(input);

        receivePacket0(packet);
    }

    public void sendPacket(@NotNull String server, @NotNull Packet packet) throws IOException {
        sendPacket0(server, encodePacket(packet));
    }

    public void broadcastPacket(@NotNull Packet packet) throws IOException {
        broadcastPacket0(encodePacket(packet));
    }

    private byte @NotNull [] encodePacket(@NotNull Packet packet) throws IOException {
        var out = new ByteArrayOutputStream();
        var dout = new DataOutputStream(out);

        PacketId packetId = PacketId.byClass(packet.getClass());
        if (packetId == null) {
            throw new IllegalArgumentException("Unknown packet passed: " + packet.getClass().getName());
        }
        dout.write(packetId.getId());
        packet.write(dout);

        return out.toByteArray();
    }

    protected abstract void sendPacket0(@NotNull String server, byte @NotNull [] payload) throws IOException;

    protected abstract void broadcastPacket0(byte @NotNull [] payload) throws IOException;

    protected abstract void receivePacket0(@NotNull Packet packet);
}
