package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.SavePlayerStatisticEvent;
import org.screamingsandals.bedwars.statistics.PlayerStatistic;
import org.screamingsandals.lib.event.CancellableAbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class SavePlayerStatisticEventImpl extends CancellableAbstractEvent implements SavePlayerStatisticEvent<PlayerStatistic> {
    private final PlayerStatistic playerStatistic;
}
