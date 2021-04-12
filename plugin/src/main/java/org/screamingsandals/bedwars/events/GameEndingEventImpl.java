package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.GameEndingEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameEndingEventImpl extends AbstractEvent implements GameEndingEvent<Game, CurrentTeam> {
    private final Game game;
    @Nullable
    private final CurrentTeam winningTeam;
}
