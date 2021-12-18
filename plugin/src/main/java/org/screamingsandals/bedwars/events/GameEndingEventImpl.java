package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.GameEndingEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.lib.event.SEvent;

@Data
public class GameEndingEventImpl implements GameEndingEvent<GameImpl, TeamImpl>, SEvent {
    private final GameImpl game;
    @Nullable
    private final TeamImpl winningTeam;
}
