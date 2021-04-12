package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.UpgradeImprovedEvent;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.lib.event.CancellableAbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpgradeImprovedEventImpl extends CancellableAbstractEvent implements UpgradeImprovedEvent<Game> {
    private final Game game;
    private final Upgrade upgrade;
    private final UpgradeStorage storage;
    private final double oldLevel;
    private final double originalNewLevel;

    @Override
    public double getNewLevel() {
        return upgrade.getLevel();
    }

    @Override
    public void setNewLevel(double newLevel) {
        upgrade.setLevel(newLevel);
    }
}
