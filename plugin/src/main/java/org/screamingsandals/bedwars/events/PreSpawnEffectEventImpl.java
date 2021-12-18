package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.PreSpawnEffectEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SCancellableEvent;

@Data
public class PreSpawnEffectEventImpl implements PreSpawnEffectEvent<GameImpl, BedWarsPlayer>, SCancellableEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final String effectsGroupName;
    private boolean cancelled;
}
