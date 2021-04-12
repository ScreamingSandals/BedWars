package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.TeamChestOpenEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamChestOpenEventImpl extends CancellableAbstractEvent implements TeamChestOpenEvent<Game, BedWarsPlayer, CurrentTeam> {
    private final Game game;
    private final BedWarsPlayer player;
    private final CurrentTeam team;
}
