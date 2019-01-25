package misat11.bw.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import misat11.bw.Main;

public enum TeamColor {
	BLACK(0xF, "BLACK_WOOL"),
	DARK_BLUE(0xB, "BLUE_WOOL"),
	DARK_GREEN(0xD, "GREEN_WOOL"),
	RED(0xE, "RED_WOOL"),
	DARK_PURPLE(0x2, "MAGENTA_WOOL"),
	GOLD(0x1, "ORANGE_WOOL"),
	GRAY(0x8, "LIGHT_GRAY_WOOL"),
	DARK_GRAY(0x7, "GRAY_WOOL"),
	BLUE(0x3, "LIGHT_BLUE_WOOL"),
	GREEN(0x5, "LIME_WOOL"),
	AQUA(0x9, "CYAN_WOOL"),
	LIGHT_PURPLE(0x6, "PINK_WOOL"),
	YELLOW(0x4, "YELLOW_WOOL"),
	WHITE(0x0, "WHITE_WOOL"),
	DARK_RED(0xC, "BROWN_WOOL");
	
	public final ChatColor chatColor;
	public final String material1_13;
	public final int dyeColor;
	
	private TeamColor(int woolData, String material1_13) {
		this.chatColor = ChatColor.valueOf(this.name());
		this.dyeColor = woolData;
		this.material1_13 = material1_13;
	}
	
	private TeamColor(ChatColor chatColor, int woolData, String material1_13) {
		this.chatColor = chatColor;
		this.dyeColor = woolData;
		this.material1_13 = material1_13;
	}
	
	public ItemStack getWool() {
		if (Main.isLegacy()) {
			return new ItemStack(Material.valueOf("WOOL"), 1, (short) dyeColor);
		} else {
			return new ItemStack(Material.valueOf(material1_13));
		}
		
	}
}
