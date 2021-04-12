package org.screamingsandals.bedwars.events;

import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.events.ApplyPropertyToBoughtItemEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.material.Item;

import java.util.Map;

public class ApplyPropertyToBoughtItemEventImpl extends ApplyPropertyToItemEventImpl implements ApplyPropertyToBoughtItemEvent<Game, BedWarsPlayer, Item> {
    public ApplyPropertyToBoughtItemEventImpl(Game game, BedWarsPlayer player, String name, Map<String, Object> properties, @NotNull Item stack) {
        super(game, player, name, properties, stack);
    }
}
