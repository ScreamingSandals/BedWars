package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

import java.util.function.Consumer;

public interface SavePlayerStatisticEvent<S extends PlayerStatistic> extends BWCancellable {
    S getPlayerStatistic();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<SavePlayerStatisticEvent<PlayerStatistic>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, SavePlayerStatisticEvent.class, (Consumer) consumer);
    }
}
