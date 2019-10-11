package misat11.bw.game;

import misat11.bw.Main;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum TeamColor {
    BLACK(0xF, "BLACK", Color.BLACK),
    DARK_BLUE(0xB, "BLUE", Color.fromRGB(0, 0, 170)),
    DARK_GREEN(0xD, "GREEN", Color.fromRGB(0, 170, 0)),
    RED(0xE, "RED", Color.fromRGB(255, 85, 85)),
    DARK_PURPLE(0x2, "MAGENTA", Color.fromRGB(170, 0, 170)),
    GOLD(0x1, "ORANGE", Color.fromRGB(255, 170, 0)),
    GRAY(0x8, "LIGHT_GRAY", Color.fromRGB(170, 170, 170)),
    DARK_GRAY(0x7, "GRAY", Color.fromRGB(85, 85, 85)),
    BLUE(0x3, "LIGHT_BLUE", Color.fromRGB(85, 85, 255)),
    GREEN(0x5, "LIME", Color.fromRGB(85, 255, 85)),
    AQUA(0x9, "CYAN", Color.fromRGB(85, 255, 255)),
    LIGHT_PURPLE(0x6, "PINK", Color.fromRGB(255, 85, 255)),
    YELLOW(0x4, "YELLOW", Color.fromRGB(255, 255, 85)),
    WHITE(0x0, "WHITE", Color.WHITE),
    DARK_RED(0xC, "BROWN", Color.fromRGB(170, 0, 0));

    public final ChatColor chatColor;
    public final String material1_13;
    public final int woolData;
    public final Color leatherColor;

    TeamColor(int woolData, String material1_13, Color leatherColor) {
        this.chatColor = ChatColor.valueOf(this.name());
        this.woolData = woolData;
        this.material1_13 = material1_13;
        this.leatherColor = leatherColor;
    }

    TeamColor(ChatColor chatColor, int woolData, String material1_13, Color leatherColor) {
        this.chatColor = chatColor;
        this.woolData = woolData;
        this.material1_13 = material1_13;
        this.leatherColor = leatherColor;
    }

    public ItemStack getWool() {
        if (Main.isLegacy()) {
            return new ItemStack(Material.valueOf("WOOL"), 1, (short) woolData);
        } else {
            return new ItemStack(Material.valueOf(material1_13 + "_WOOL"));
        }

    }

    public ItemStack getWool(ItemStack stack) {
        if (Main.isLegacy()) {
            stack.setType(Material.valueOf("WOOL"));
            stack.setDurability((short) woolData);
        } else {
            stack.setType(Material.valueOf(material1_13 + "_WOOL"));
        }
        return stack;

    }

    public misat11.bw.api.TeamColor toApiColor() {
        return misat11.bw.api.TeamColor.valueOf(this.name());
    }

    public static TeamColor fromApiColor(misat11.bw.api.TeamColor color) {
        return TeamColor.valueOf(color.name());
    }
}
