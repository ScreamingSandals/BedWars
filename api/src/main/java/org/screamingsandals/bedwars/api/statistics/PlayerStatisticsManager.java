package org.screamingsandals.bedwars.api.statistics;

import java.util.List;
import java.util.UUID;

public interface PlayerStatisticsManager {

    List<LeaderboardEntry> getLeaderboard(int count);

    PlayerStatistic loadStatistic(UUID uuid);
}
