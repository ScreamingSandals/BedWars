package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.PreRebuildingEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.SEvent;

@Data
public class PreRebuildingEventImpl implements PreRebuildingEvent<GameImpl>, SEvent {
    private final GameImpl game;
}
