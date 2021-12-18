package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.GameStartEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.SCancellableEvent;

@Data
public class GameStartEventImpl implements GameStartEvent<GameImpl>, SCancellableEvent {
    private final GameImpl game;
    private boolean cancelled;
}
