package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.GameEndingEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameEndingEventImpl extends AbstractEvent implements GameEndingEvent<GameImpl, TeamImpl> {
    private final GameImpl game;
    @Nullable
    private final TeamImpl winningTeam;
}
