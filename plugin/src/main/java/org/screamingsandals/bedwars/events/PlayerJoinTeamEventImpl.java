package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.PlayerJoinTeamEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerJoinTeamEventImpl extends CancellableAbstractEvent implements PlayerJoinTeamEvent<GameImpl, BedWarsPlayer, TeamImpl> {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final TeamImpl team;
    @Nullable
    private final TeamImpl previousTeam;
}
