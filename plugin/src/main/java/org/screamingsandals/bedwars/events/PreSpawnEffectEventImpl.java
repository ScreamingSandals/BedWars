package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PreSpawnEffectEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PreSpawnEffectEventImpl extends CancellableAbstractEvent implements PreSpawnEffectEvent<Game, BedWarsPlayer> {
    private final Game game;
    private final BedWarsPlayer player;
    private final String effectsGroupName;
}
