package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PostRebuildingEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostRebuildingEventImpl extends AbstractEvent implements PostRebuildingEvent<GameImpl> {
    private final GameImpl game;
}
