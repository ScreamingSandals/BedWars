package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.PlayerJoinedEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerJoinedEventImpl extends AbstractEvent implements PlayerJoinedEvent<Game, BedWarsPlayer, CurrentTeam> {
    private final Game game;
    private final BedWarsPlayer player;
    @Nullable
    private final CurrentTeam team;
}
