package misat11.bw.utils;

import org.bukkit.entity.Player;

import misat11.bw.Main;

public class Title {
	public static void send(Player player, String title, String subtitle) {
		int fadeIn = Main.getConfigurator().config.getInt("title.fadeIn");
		int stay = Main.getConfigurator().config.getInt("title.stay");
		int fadeOut = Main.getConfigurator().config.getInt("title.fadeOut");

		try {
			player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		} catch (Throwable ex) {
			if (Main.isNMS()) {
				try {
					Class<?> clazz = Class.forName("misat11.bw.nms." + Main.getNMSVersion().toLowerCase() + ".Title");
					clazz.getDeclaredMethod("showTitle", Player.class, String.class, double.class, double.class,
							double.class).invoke(null, player, title, fadeIn, stay, fadeOut);
					clazz.getDeclaredMethod("showSubTitle", Player.class, String.class, double.class, double.class,
							double.class).invoke(null, player, subtitle, fadeIn, stay, fadeOut);
				} catch (Throwable ex2) {

				}
			} else {
				try {
					player.sendTitle(title, subtitle);
				} catch (Throwable ex2) {

				}
			}
		}
	}
}
