package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.PlayerJoinTeamEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerJoinTeamEventImpl extends CancellableAbstractEvent implements PlayerJoinTeamEvent<GameImpl, BedWarsPlayer, CurrentTeam> {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final CurrentTeam team;
    @Nullable
    private final CurrentTeam previousTeam;
}
