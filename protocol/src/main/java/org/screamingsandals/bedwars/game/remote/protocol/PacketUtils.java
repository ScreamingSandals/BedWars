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

package org.screamingsandals.bedwars.game.remote.protocol;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@UtilityClass
public class PacketUtils {
    public static @NotNull String readStandardUTF(@NotNull DataInputStream inputStream) throws IOException {
        return new String(inputStream.readNBytes(inputStream.readUnsignedShort()), StandardCharsets.UTF_8);
    }

    public static void writeStandardUTF(@NotNull DataOutputStream outputStream, @NotNull String payload) throws IOException {
        var bytes = payload.getBytes(StandardCharsets.UTF_8);
        outputStream.writeShort(bytes.length);
        outputStream.write(bytes);
    }

    public static @NotNull UUID readUuid(@NotNull DataInputStream inputStream) throws IOException {
        return new UUID(inputStream.readLong(), inputStream.readLong());
    }

    public static void writeUuid(@NotNull DataOutputStream outputStream, @NotNull UUID uuid) throws IOException {
        outputStream.writeLong(uuid.getMostSignificantBits());
        outputStream.writeLong(uuid.getLeastSignificantBits());
    }
}
