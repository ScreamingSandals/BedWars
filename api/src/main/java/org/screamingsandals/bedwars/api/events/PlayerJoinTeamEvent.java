package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

public interface PlayerJoinTeamEvent<G extends Game, P extends BWPlayer, T extends RunningTeam> extends BWCancellable {

    G getGame();

    P getPlayer();

    T getTeam();

    T getPreviousTeam();
}
