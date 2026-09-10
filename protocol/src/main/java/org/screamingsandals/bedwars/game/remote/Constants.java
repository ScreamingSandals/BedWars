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

    /**
     * Hard upper bound (in bytes) for a single framed packet accepted from the network.
     * Prevents a peer from declaring an arbitrarily large length and forcing the receiver
     * to allocate/buffer gigabytes of memory (remote denial of service).
     */
    public static final int MAX_PACKET_SIZE = 2 * 1024 * 1024; // 2 MiB

    /**
     * Sanity cap for the initial capacity of collections sized from untrusted packet data.
     * The real number of elements is still bounded by the frame size while decoding, but this
     * stops a declared element count from triggering a huge up-front allocation.
     */
    public static final int MAX_PREALLOC_ELEMENTS = 8192;
}
