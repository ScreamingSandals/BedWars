package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.PlayerLastLeaveEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SEvent;

@Data
public class PlayerLastLeaveEventImpl implements PlayerLastLeaveEvent<GameImpl, BedWarsPlayer, TeamImpl>, SEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final TeamImpl team;
}
