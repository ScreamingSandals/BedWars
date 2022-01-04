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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.lib.block.BlockTypeHolder;

@RequiredArgsConstructor
public enum TeamColorImpl implements TeamColor {
    BLACK(NamedTextColor.BLACK, 0xF, "BLACK", TextColor.color(0, 0, 0)),
    BLUE(NamedTextColor.DARK_BLUE, 0xB, "BLUE", TextColor.color(0, 0, 170)),
    GREEN(NamedTextColor.DARK_GREEN, 0xD, "GREEN", TextColor.color(0, 170, 0)),
    RED(NamedTextColor.RED, 0xE, "RED", TextColor.color(255, 85, 85)),
    MAGENTA(NamedTextColor.DARK_PURPLE, 0x2, "MAGENTA", TextColor.color(170, 0, 170)),
    ORANGE(NamedTextColor.GOLD, 0x1, "ORANGE", TextColor.color(255, 170, 0)),
    LIGHT_GRAY(NamedTextColor.GRAY, 0x8, "LIGHT_GRAY", TextColor.color(170, 170, 170)),
    GRAY(NamedTextColor.DARK_GRAY, 0x7, "GRAY", TextColor.color(85, 85, 85)),
    LIGHT_BLUE(NamedTextColor.BLUE, 0x3, "LIGHT_BLUE", TextColor.color(85, 85, 255)),
    LIME(NamedTextColor.GREEN, 0x5, "LIME", TextColor.color(85, 255, 85)),
    CYAN(NamedTextColor.AQUA, 0x9, "CYAN", TextColor.color(85, 255, 255)),
    PINK(NamedTextColor.LIGHT_PURPLE, 0x6, "PINK", TextColor.color(255, 85, 255)),
    YELLOW(NamedTextColor.YELLOW, 0x4, "YELLOW", TextColor.color(255, 255, 85)),
    WHITE(NamedTextColor.WHITE, 0x0, "WHITE", TextColor.color(255, 255, 255)),
    BROWN(NamedTextColor.DARK_RED, 0xC, "BROWN", TextColor.color(139, 69, 19));

    @Getter
    private final TextColor textColor;
    @Deprecated
    public final int woolData;
    public final String material1_13;
    @Getter
    private final RGBLike leatherColor;

    public BlockTypeHolder getWoolBlockType() {
        return BlockTypeHolder.of("WOOL").colorize(material1_13);
    }
}
