package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.List;

public interface PlayerKilledEvent<G extends Game, P extends BWPlayer, I extends Wrapper> {
    G getGame();

    P getKiller();

    P getPlayer();

    List<I> getDrops();
}
