package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.TargetBlockDestroyedEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class TargetBlockDestroyedEventImpl extends AbstractEvent implements TargetBlockDestroyedEvent<Game, BedWarsPlayer, CurrentTeam> {
    private final Game game;
    @Nullable
    private final BedWarsPlayer broker;
    private final CurrentTeam team;
}
