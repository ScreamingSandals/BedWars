package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;

import java.util.function.Consumer;

public interface UpgradeUnregisteredEvent<G extends Game> {
    G getGame();

    Upgrade getUpgrade();

    UpgradeStorage getStorage();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<UpgradeUnregisteredEvent<Game>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, UpgradeUnregisteredEvent.class, (Consumer) consumer);
    }
}
