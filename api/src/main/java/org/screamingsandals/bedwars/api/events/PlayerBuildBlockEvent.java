package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

public interface PlayerBuildBlockEvent<G extends Game, P extends BWPlayer, T extends RunningTeam, B extends Wrapper, R extends Wrapper, I extends Wrapper> extends BWCancellable {
    G getGame();

    P getPlayer();

    T getTeam();

    B getBlock();

    R getReplaced();

    I getItemInHand();
}
