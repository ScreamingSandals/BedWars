package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface PostSpawnEffectEvent<G extends Game, P extends BWPlayer> {

    G getGame();

    P getPlayer();

    String getEffectsGroupName();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<PostSpawnEffectEvent<Game, BWPlayer>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, PostSpawnEffectEvent.class, (Consumer) consumer);
    }
}
