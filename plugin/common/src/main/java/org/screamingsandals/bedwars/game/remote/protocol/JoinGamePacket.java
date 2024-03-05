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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.remote.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JoinGamePacket implements Packet {
    private UUID playerUuid;
    private String gameIdentifier;
    private @Nullable String sendingHub;

    @Override
    public int packetId() {
        return Constants.JOIN_GAME_PACKET_ID;
    }

    @Override
    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(playerUuid.toString());
        dataOutputStream.writeUTF(gameIdentifier);
        dataOutputStream.writeBoolean(sendingHub != null);
        if (sendingHub != null) {
            dataOutputStream.writeUTF(sendingHub);
        }
    }

    @Override
    public void read(DataInputStream dataInputStream) throws IOException {
        playerUuid = UUID.fromString(dataInputStream.readUTF());
        gameIdentifier = dataInputStream.readUTF();
        if (dataInputStream.readBoolean()) {
            sendingHub = dataInputStream.readUTF();
        }
    }
}
