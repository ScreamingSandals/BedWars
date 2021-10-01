package org.screamingsandals.bedwars.game;

import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.ChatColor;
import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.lib.block.BlockTypeHolder;

public enum TeamColorImpl implements TeamColor {
    BLACK(ChatColor.BLACK, 0xF, "BLACK", TextColor.color(0,0,0)),
    BLUE(ChatColor.DARK_BLUE, 0xB, "BLUE", TextColor.color(0, 0, 170)),
    GREEN(ChatColor.DARK_GREEN, 0xD, "GREEN", TextColor.color(0, 170, 0)),
    RED(ChatColor.RED, 0xE, "RED", TextColor.color(255, 85, 85)),
    MAGENTA(ChatColor.DARK_PURPLE, 0x2, "MAGENTA", TextColor.color(170, 0, 170)),
    ORANGE(ChatColor.GOLD, 0x1, "ORANGE", TextColor.color(255, 170, 0)),
    LIGHT_GRAY(ChatColor.GRAY, 0x8, "LIGHT_GRAY", TextColor.color(170, 170, 170)),
    GRAY(ChatColor.DARK_GRAY, 0x7, "GRAY", TextColor.color(85, 85, 85)),
    LIGHT_BLUE(ChatColor.BLUE, 0x3, "LIGHT_BLUE", TextColor.color(85, 85, 255)),
    LIME(ChatColor.GREEN, 0x5, "LIME", TextColor.color(85, 255, 85)),
    CYAN(ChatColor.AQUA, 0x9, "CYAN", TextColor.color(85, 255, 255)),
    PINK(ChatColor.LIGHT_PURPLE, 0x6, "PINK", TextColor.color(255, 85, 255)),
    YELLOW(ChatColor.YELLOW, 0x4, "YELLOW", TextColor.color(255, 255, 85)),
    WHITE(ChatColor.WHITE, 0x0, "WHITE", TextColor.color(255,255,255)),
    BROWN(ChatColor.DARK_RED, 0xC, "BROWN", TextColor.color(139,69,19));

    public final ChatColor chatColor;
    public final String material1_13;
    public final int woolData;
    @Getter
    private final RGBLike leatherColor;

    TeamColorImpl(ChatColor chatColor, int woolData, String material1_13, RGBLike leatherColor) {
        this.chatColor = chatColor;
        this.woolData = woolData;
        this.material1_13 = material1_13;
        this.leatherColor = leatherColor;
    }

    public BlockTypeHolder getWoolBlockType() {
        return BlockTypeHolder.of("WOOL").colorize(material1_13);
    }
}
