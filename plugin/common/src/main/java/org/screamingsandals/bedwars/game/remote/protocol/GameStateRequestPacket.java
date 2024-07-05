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
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.game.remote.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameStateRequestPacket implements Packet {
    private @NotNull String gameIdentifier;

    @Override
    public int packetId() {
        return Constants.GAME_STATE_REQUEST_PACKET_ID;
    }

    @Override
    public void write(@NotNull DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(gameIdentifier);
    }

    @Override
    public void read(@NotNull DataInputStream dataInputStream) throws IOException {
        gameIdentifier = dataInputStream.readUTF();
    }
}
