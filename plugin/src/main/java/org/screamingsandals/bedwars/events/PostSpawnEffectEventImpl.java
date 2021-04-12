package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PostSpawnEffectEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostSpawnEffectEventImpl extends AbstractEvent implements PostSpawnEffectEvent<Game, BedWarsPlayer> {
    private final Game game;
    private final BedWarsPlayer player;
    private final String effectsGroupName;
}
