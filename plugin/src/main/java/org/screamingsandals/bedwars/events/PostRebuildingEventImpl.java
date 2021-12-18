package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.PostRebuildingEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.SEvent;

@Data
public class PostRebuildingEventImpl implements PostRebuildingEvent<GameImpl>, SEvent {
    private final GameImpl game;
}
