package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PreSpawnEffectEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PreSpawnEffectEventImpl extends CancellableAbstractEvent implements PreSpawnEffectEvent<GameImpl, BedWarsPlayer> {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final String effectsGroupName;
}
