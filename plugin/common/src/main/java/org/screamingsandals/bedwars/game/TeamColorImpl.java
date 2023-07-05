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
import org.screamingsandals.lib.DyeColor;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.spectator.Color;

@RequiredArgsConstructor
public enum TeamColorImpl implements TeamColor {
    BLACK(Color.BLACK, DyeColor.of("black"), "BLACK", Color.rgb(0, 0, 0)),
    BLUE(Color.DARK_BLUE, DyeColor.of("blue"), "BLUE", Color.rgb(0, 0, 170)),
    GREEN(Color.DARK_GREEN, DyeColor.of("green"), "GREEN", Color.rgb(0, 170, 0)),
    RED(Color.RED, DyeColor.of("red"), "RED", Color.rgb(255, 85, 85)),
    MAGENTA(Color.DARK_PURPLE, DyeColor.of("magenta"), "MAGENTA", Color.rgb(170, 0, 170)),
    ORANGE(Color.GOLD, DyeColor.of("orange"), "ORANGE", Color.rgb(255, 170, 0)),
    LIGHT_GRAY(Color.GRAY, DyeColor.of("light_gray"), "LIGHT_GRAY", Color.rgb(170, 170, 170)),
    GRAY(Color.DARK_GRAY, DyeColor.of("gray"), "GRAY", Color.rgb(85, 85, 85)),
    LIGHT_BLUE(Color.BLUE, DyeColor.of("light_blue"), "LIGHT_BLUE", Color.rgb(85, 85, 255)),
    LIME(Color.GREEN, DyeColor.of("lime"), "LIME", Color.rgb(85, 255, 85)),
    CYAN(Color.AQUA, DyeColor.of("cyan"), "CYAN", Color.rgb(85, 255, 255)),
    PINK(Color.LIGHT_PURPLE, DyeColor.of("pink"), "PINK", Color.rgb(255, 85, 255)),
    YELLOW(Color.YELLOW, DyeColor.of("yellow"), "YELLOW", Color.rgb(255, 255, 85)),
    WHITE(Color.WHITE, DyeColor.of("white"), "WHITE", Color.rgb(255, 255, 255)),
    BROWN(Color.DARK_RED, DyeColor.of("brown"), "BROWN", Color.rgb(139, 69, 19));

    @Getter
    private final Color textColor;
    @Getter
    public final DyeColor dyeColor;
    public final String material1_13;
    @Getter
    private final Color leatherColor;

    public Block getWoolBlockType() {
        return Block.of("WOOL").colorize(material1_13);
    }
}
