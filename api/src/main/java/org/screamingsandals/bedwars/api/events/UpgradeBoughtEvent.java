package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;

import java.util.List;
import java.util.function.Consumer;

public interface UpgradeBoughtEvent<G extends Game, P extends BWPlayer> extends BWCancellable {

    G getGame();

    List<Upgrade> getUpgrades();

    P getCustomer();

    double getAddLevels();

    UpgradeStorage getStorage();

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<UpgradeBoughtEvent<Game, BWPlayer>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, UpgradeBoughtEvent.class, (Consumer) consumer);
    }
}
