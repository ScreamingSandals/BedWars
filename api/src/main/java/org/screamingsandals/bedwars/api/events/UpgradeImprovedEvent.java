package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface UpgradeImprovedEvent<G extends Game> extends BWCancellable {

    G getGame();

    Upgrade getUpgrade();

    UpgradeStorage getStorage();

    double getNewLevel();

    double getOldLevel();

    double getOriginalNewLevel();

    void setNewLevel(double newLevel);

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<UpgradeImprovedEvent<Game>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, UpgradeImprovedEvent.class, (Consumer) consumer);
    }
}
