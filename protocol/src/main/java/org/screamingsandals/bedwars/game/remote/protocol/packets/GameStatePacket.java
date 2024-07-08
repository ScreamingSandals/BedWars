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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.remote.protocol.PacketUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(builderClassName = "Builder")
@Getter
public class GameStatePacket implements Packet {
    private final @NotNull String server;
    private final @NotNull UUID uuid;
    private final @NotNull String name;
    private final @Nullable String displayName;
    private final int onlinePlayers;
    private final int alivePlayers;
    private final int minPlayers;
    private final int maxPlayers;
    private final int teams;
    private final int aliveTeams;
    private final @NotNull String state;
    private final @NotNull List<@NotNull PlayerEntry> players;
    private final int elapsed;
    private final @Nullable Integer maxTime;
    private long generationTime = 0; // millis

    public GameStatePacket(@NotNull DataInputStream dataInputStream) throws IOException {
        server = PacketUtils.readStandardUTF(dataInputStream);
        uuid = PacketUtils.readUuid(dataInputStream);
        name = PacketUtils.readStandardUTF(dataInputStream);
        if (dataInputStream.readBoolean()) {
            displayName = PacketUtils.readStandardUTF(dataInputStream);
        } else {
            displayName = null;
        }
        onlinePlayers = dataInputStream.readInt();
        alivePlayers = dataInputStream.readInt();
        minPlayers = dataInputStream.readInt();
        maxPlayers = dataInputStream.readInt();
        teams = dataInputStream.readInt();
        aliveTeams = dataInputStream.readInt();
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
        } else {
            maxTime = null;
        }
        generationTime = dataInputStream.readLong();
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
        dataOutputStream.writeInt(alivePlayers);
        dataOutputStream.writeInt(minPlayers);
        dataOutputStream.writeInt(maxPlayers);
        dataOutputStream.writeInt(teams);
        dataOutputStream.writeInt(aliveTeams);
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
        dataOutputStream.writeLong(System.currentTimeMillis());
    }

    @Data
    public static class PlayerEntry {
        private final @NotNull UUID uuid;
        private final @NotNull String name;
    }
}
