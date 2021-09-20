package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface PreSpawnEffectEvent<G extends Game, P extends BWPlayer> extends BWCancellable {

    G getGame();

    P getPlayer();

    String getEffectsGroupName();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<PreSpawnEffectEvent<Game, BWPlayer>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PreSpawnEffectEvent.class, (Consumer) consumer);
    }
}
