package misat11.bw.utils;

import org.bukkit.entity.Player;

import misat11.bw.Main;

public class Title {
	public static void send(Player player, String title, String subtitle) {
		try {
			player.sendTitle(title, subtitle, Main.getConfigurator().config.getInt("title.fadeIn"),
					Main.getConfigurator().config.getInt("title.stay"),
					Main.getConfigurator().config.getInt("title.fadeOut"));
		} catch (NoSuchMethodError ex) {
			player.sendTitle(title, subtitle);
		}
	}
}
