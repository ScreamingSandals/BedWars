package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PlayerKilledEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.AbstractEvent;
import org.screamingsandals.lib.material.Item;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerKilledEventImpl extends AbstractEvent implements PlayerKilledEvent<Game, BedWarsPlayer, Item> {
    private final Game game;
    private final BedWarsPlayer killer;
    private final BedWarsPlayer player;
    private final List<Item> drops;
}
