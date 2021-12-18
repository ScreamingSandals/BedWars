package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.GameStartedEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.SEvent;

@Data
public class GameStartedEventImpl implements GameStartedEvent<GameImpl>, SEvent {
    private final GameImpl game;
}
