package misat11.bw.game;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import misat11.bw.Main;

public enum TeamColor {
	BLACK(0xF, "BLACK_WOOL", Color.BLACK),
	DARK_BLUE(0xB, "BLUE_WOOL", Color.fromRGB(0, 0, 170)),
	DARK_GREEN(0xD, "GREEN_WOOL", Color.fromRGB(0, 170, 0)),
	RED(0xE, "RED_WOOL", Color.fromRGB(255, 85, 85)),
	DARK_PURPLE(0x2, "MAGENTA_WOOL", Color.fromRGB(170, 0, 170)),
	GOLD(0x1, "ORANGE_WOOL", Color.fromRGB(255, 170, 0)),
	GRAY(0x8, "LIGHT_GRAY_WOOL", Color.fromRGB(170, 170, 170)),
	DARK_GRAY(0x7, "GRAY_WOOL", Color.fromRGB(85, 85, 85)),
	BLUE(0x3, "LIGHT_BLUE_WOOL", Color.fromRGB(85, 85, 255)),
	GREEN(0x5, "LIME_WOOL", Color.fromRGB(85, 255, 85)),
	AQUA(0x9, "CYAN_WOOL", Color.fromRGB(85, 255, 255)),
	LIGHT_PURPLE(0x6, "PINK_WOOL", Color.fromRGB(255, 85, 255)),
	YELLOW(0x4, "YELLOW_WOOL", Color.fromRGB(255, 255, 85)),
	WHITE(0x0, "WHITE_WOOL", Color.WHITE),
	DARK_RED(0xC, "BROWN_WOOL", Color.fromRGB(170, 0, 0));
	
	public final ChatColor chatColor;
	public final String material1_13;
	public final int dyeColor;
	public final Color leatherColor;
	
	private TeamColor(int woolData, String material1_13, Color leatherColor) {
		this.chatColor = ChatColor.valueOf(this.name());
		this.dyeColor = woolData;
		this.material1_13 = material1_13;
		this.leatherColor = leatherColor;
	}
	
	private TeamColor(ChatColor chatColor, int woolData, String material1_13, Color leatherColor) {
		this.chatColor = chatColor;
		this.dyeColor = woolData;
		this.material1_13 = material1_13;
		this.leatherColor = leatherColor;
	}
	
	public ItemStack getWool() {
		if (Main.isLegacy()) {
			return new ItemStack(Material.valueOf("WOOL"), 1, (short) dyeColor);
		} else {
			return new ItemStack(Material.valueOf(material1_13));
		}
		
	}
}
