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

package org.screamingsandals.bedwars.game.remote.protocol.messaging;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.remote.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class BungeeCordMessenger implements Messenger {
    private final @NotNull Consumer<byte @NotNull []> packetSender;

    @Override
    public void sendPacket(@NotNull String server, byte @NotNull [] payload) throws IOException {
        var bout = new ByteArrayOutputStream();
        var bungeeDout = new DataOutputStream(bout);
        bungeeDout.writeUTF("Forward");
        bungeeDout.writeUTF(server);
        bungeeDout.writeUTF(Constants.MESSAGING_CHANNEL);
        bungeeDout.writeShort(payload.length);
        bungeeDout.write(payload);

        packetSender.accept(bout.toByteArray());
    }

    @Override
    public void broadcastPacket(byte @NotNull [] payload) throws IOException {
        var bout = new ByteArrayOutputStream();
        var bungeeDout = new DataOutputStream(bout);
        bungeeDout.writeUTF("Forward");
        bungeeDout.writeUTF("ONLINE");
        bungeeDout.writeUTF(Constants.MESSAGING_CHANNEL);
        bungeeDout.writeShort(payload.length);
        bungeeDout.write(payload);

        packetSender.accept(bout.toByteArray());
    }

    @Override
    public byte @Nullable [] incomingPacketTransformer(byte @NotNull [] payload) throws IOException {
        var outerIn = new DataInputStream(new ByteArrayInputStream(payload));
        if (!Constants.MESSAGING_CHANNEL.equals(outerIn.readUTF())) {
            return null;
        }

        return outerIn.readNBytes(outerIn.readShort());
    }
}
