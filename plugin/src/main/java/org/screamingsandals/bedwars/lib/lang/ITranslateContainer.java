package org.screamingsandals.bedwars.lib.lang;

import org.bukkit.ChatColor;

public interface ITranslateContainer {
	String getLocaleCode();

	default String getLanguage() {
		return translate("lang_name");
	}

	default String getFallbackLanguage() {
		return getFallbackContainer() != null ? getFallbackContainer().getLanguage() : null;
	}

	default String translate(String key) {
		return translate(key, null);
	}

	String translate(String key, String def);

	default String translate(String key, String def, boolean prefix) {
		return translate(key, def, prefix, null);
	}

	default String translate(String key, String def, boolean prefix, String customPrefix) {
		if (prefix) {
			return translateWithPrefix(key, def, customPrefix);
		} else {
			return translate(key, def);
		}
	}

	default String translate(String key, boolean prefix) {
		return translate(key, null, prefix);
	}

	default String getPrefix() {
		return translate("prefix", "");
	}

	default String translateWithPrefix(String key) {
		return translateWithPrefix(key, null);
	}

	default String translateWithPrefix(String key, String def) {
		return translateWithPrefix(key, def, null);
	}

	default String translateWithPrefix(String key, String def, String customPrefix) {
		String value = "";
		String prefix = customPrefix != null ? ChatColor.translateAlternateColorCodes('&', customPrefix) : getPrefix();
		if (prefix != null && prefix.length() > 0) {
			value += prefix + ChatColor.COLOR_CHAR + "r ";
		}
		value += translate(key, def);
		return value;
	}

	ITranslateContainer getFallbackContainer();
}
