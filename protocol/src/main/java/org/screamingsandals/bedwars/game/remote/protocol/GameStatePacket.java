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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class GameStatePacket implements Packet {
    private @NotNull String server;
    private @NotNull UUID uuid;
    private @NotNull String name;
    private @Nullable String displayName;
    private int onlinePlayers;
    private int minPlayers;
    private int maxPlayers;
    private int teams;
    private @NotNull String state;
    private @NotNull List<@NotNull PlayerEntry> players;
    private int elapsed;
    private @Nullable Integer maxTime;
    private @NotNull Instant generationTime;

    public GameStatePacket(@NotNull DataInputStream dataInputStream) throws IOException {
        server = PacketUtils.readStandardUTF(dataInputStream);
        uuid = PacketUtils.readUuid(dataInputStream);
        name = PacketUtils.readStandardUTF(dataInputStream);
        if (dataInputStream.readBoolean()) {
            displayName = PacketUtils.readStandardUTF(dataInputStream);
        }
        onlinePlayers = dataInputStream.readInt();
        minPlayers = dataInputStream.readInt();
        maxPlayers = dataInputStream.readInt();
        teams = dataInputStream.readInt();
        state = PacketUtils.readStandardUTF(dataInputStream);
        int playersSize = dataInputStream.readInt();
        players = new ArrayList<>();
        for (int i = 0; i < playersSize; i++) {
            var uuid = PacketUtils.readUuid(dataInputStream);
            var name = PacketUtils.readStandardUTF(dataInputStream);
            players.add(new PlayerEntry(uuid, name));
        }
        elapsed = dataInputStream.readInt();
        if (dataInputStream.readBoolean()) {
            maxTime = dataInputStream.readInt();
        }
        generationTime = Instant.ofEpochSecond(dataInputStream.readLong());
    }

    @Override
    public void write(@NotNull DataOutputStream dataOutputStream) throws IOException {
        PacketUtils.writeStandardUTF(dataOutputStream, server);
        PacketUtils.writeUuid(dataOutputStream, uuid);
        PacketUtils.writeStandardUTF(dataOutputStream, name);
        dataOutputStream.writeBoolean(displayName != null);
        if (displayName != null) {
            PacketUtils.writeStandardUTF(dataOutputStream, displayName);
        }
        dataOutputStream.writeInt(onlinePlayers);
        dataOutputStream.writeInt(minPlayers);
        dataOutputStream.writeInt(maxPlayers);
        dataOutputStream.writeInt(teams);
        dataOutputStream.writeInt(players.size());
        PacketUtils.writeStandardUTF(dataOutputStream, state);
        for (var player : players) {
            PacketUtils.writeUuid(dataOutputStream, player.getUuid());
            PacketUtils.writeStandardUTF(dataOutputStream, player.getName());
        }
        dataOutputStream.writeInt(elapsed);
        dataOutputStream.writeBoolean(maxTime != null);
        if (maxTime != null) {
            dataOutputStream.writeInt(maxTime);
        }
        dataOutputStream.writeLong(generationTime.getEpochSecond());
    }

    @Data
    public static class PlayerEntry {
        private final @NotNull UUID uuid;
        private final @NotNull String name;
    }
}
