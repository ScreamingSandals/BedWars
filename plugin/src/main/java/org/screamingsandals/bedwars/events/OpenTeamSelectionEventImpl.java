package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.OpenTeamSelectionEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SCancellableEvent;

@Data
public class OpenTeamSelectionEventImpl implements OpenTeamSelectionEvent<GameImpl, BedWarsPlayer>, SCancellableEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private boolean cancelled;
}
