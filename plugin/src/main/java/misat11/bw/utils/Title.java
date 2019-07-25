package misat11.bw.utils;

import org.bukkit.entity.Player;

import misat11.bw.Main;
import misat11.lib.nms.NMSUtils;

public class Title {
	public static void send(Player player, String title, String subtitle) {
		int fadeIn = Main.getConfigurator().config.getInt("title.fadeIn");
		int stay = Main.getConfigurator().config.getInt("title.stay");
		int fadeOut = Main.getConfigurator().config.getInt("title.fadeOut");
		
		NMSUtils.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
	}
}
