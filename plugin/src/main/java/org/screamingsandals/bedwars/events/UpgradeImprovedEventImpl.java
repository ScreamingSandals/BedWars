package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.UpgradeImprovedEvent;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.SCancellableEvent;

@Data
public class UpgradeImprovedEventImpl implements UpgradeImprovedEvent<GameImpl>, SCancellableEvent {
    private final GameImpl game;
    private final Upgrade upgrade;
    private final UpgradeStorage storage;
    private final double oldLevel;
    private final double originalNewLevel;
    private boolean cancelled;

    @Override
    public double getNewLevel() {
        return upgrade.getLevel();
    }

    @Override
    public void setNewLevel(double newLevel) {
        upgrade.setLevel(newLevel);
    }
}
