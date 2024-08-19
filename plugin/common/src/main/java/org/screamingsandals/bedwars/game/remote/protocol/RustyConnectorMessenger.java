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

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.remote.Constants;
import org.screamingsandals.bedwars.game.remote.protocol.messaging.Messenger;
import org.screamingsandals.bedwars.utils.RustyConnectorUtils;

import java.io.*;

@RequiredArgsConstructor
public final class RustyConnectorMessenger implements Messenger {

    @Override
    public void sendPacket(@NotNull String server, byte @NotNull [] payload) {
        RustyConnectorUtils.packetToServer(server, payload);
    }

    @Override
    public void broadcastPacket(byte @NotNull [] payload) {
        RustyConnectorUtils.packetBroadcast(payload);
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
