package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.PlayerKilledEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SEvent;
import org.screamingsandals.lib.item.Item;

import java.util.List;

@Data
public class PlayerKilledEventImpl implements PlayerKilledEvent<GameImpl, BedWarsPlayer, Item>, SEvent {
    private final GameImpl game;
    private final BedWarsPlayer killer;
    private final BedWarsPlayer player;
    private final List<Item> drops;
}
