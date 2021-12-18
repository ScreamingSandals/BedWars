package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface SavePlayerStatisticEvent extends BWCancellable {
    PlayerStatistic getPlayerStatistic();

    static void handle(Object plugin, Consumer<SavePlayerStatisticEvent> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, SavePlayerStatisticEvent.class, consumer);
    }
}
