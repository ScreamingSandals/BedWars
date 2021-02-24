package org.screamingsandals.bedwars.api.statistics;

import org.screamingsandals.lib.utils.Wrapper;

public interface LeaderboardEntry<OP extends Wrapper> {
    OP getPlayer();

    int getTotalScore();

    PlayerStatistic fetchStatistics();
}
