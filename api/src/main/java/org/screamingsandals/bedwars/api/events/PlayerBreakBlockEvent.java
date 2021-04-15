package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.function.Consumer;

public interface PlayerBreakBlockEvent<G extends Game, P extends BWPlayer, T extends RunningTeam, B extends Wrapper> extends BWCancellable {
    G getGame();

    P getPlayer();

    T getTeam();

    B getBlock();

    boolean isDrops();

    void setDrops(boolean drops);

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<PlayerBreakBlockEvent<Game, BWPlayer, RunningTeam, Wrapper>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PlayerBreakBlockEvent.class, (Consumer) consumer);
    }
}
