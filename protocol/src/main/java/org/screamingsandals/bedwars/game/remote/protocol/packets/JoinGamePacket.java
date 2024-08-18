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

import lombok.Builder;
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
@Builder(builderClassName = "Builder")
@Getter
public class JoinGamePacket implements Packet {
    private final @NotNull UUID playerUuid;
    private final @NotNull String gameIdentifier;
    private final @Nullable String sendingHub;

    // Following fields are currently unused but may be useful in the future
    private final @Nullable String partyIdentifier;
    private final @Nullable List<@NotNull UUID> partyMembers;
    private final @Nullable UUID partyLeader;
    private final @Nullable String partyPlugin;

    public JoinGamePacket(@NotNull UUID playerUuid, @NotNull String gameIdentifier, @Nullable String sendingHub) {
        this(playerUuid, gameIdentifier, sendingHub, null, null, null, null);
    }

    public JoinGamePacket(@NotNull DataInputStream dataInputStream) throws IOException {
        playerUuid = PacketUtils.readUuid(dataInputStream);
        gameIdentifier = PacketUtils.readStandardUTF(dataInputStream);
        if (dataInputStream.readBoolean()) {
            sendingHub = PacketUtils.readStandardUTF(dataInputStream);
        } else {
            sendingHub = null;
        }

        if (dataInputStream.readBoolean()) {
            partyIdentifier = PacketUtils.readStandardUTF(dataInputStream);
        } else {
            partyIdentifier = null;
        }
        if (dataInputStream.readBoolean()) {
            partyMembers = new ArrayList<>();
            var size = dataInputStream.readInt();
            for (int i = 0; i < size; i++) {
                partyMembers.add(PacketUtils.readUuid(dataInputStream));
            }
        } else {
            partyMembers = null;
        }
        if (dataInputStream.readBoolean()) {
            partyLeader = PacketUtils.readUuid(dataInputStream);
        } else {
            partyLeader = null;
        }
        if (dataInputStream.readBoolean()) {
            partyPlugin = PacketUtils.readStandardUTF(dataInputStream);
        } else {
            partyPlugin = null;
        }
    }

    @Override
    public void write(@NotNull DataOutputStream dataOutputStream) throws IOException {
        PacketUtils.writeUuid(dataOutputStream, playerUuid);
        PacketUtils.writeStandardUTF(dataOutputStream, gameIdentifier);
        dataOutputStream.writeBoolean(sendingHub != null);
        if (sendingHub != null) {
            PacketUtils.writeStandardUTF(dataOutputStream, sendingHub);
        }

        dataOutputStream.writeBoolean(partyIdentifier != null);
        if (partyIdentifier != null) {
            PacketUtils.writeStandardUTF(dataOutputStream, partyIdentifier);
        }
        dataOutputStream.writeBoolean(partyMembers != null);
        if (partyMembers != null) {
            dataOutputStream.writeInt(partyMembers.size());
            for (var uuid : partyMembers) {
                PacketUtils.writeUuid(dataOutputStream, uuid);
            }
        }
        dataOutputStream.writeBoolean(partyLeader != null);
        if (partyLeader != null) {
            PacketUtils.writeUuid(dataOutputStream, partyLeader);
        }
        dataOutputStream.writeBoolean(partyPlugin != null);
        if (partyPlugin != null) {
            PacketUtils.writeStandardUTF(dataOutputStream, partyPlugin);
        }
    }
}
