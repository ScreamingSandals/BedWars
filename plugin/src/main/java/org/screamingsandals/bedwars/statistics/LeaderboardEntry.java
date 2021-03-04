package org.screamingsandals.bedwars.statistics;

import lombok.Data;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;
import org.screamingsandals.lib.player.OfflinePlayerWrapper;

@Data
public class LeaderboardEntry implements org.screamingsandals.bedwars.api.statistics.LeaderboardEntry<OfflinePlayerWrapper> {
    private final OfflinePlayerWrapper player;
    private final int totalScore;

    @Override
    public PlayerStatistic fetchStatistics() {
        return PlayerStatisticManager.getInstance().getStatistic(player);
    }
}
