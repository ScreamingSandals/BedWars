/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

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
