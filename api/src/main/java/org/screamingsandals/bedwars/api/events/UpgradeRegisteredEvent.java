package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;

public interface UpgradeRegisteredEvent<G extends Game> {
    G getGame();

    Upgrade getUpgrade();

    UpgradeStorage getStorage();
}
