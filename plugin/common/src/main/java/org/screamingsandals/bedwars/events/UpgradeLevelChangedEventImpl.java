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

package org.screamingsandals.bedwars.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.events.UpgradeLevelChangedEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.upgrade.UpgradableImpl;
import org.screamingsandals.bedwars.game.upgrade.UpgradeImpl;
import org.screamingsandals.lib.event.Event;

@Data
@AllArgsConstructor
public class UpgradeLevelChangedEventImpl implements UpgradeLevelChangedEvent, Event {
    private final @NotNull GameImpl game;
    private final @NotNull UpgradableImpl upgradable;
    private final @NotNull String name;
    private final @NotNull UpgradeImpl upgrade;
    private final double oldLevel;

    @Override
    public double getNewLevel() {
        return this.upgrade.getLevel();
    }
}
