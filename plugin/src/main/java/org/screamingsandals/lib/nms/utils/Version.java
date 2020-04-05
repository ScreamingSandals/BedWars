package org.screamingsandals.lib.nms.utils;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

public class Version {
	
	public static final int MAJOR_VERSION;
	public static final int MINOR_VERSION;
	public static final int PATCH_VERSION;
	
	static {
		Pattern versionPattern = Pattern.compile("\\(MC: (\\d+)\\.(\\d+)\\.?(\\d+?)?\\)");
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
	}

	public static boolean isVersion(int major, int minor) {
		return isVersion(major, minor, 0);
	}

	public static boolean isVersion(int major, int minor, int patch) {
		return MAJOR_VERSION > major || (MAJOR_VERSION >= major && (MINOR_VERSION > minor || (MINOR_VERSION >= minor && PATCH_VERSION >= patch)));
	}
}
