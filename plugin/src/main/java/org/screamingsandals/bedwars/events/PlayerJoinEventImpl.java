package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.PlayerJoinEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SCancellableEvent;

@Data
public class PlayerJoinEventImpl implements PlayerJoinEvent<GameImpl, BedWarsPlayer>, SCancellableEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
    @Nullable
    private String cancelMessage;
    private boolean cancelled;
}
