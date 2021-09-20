package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.List;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface PlayerKilledEvent<G extends Game, P extends BWPlayer, I extends Wrapper> {
    G getGame();

    P getKiller();

    P getPlayer();

    List<I> getDrops();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<PlayerKilledEvent<Game, BWPlayer, Wrapper>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PlayerKilledEvent.class, (Consumer) consumer);
    }
}
