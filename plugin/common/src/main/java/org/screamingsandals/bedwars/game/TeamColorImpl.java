/*
 * Copyright (C) 2022 ScreamingSandals
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

package org.screamingsandals.bedwars.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.spectator.Color;

@RequiredArgsConstructor
public enum TeamColorImpl implements TeamColor {
    BLACK(Color.BLACK, 0xF, "BLACK", Color.rgb(0, 0, 0)),
    BLUE(Color.DARK_BLUE, 0xB, "BLUE", Color.rgb(0, 0, 170)),
    GREEN(Color.DARK_GREEN, 0xD, "GREEN", Color.rgb(0, 170, 0)),
    RED(Color.RED, 0xE, "RED", Color.rgb(255, 85, 85)),
    MAGENTA(Color.DARK_PURPLE, 0x2, "MAGENTA", Color.rgb(170, 0, 170)),
    ORANGE(Color.GOLD, 0x1, "ORANGE", Color.rgb(255, 170, 0)),
    LIGHT_GRAY(Color.GRAY, 0x8, "LIGHT_GRAY", Color.rgb(170, 170, 170)),
    GRAY(Color.DARK_GRAY, 0x7, "GRAY", Color.rgb(85, 85, 85)),
    LIGHT_BLUE(Color.BLUE, 0x3, "LIGHT_BLUE", Color.rgb(85, 85, 255)),
    LIME(Color.GREEN, 0x5, "LIME", Color.rgb(85, 255, 85)),
    CYAN(Color.AQUA, 0x9, "CYAN", Color.rgb(85, 255, 255)),
    PINK(Color.LIGHT_PURPLE, 0x6, "PINK", Color.rgb(255, 85, 255)),
    YELLOW(Color.YELLOW, 0x4, "YELLOW", Color.rgb(255, 255, 85)),
    WHITE(Color.WHITE, 0x0, "WHITE", Color.rgb(255, 255, 255)),
    BROWN(Color.DARK_RED, 0xC, "BROWN", Color.rgb(139, 69, 19));

    @Getter
    private final Color textColor;
    @Deprecated
    public final int woolData;
    public final String material1_13;
    @Getter
    private final Color leatherColor;

    public BlockTypeHolder getWoolBlockType() {
        return BlockTypeHolder.of("WOOL").colorize(material1_13);
    }
}
