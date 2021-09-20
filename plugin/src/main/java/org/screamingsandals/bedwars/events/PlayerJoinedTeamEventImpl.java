package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.PlayerJoinedTeamEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerJoinedTeamEventImpl extends AbstractEvent implements PlayerJoinedTeamEvent<GameImpl, BedWarsPlayer, TeamImpl> {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final TeamImpl team;
    @Nullable
    private final TeamImpl previousTeam;
}
