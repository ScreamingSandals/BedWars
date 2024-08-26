/*
 * Copyright (C) 2024 ScreamingSandals
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

package org.screamingsandals.bedwars.game.upgrade;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.upgrade.Upgradable;
import org.screamingsandals.bedwars.game.upgrade.builtin.BuiltInUpgradeDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpgradableImpl implements Upgradable {
    protected final @NotNull Map<@NotNull String, UpgradeImpl> teamUpgrades = new HashMap<>();

    @Override
    public @NotNull UpgradeImpl registerUpgrade(@NotNull String name, double initialLevel, @Nullable Double maxLevel) throws IllegalStateException {
        if (teamUpgrades.containsKey(name)) {
            throw new IllegalStateException("Upgrade " + name + " is already registered!");
        }

        var teamUpgrade = new UpgradeImpl(initialLevel, maxLevel);
        teamUpgrade.reset();
        teamUpgrades.put(name, teamUpgrade);
        return teamUpgrade;
    }

    @Override
    public @Nullable UpgradeImpl getUpgrade(@NotNull String name) {
        return teamUpgrades.get(name);
    }

    protected void syncBuiltInUpgrades(@NotNull Map<@NotNull String, BuiltInUpgradeDefinition> upgrades) {
        upgrades.forEach((s, upgrade) -> {
            if (teamUpgrades.containsKey(s)) {
                if (teamUpgrades.get(s).getInitialLevel() != upgrade.getInitialLevel() || !Objects.equals(teamUpgrades.get(s).getMaximalLevel(), upgrade.getMaximalLevel())) {
                    teamUpgrades.put(s, new UpgradeImpl(upgrade.getInitialLevel(), upgrade.getMaximalLevel()));
                }
            } else {
                teamUpgrades.put(s, new UpgradeImpl(upgrade.getInitialLevel(), upgrade.getMaximalLevel()));
            }
        });
    }

    protected void resetUpgrades() {
        teamUpgrades.forEach((s, upgrade) -> upgrade.reset());
    }
}
