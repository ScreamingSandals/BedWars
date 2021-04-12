package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.GameChangedStatusEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameChangedStatusEventImpl extends AbstractEvent implements GameChangedStatusEvent<Game> {
    private final Game game;
}
