package org.screamingsandals.bedwars.lib.nms.utils;

import org.screamingsandals.bedwars.lib.nms.accessors.AccessorUtils;

public class Version {

	public static boolean isVersion(int major, int minor) {
		return isVersion(major, minor, 0);
	}

	public static boolean isVersion(int major, int minor, int patch) {
		return AccessorUtils.isVersion(major, minor, patch);
	}
}
