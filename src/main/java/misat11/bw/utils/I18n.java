package misat11.bw.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import misat11.bw.Main;

public class I18n {

	public static final String base_lang_code = "en";

	public static final List<String> supported_lang_codes = Arrays.asList("en", "cs");

	private static String locale = "en";
	private static FileConfiguration config_baseLanguage;
	private static FileConfiguration config;

	public static String _(String key) {
		return _(key, true);
	}

	public static String _(String key, boolean prefix) {
		String value = "";
		if (prefix) {
			value += translate("prefix") + " ";
		}
		value += translate(key);
		return value;
	}

	private static String translate(String base) {
		if (config.isSet(base)) {
			return config.getString(base);
		} else if (config_baseLanguage != null) {
			if (config_baseLanguage.isSet(base)) {
				return config_baseLanguage.getString(base);
			}
		}
		return "§c" + base;
	}

	public static void load() {
		if (Main.getConfigurator().config.isSet("locale")) {
			locale = Main.getConfigurator().config.getString("locale");
		}
		if (!supported_lang_codes.contains(locale)) {
			locale = base_lang_code;
		}
		if (!base_lang_code.equals(locale)) {
			InputStream inb = Main.getInstance().getResource("messages_" + base_lang_code + ".yml");
			config_baseLanguage = new YamlConfiguration();
			try {
				config_baseLanguage.load(new InputStreamReader(inb));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		InputStream in = Main.getInstance().getResource("messages_" + locale + ".yml");
		config = new YamlConfiguration();
		try {
			config.load(new InputStreamReader(in));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		Main.getInstance().getLogger()
				.info("Successfully loaded messages for BedWars! Language: " + config.getString("lang_name"));
	}

}
