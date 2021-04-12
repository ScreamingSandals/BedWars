package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

public interface ApplyPropertyToBoughtItemEvent<G extends Game, P extends BWPlayer, I extends Wrapper> extends ApplyPropertyToItemEvent<G, P, I> {
}
