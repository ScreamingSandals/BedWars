package misat11.bw.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum ItemSpawnerType {
	BRONZE(ChatColor.DARK_RED, Material.BRICK),
	IRON(ChatColor.GRAY, Material.IRON_INGOT),
	GOLD(ChatColor.GOLD, Material.GOLD_INGOT);
	
	public final ChatColor color;
	public final Material material;
	
	private ItemSpawnerType(ChatColor color, Material material) {
		this.color = color;
		this.material = material;
	}
}
