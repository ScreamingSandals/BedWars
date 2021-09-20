package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface PlayerBuildBlockEvent<G extends Game, P extends BWPlayer, T extends Team, B extends Wrapper, R extends Wrapper, I extends Wrapper> extends BWCancellable {
    G getGame();

    P getPlayer();

    T getTeam();

    B getBlock();

    R getReplaced();

    I getItemInHand();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<PlayerBuildBlockEvent<Game, BWPlayer, Team, Wrapper, Wrapper, Wrapper>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PlayerBuildBlockEvent.class, (Consumer) consumer);
    }
}
