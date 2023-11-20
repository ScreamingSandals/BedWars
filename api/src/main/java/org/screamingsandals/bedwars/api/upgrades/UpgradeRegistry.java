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

package org.screamingsandals.bedwars.api.upgrades;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.ItemSpawner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ScreamingSandals
 */
public final class UpgradeRegistry {
    private static final Map<String, UpgradeStorage> registeredUpgrades = new HashMap<>();

    static {
        registerUpgrade("spawner", ItemSpawner.class);
    }

    /**
     * Register new type of upgrade. If upgrade type is registered, you can buy
     * upgrade in shop.
     *
     * @param name         Name of upgrade
     * @param upgradeClass Class type of upgrade
     * @return new instance of upgrade storage
     */
    public static UpgradeStorage registerUpgrade(String name, Class<? extends Upgrade> upgradeClass) {
        UpgradeStorage storage = new UpgradeStorage(name, upgradeClass);
        registeredUpgrades.put(name, storage);
        return storage;
    }

    /**
     * Unregister upgrade type
     *
     * @param name Name of upgrade
     */
    public static void unregisterUpgrade(String name) {
		registeredUpgrades.remove(name);
    }

    /**
     * Check if upgrade is registered
     *
     * @param name Name of upgrade
     * @return if upgrade is registered
     */
    public static boolean isUpgradeRegistered(String name) {
        return registeredUpgrades.containsKey(name);
    }

    /**
     * Get storage for upgrades
     *
     * @param name Name of upgrade
     * @return storage of specified upgrade type or null
     */
    public static UpgradeStorage getUpgrade(String name) {
        return registeredUpgrades.get(name);
    }

    /**
     * Unregister all active upgrades of any type from game
     *
     * @param game that is ending
     */
    public static void clearAll(Game game) {
        for (UpgradeStorage storage : registeredUpgrades.values()) {
            storage.resetUpgradesForGame(game);
        }
    }
}
