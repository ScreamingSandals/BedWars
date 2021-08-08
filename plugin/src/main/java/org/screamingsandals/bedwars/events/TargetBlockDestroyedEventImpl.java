package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.TargetBlockDestroyedEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class TargetBlockDestroyedEventImpl extends AbstractEvent implements TargetBlockDestroyedEvent<GameImpl, BedWarsPlayer, CurrentTeam> {
    private final GameImpl game;
    @Nullable
    private final BedWarsPlayer broker;
    private final CurrentTeam team;
}
