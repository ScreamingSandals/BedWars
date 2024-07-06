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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class GameListPacket implements Packet {
    private @NotNull String server;
    private @NotNull List<@NotNull GameEntry> games;

    public GameListPacket(@NotNull DataInputStream dataInputStream) throws IOException {
        server = PacketUtils.readStandardUTF(dataInputStream);
        int size = dataInputStream.readInt();
        games = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            var uuid = PacketUtils.readUuid(dataInputStream);
            var name = PacketUtils.readStandardUTF(dataInputStream);
            @Nullable String displayName;
            if (dataInputStream.readBoolean()) {
                displayName = PacketUtils.readStandardUTF(dataInputStream);
            } else {
                displayName = null;
            }
            games.add(new GameEntry(uuid, name, displayName));
        }
    }

    @Override
    public void write(@NotNull DataOutputStream dataOutputStream) throws IOException {
        PacketUtils.writeStandardUTF(dataOutputStream, server);
        dataOutputStream.writeInt(games.size());
        for (var game : games) {
            PacketUtils.writeUuid(dataOutputStream, game.getUuid());
            PacketUtils.writeStandardUTF(dataOutputStream, game.getName());
            var displayName = game.getDisplayName();
            dataOutputStream.writeBoolean(displayName != null);
            if (displayName != null) {
                PacketUtils.writeStandardUTF(dataOutputStream, displayName);
            }
        }
    }

    @Data
    public static class GameEntry {
        private final @NotNull UUID uuid;
        private final @NotNull String name;
        private final @Nullable String displayName;
    }
}
