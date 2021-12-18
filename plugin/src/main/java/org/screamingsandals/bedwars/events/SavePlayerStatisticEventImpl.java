package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.SavePlayerStatisticEvent;
import org.screamingsandals.bedwars.statistics.PlayerStatisticImpl;
import org.screamingsandals.lib.event.SCancellableEvent;

@Data
public class SavePlayerStatisticEventImpl implements SavePlayerStatisticEvent, SCancellableEvent {
    private final PlayerStatisticImpl playerStatistic;
    private boolean cancelled;
}
