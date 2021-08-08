package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PlayerLastLeaveEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerLastLeaveEventImpl extends AbstractEvent implements PlayerLastLeaveEvent<GameImpl, BedWarsPlayer, CurrentTeam> {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final CurrentTeam team;
}
