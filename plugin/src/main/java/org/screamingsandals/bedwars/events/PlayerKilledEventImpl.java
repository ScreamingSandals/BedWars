package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PlayerKilledEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.AbstractEvent;
import org.screamingsandals.lib.material.Item;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerKilledEventImpl extends AbstractEvent implements PlayerKilledEvent<GameImpl, BedWarsPlayer, Item> {
    private final GameImpl game;
    private final BedWarsPlayer killer;
    private final BedWarsPlayer player;
    private final List<Item> drops;
}
