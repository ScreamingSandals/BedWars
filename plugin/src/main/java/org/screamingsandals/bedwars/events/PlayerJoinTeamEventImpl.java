package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.PlayerJoinTeamEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerJoinTeamEventImpl extends CancellableAbstractEvent implements PlayerJoinTeamEvent<Game, BedWarsPlayer, CurrentTeam> {
    private final Game game;
    private final BedWarsPlayer player;
    private final CurrentTeam team;
    @Nullable
    private final CurrentTeam previousTeam;
}
