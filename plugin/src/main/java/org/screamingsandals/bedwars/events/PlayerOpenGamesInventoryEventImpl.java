package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PlayerOpenGamesInventoryEvent;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.lib.player.PlayerWrapper;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerOpenGamesInventoryEventImpl extends CancellableAbstractEvent implements PlayerOpenGamesInventoryEvent<PlayerWrapper> {
    private final PlayerWrapper player;
    private final String type;
}
