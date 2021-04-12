package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;

import java.util.List;

public interface UpgradeBoughtEvent<G extends Game, P extends BWPlayer> extends BWCancellable {

    G getGame();

    List<Upgrade> getUpgrades();

    P getCustomer();

    double getAddLevels();

    UpgradeStorage getStorage();
}
