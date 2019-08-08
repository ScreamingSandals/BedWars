package misat11.bw.api.upgrades;

import java.util.HashMap;
import java.util.Map;

import misat11.bw.api.Game;
import misat11.bw.api.ItemSpawner;

public final class UpgradeRegistry {
	
	// TODO IMPLEMENT UPGRADE API

	private static final Map<String, UpgradeStorage> UPGRADES = new HashMap<>();
	
	static {
		registerUpgrade("spawner", ItemSpawner.class);
	}

	/**
	 * Register new type of upgrade. If upgrade type is registered, you can buy
	 * upgrade in shop.
	 * 
	 * @param name         Name of upgrade
	 * @param upgradeClass Class type of upgrade
	 */
	public static UpgradeStorage registerUpgrade(String name, Class<? extends Upgrade> upgradeClass) {
		UpgradeStorage storage = new UpgradeStorage(name, upgradeClass);
		UPGRADES.put(name, storage);
		return storage;
	}

	/**
	 * Unregister upgrade type
	 * 
	 * @param name Name of upgrade
	 */
	public static void unregisterUpgrade(String name) {
		if (UPGRADES.containsKey(name)) {
			UPGRADES.remove(name);
		}
	}

	/**
	 * Check if upgrade is registered
	 * 
	 * @param name Name of upgrade
	 * @return if upgrade is registered
	 */
	public static boolean isUpgradeRegistered(String name) {
		return UPGRADES.containsKey(name);
	}

	/**
	 * Get storage for upgrades
	 * 
	 * @param name Name of upgrade
	 * @return storage of specified upgrade type or null
	 */
	public static UpgradeStorage getUpgrade(String name) {
		return UPGRADES.get(name);
	}
	
	/**
	 * Unregister all active upgrades of any type from game
	 * 
	 * @param game that is ending
	 */
	public static void clearAll(Game game) {
		for (UpgradeStorage storage : UPGRADES.values()) {
			storage.resetUpgradesForGame(game);
		}
	}
}
