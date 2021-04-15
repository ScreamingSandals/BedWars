package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

import java.util.function.Consumer;

public interface TeamChestOpenEvent<G extends Game, P extends BWPlayer, T extends RunningTeam> extends BWCancellable {
    G getGame();

    P getPlayer();

    T getTeam();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<TeamChestOpenEvent<Game, BWPlayer, RunningTeam>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, TeamChestOpenEvent.class, (Consumer) consumer);
    }
}
