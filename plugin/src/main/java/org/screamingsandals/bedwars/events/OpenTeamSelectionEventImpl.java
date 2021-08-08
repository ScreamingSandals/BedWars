package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.OpenTeamSelectionEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class OpenTeamSelectionEventImpl extends CancellableAbstractEvent implements OpenTeamSelectionEvent<GameImpl, BedWarsPlayer> {
    private final GameImpl game;
    private final BedWarsPlayer player;
}
