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

package org.screamingsandals.bedwars.statistics;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

public class LeaderboardEntry implements org.screamingsandals.bedwars.api.statistics.LeaderboardEntry {
    private final OfflinePlayer offlinePlayer;
    private final int currentScore;
    private final @Nullable String latestKnownName;

    public LeaderboardEntry(OfflinePlayer offlinePlayer, int currentScore, @Nullable String latestKnownName) {
        this.offlinePlayer = offlinePlayer;
        this.currentScore = currentScore;
        this.latestKnownName = latestKnownName;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return this.offlinePlayer;
    }

    @Override
    public int getTotalScore() {
        return this.currentScore;
    }

    @Override
    public PlayerStatistic fetchStatistics() {
        return Main.getPlayerStatisticsManager().getStatistic(offlinePlayer);
    }

    @Override
    public @Nullable String getLatestKnownName() {
        return this.latestKnownName;
    }
}
