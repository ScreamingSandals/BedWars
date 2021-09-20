package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface PlayerJoinTeamEvent<G extends Game, P extends BWPlayer, T extends Team> extends BWCancellable {

    G getGame();

    P getPlayer();

    T getTeam();

    T getPreviousTeam();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<PlayerJoinTeamEvent<Game, BWPlayer, Team>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PlayerJoinTeamEvent.class, (Consumer) consumer);
    }
}
