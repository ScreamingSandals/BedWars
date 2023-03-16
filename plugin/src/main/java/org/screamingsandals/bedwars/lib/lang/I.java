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

public class I {
	public static final String BASE_LANG_CODE = "en";

	protected static String locale = "en";
	protected static ITranslateContainer customContainer;
	protected static ITranslateContainer mainContainer;
	protected static ITranslateContainer fallbackContainer;

	public static String i18n(String key) {
		return i18n(key, null, true);
	}

	public static String i18nonly(String key) {
		return i18n(key, null, false);
	}

	public static String i18n(String key, boolean prefix) {
		return i18n(key, null, prefix);
	}

	public static String i18n(String key, String defaultK) {
		return i18n(key, defaultK, true);
	}

	public static String i18nonly(String key, String defaultK) {
		return i18n(key, defaultK, false);
	}

	public static String i18n(String key, String def, boolean prefix) {
		if (prefix) {
			return customContainer.translateWithPrefix(key, def);
		} else {
			return customContainer.translate(key, def);
		}
	}

	public static String i18nc(String key, String customPrefix) {
		return i18nc(key, null, customPrefix);
	}

	public static String i18nc(String key, String def, String customPrefix) {
		return customContainer.translateWithPrefix(key, def, customPrefix);
	}
	
	public static Message mpr() {
		return m(null, null, true);
	}
	
	public static Message mpr(String key) {
		return m(key, null, true);
	}
	
	public static Message mpr(String key, String def) {
		return m(key, def, true);
	}
	
	public static Message m() {
		return m(null, null, false);
	}
	
	public static Message m(String key) {
		return m(key, null, false);
	}

	public static Message m(String key, boolean prefix) {
		return m(key, null, prefix);
	}
	
	public static Message m(String key, String def) {
		return m(key, def, false);
	}
	
	public static Message m(String key, String def, boolean prefix) {
		return new Message(key, customContainer, def, prefix);
	}

	@Deprecated
	private static String translate(String base, String defaultK) {
		return customContainer.translate(base, defaultK);
	}
}
