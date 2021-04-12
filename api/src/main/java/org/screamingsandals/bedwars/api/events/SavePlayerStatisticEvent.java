package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

public interface SavePlayerStatisticEvent<S extends PlayerStatistic> extends BWCancellable {
    S getPlayerStatistic();
}
