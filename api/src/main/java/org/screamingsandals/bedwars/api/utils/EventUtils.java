package org.screamingsandals.bedwars.api.utils;

import org.screamingsandals.bedwars.api.events.UpgradeRegisteredEvent;
import org.screamingsandals.bedwars.api.events.UpgradeUnregisteredEvent;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;

import java.util.function.Consumer;

public interface EventUtils {
    <T> void handle(Object pluginObject, Class<T> event, Consumer<T> consumer);

    @Deprecated
    UpgradeRegisteredEvent<Game> fireUpgradeRegisteredEvent(Game game, UpgradeStorage storage, Upgrade upgrade);

    @Deprecated
    UpgradeUnregisteredEvent<Game> fireUpgradeUnregisteredEvent(Game game, UpgradeStorage storage, Upgrade upgrade);
}
