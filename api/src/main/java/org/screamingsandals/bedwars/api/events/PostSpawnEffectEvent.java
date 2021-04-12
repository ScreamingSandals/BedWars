package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

public interface PostSpawnEffectEvent<G extends Game, P extends BWPlayer> {

    G getGame();

    P getPlayer();

    String getEffectsGroupName();
}
