package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.GameEndEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameEndEventImpl extends AbstractEvent implements GameEndEvent<GameImpl> {
    private final GameImpl game;
}
