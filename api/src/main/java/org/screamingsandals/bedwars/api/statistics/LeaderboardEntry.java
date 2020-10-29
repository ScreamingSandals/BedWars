package org.screamingsandals.bedwars.api.statistics;

import org.bukkit.OfflinePlayer;

public interface LeaderboardEntry {
    OfflinePlayer getPlayer();

    int getTotalScore();

    PlayerStatistic fetchStatistics();
}
