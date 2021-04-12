package org.screamingsandals.bedwars.events;

import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.events.ApplyPropertyToDisplayedItemEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.material.Item;

import java.util.Map;

public class ApplyPropertyToDisplayedItemEventImpl extends ApplyPropertyToItemEventImpl implements ApplyPropertyToDisplayedItemEvent<Game, BedWarsPlayer, Item> {
    public ApplyPropertyToDisplayedItemEventImpl(Game game, BedWarsPlayer player, String name, Map<String, Object> properties, @NotNull Item stack) {
        super(game, player, name, properties, stack);
    }
}
