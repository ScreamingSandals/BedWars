package misat11.bw.utils;

import org.bukkit.entity.Player;

public class Title {
	public static void send(Player player, String title, String subtitle) {
		try {
			player.sendTitle(title, subtitle, 0, 20, 0);
		} catch (NoSuchMethodError ex) {
			player.sendTitle(title, subtitle);
		}
	}
}
