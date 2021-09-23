package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.SavePlayerStatisticEvent;
import org.screamingsandals.bedwars.statistics.PlayerStatisticImpl;
import org.screamingsandals.lib.event.CancellableAbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class SavePlayerStatisticEventImpl extends CancellableAbstractEvent implements SavePlayerStatisticEvent<PlayerStatisticImpl> {
    private final PlayerStatisticImpl playerStatistic;
}
