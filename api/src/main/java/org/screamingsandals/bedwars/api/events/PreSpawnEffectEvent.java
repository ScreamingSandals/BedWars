package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

public interface PreSpawnEffectEvent<G extends Game, P extends BWPlayer> extends BWCancellable {

    G getGame();

    P getPlayer();

    String getEffectsGroupName();
}
