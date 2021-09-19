package org.screamingsandals.bedwars.game;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.lib.block.BlockTypeHolder;

public enum TeamColorImpl implements TeamColor {
    BLACK(ChatColor.BLACK, 0xF, "BLACK", Color.BLACK),
    BLUE(ChatColor.DARK_BLUE, 0xB, "BLUE", Color.fromRGB(0, 0, 170)),
    GREEN(ChatColor.DARK_GREEN, 0xD, "GREEN", Color.fromRGB(0, 170, 0)),
    RED(ChatColor.RED, 0xE, "RED", Color.fromRGB(255, 85, 85)),
    MAGENTA(ChatColor.DARK_PURPLE, 0x2, "MAGENTA", Color.fromRGB(170, 0, 170)),
    ORANGE(ChatColor.GOLD, 0x1, "ORANGE", Color.fromRGB(255, 170, 0)),
    LIGHT_GRAY(ChatColor.GRAY, 0x8, "LIGHT_GRAY", Color.fromRGB(170, 170, 170)),
    GRAY(ChatColor.DARK_GRAY, 0x7, "GRAY", Color.fromRGB(85, 85, 85)),
    LIGHT_BLUE(ChatColor.BLUE, 0x3, "LIGHT_BLUE", Color.fromRGB(85, 85, 255)),
    LIME(ChatColor.GREEN, 0x5, "LIME", Color.fromRGB(85, 255, 85)),
    CYAN(ChatColor.AQUA, 0x9, "CYAN", Color.fromRGB(85, 255, 255)),
    PINK(ChatColor.LIGHT_PURPLE, 0x6, "PINK", Color.fromRGB(255, 85, 255)),
    YELLOW(ChatColor.YELLOW, 0x4, "YELLOW", Color.fromRGB(255, 255, 85)),
    WHITE(ChatColor.WHITE, 0x0, "WHITE", Color.WHITE),
    BROWN(ChatColor.DARK_RED, 0xC, "BROWN", Color.fromRGB(139,69,19));

    public final ChatColor chatColor;
    public final String material1_13;
    public final int woolData;
    public final Color leatherColor;

    TeamColorImpl(ChatColor chatColor, int woolData, String material1_13, Color leatherColor) {
        this.chatColor = chatColor;
        this.woolData = woolData;
        this.material1_13 = material1_13;
        this.leatherColor = leatherColor;
    }

    public BlockTypeHolder getWoolBlockType() {
        return BlockTypeHolder.of("WOOL").colorize(material1_13);
    }

    public RGBLike getLeatherColor() {
        return TextColor.color(leatherColor.getRed(), leatherColor.getGreen(), leatherColor.getBlue());
    }
}
