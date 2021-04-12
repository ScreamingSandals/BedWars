package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

public interface OpenTeamSelectionEvent<G extends Game, P extends BWPlayer> extends BWCancellable {
    G getGame();

    P getPlayer();
}
