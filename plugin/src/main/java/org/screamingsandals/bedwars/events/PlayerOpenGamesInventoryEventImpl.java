package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.PlayerOpenGamesInventoryEvent;
import org.screamingsandals.lib.event.SCancellableEvent;
import org.screamingsandals.lib.player.PlayerWrapper;

@Data
public class PlayerOpenGamesInventoryEventImpl implements PlayerOpenGamesInventoryEvent<PlayerWrapper>, SCancellableEvent {
    private final PlayerWrapper player;
    private final String type;
    private boolean cancelled;
}
