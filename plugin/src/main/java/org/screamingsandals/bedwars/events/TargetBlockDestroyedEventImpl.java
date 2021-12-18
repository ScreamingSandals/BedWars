package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.TargetBlockDestroyedEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SEvent;

@Data
public class TargetBlockDestroyedEventImpl implements TargetBlockDestroyedEvent<GameImpl, BedWarsPlayer, TeamImpl>, SEvent {
    private final GameImpl game;
    @Nullable
    private final BedWarsPlayer broker;
    private final TeamImpl team;
}
