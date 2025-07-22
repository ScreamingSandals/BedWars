/*
 * Copyright (C) 2025 ScreamingSandals
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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.upgrade.Upgrade;

@RequiredArgsConstructor
@Getter
public class UpgradeImpl implements Upgrade {
    private final double initialLevel;
    private final @Nullable Double maximalLevel;
    @Setter
    private double level;

    public void reset() {
        this.level = this.initialLevel;
    }

    @Override
    public void increaseLevel(double level) {
        setLevel(this.level + level);
    }
}
