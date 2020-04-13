package misat11.lib.lang;

import java.io.File;

import org.bukkit.plugin.Plugin;

import java.io.File;

public class I18nBukkit extends I {
	public static void load(Plugin plugin) {
		load(plugin, locale);
	}

	public static void load(Plugin plugin, String loc) {
		if (loc != null && !"".equals(loc.trim())) {
			locale = loc;
		}

		if (!BASE_LANG_CODE.equals(locale)) {
			fallbackContainer = new BukkitTranslateContainer(BASE_LANG_CODE, plugin);
		}

		mainContainer = new BukkitTranslateContainer(locale, plugin, fallbackContainer);

		customContainer = new BukkitTranslateContainer(new File(plugin.getDataFolder().toString(), "languages"), locale, mainContainer);

		plugin.getLogger().info(
			"Successfully loaded messages for " + plugin.getName() + "! Language: " + customContainer.getLanguage());
	}

	public static void load(Plugin plugin, String loc, File folder) {
		if (loc != null && !"".equals(loc.trim())) {
			locale = loc;
		}

		if (!BASE_LANG_CODE.equals(locale)) {
			fallbackContainer = new BukkitTranslateContainer(BASE_LANG_CODE, plugin);
		}

		mainContainer = new BukkitTranslateContainer(locale, plugin, fallbackContainer);

		customContainer = new BukkitTranslateContainer(folder, locale, mainContainer);

		plugin.getLogger().info(
			"Successfully loaded messages for " + plugin.getName() + "! Language: " + customContainer.getLanguage());
	}
}
