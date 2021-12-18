package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.PlayerRespawnedEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SEvent;

@Data
public class PlayerRespawnedEventImpl implements PlayerRespawnedEvent<GameImpl, BedWarsPlayer>, SEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
}
