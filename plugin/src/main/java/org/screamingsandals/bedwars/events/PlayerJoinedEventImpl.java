package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.PlayerJoinedEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SEvent;

@Data
public class PlayerJoinedEventImpl implements PlayerJoinedEvent<GameImpl, BedWarsPlayer, TeamImpl>, SEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
    @Nullable
    private final TeamImpl team;
}
