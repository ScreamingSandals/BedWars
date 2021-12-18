package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.GameEndEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.SEvent;

@Data
public class GameEndEventImpl implements GameEndEvent<GameImpl>, SEvent {
    private final GameImpl game;
}
