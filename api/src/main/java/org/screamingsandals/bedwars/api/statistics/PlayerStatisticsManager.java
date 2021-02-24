package org.screamingsandals.bedwars.api.statistics;

import org.screamingsandals.lib.utils.Wrapper;

import java.util.List;
import java.util.UUID;

public interface PlayerStatisticsManager<OP extends Wrapper> {

    List<LeaderboardEntry<OP>> getLeaderboard(int count);

    PlayerStatistic loadStatistic(UUID uuid);
}
