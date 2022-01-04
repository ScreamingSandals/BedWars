/*
 * Copyright (C) 2022 ScreamingSandals
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

import java.io.File;

import org.bukkit.plugin.Plugin;

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
