package misat11.bw.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum TeamColor {
	BLACK(Material.BLACK_WOOL),
	DARK_BLUE(Material.BLUE_WOOL),
	DARK_GREEN(Material.GREEN_WOOL),
	RED(Material.RED_WOOL),
	DARK_PURPLE(Material.MAGENTA_WOOL),
	GOLD(Material.ORANGE_WOOL),
	GRAY(Material.LIGHT_GRAY_WOOL),
	DARK_GRAY(Material.GRAY_WOOL),
	BLUE(Material.LIGHT_BLUE_WOOL),
	GREEN(Material.LIME_WOOL),
	AQUA(Material.CYAN_WOOL),
	LIGHT_PURPLE(Material.PINK_WOOL),
	YELLOW(Material.YELLOW_WOOL),
	WHITE(Material.WHITE_WOOL),
	DARK_RED(Material.BROWN_WOOL);
	
	public final ChatColor chatColor;
	public final Material material;
	
	private TeamColor(Material material) {
		this.chatColor = ChatColor.valueOf(this.name());
		this.material = material;
	}
	
	private TeamColor(ChatColor chatColor, Material material) {
		this.chatColor = chatColor;
		this.material = material;
	}
}
