package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.PlayerJoinedTeamEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerJoinedTeamEventImpl extends AbstractEvent implements PlayerJoinedTeamEvent<Game, BedWarsPlayer, CurrentTeam> {
    private final Game game;
    private final BedWarsPlayer player;
    private final CurrentTeam team;
    @Nullable
    private final CurrentTeam previousTeam;
}
