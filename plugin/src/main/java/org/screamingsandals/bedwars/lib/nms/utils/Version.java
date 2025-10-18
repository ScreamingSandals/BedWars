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

package org.screamingsandals.bedwars.lib.nms.utils;

import org.bukkit.Bukkit;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version {
	private static final int MAJOR_VERSION;
	private static final int MINOR_VERSION;
	private static final int PATCH_VERSION;

	static {
		try {
			Pattern versionPattern = Pattern.compile("\\(MC: (\\d+)\\.(\\d+)\\.?(\\d+)?");
			Matcher matcher = versionPattern.matcher(Bukkit.getVersion());
			int majorVersion = 1;
			int minorVersion = 0;
			int patchVersion = 0;
			if (matcher.find()) {
				MatchResult matchResult = matcher.toMatchResult();
				try {
					majorVersion = Integer.parseInt(matchResult.group(1), 10);
				} catch (Exception ignored) {
				}
				try {
					minorVersion = Integer.parseInt(matchResult.group(2), 10);
				} catch (Exception ignored) {
				}
				if (matchResult.groupCount() >= 3) {
					try {
						patchVersion = Integer.parseInt(matchResult.group(3), 10);
					} catch (Exception ignored) {
					}
				}
			}
			MAJOR_VERSION = majorVersion;
			MINOR_VERSION = minorVersion;
			PATCH_VERSION = patchVersion;
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static boolean isVersion(int major, int minor) {
		return isVersion(major, minor, 0);
	}

	public static boolean isVersion(int major, int minor, int patch) {
		return MAJOR_VERSION > major || (MAJOR_VERSION >= major && (MINOR_VERSION > minor || (MINOR_VERSION >= minor && PATCH_VERSION >= patch)));
	}
}
