package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;

public interface UpgradeImprovedEvent<G extends Game> extends BWCancellable {

    G getGame();

    Upgrade getUpgrade();

    UpgradeStorage getStorage();

    double getNewLevel();

    double getOldLevel();

    double getOriginalNewLevel();

    void setNewLevel(double newLevel);
}
