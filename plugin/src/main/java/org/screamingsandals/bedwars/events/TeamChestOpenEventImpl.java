package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.TeamChestOpenEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SCancellableEvent;

@Data
public class TeamChestOpenEventImpl implements TeamChestOpenEvent<GameImpl, BedWarsPlayer, TeamImpl>, SCancellableEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final TeamImpl team;
    private boolean cancelled;
}
