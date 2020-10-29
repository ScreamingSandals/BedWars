package org.screamingsandals.bedwars.statistics;

import org.bukkit.OfflinePlayer;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

public class LeaderboardEntry implements org.screamingsandals.bedwars.api.statistics.LeaderboardEntry {
    private final OfflinePlayer offlinePlayer;
    private final int currentScore;

    public LeaderboardEntry(OfflinePlayer offlinePlayer, int currentScore) {
        this.offlinePlayer = offlinePlayer;
        this.currentScore = currentScore;
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
}
