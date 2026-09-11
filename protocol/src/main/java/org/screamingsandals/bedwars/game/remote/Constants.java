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

package org.screamingsandals.bedwars.game.remote;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Constants {
    public static final @NotNull String MESSAGING_CHANNEL = "ScreamingSandalsMinigameV1";
    public static final int PROTOCOL_VERSION = 1;

    // Upper bound (bytes) for a single framed packet, to prevent remote OOM from an oversized length.
    public static final int MAX_PACKET_SIZE = 2 * 1024 * 1024; // 2 MiB

    // Upper bound for element counts read from untrusted packet data.
    public static final int MAX_PREALLOC_ELEMENTS = 8192;
}
