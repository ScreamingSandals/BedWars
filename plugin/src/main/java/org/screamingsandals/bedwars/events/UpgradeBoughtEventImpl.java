package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.UpgradeBoughtEvent;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SCancellableEvent;

import java.util.List;

@Data
public class UpgradeBoughtEventImpl implements UpgradeBoughtEvent<GameImpl, BedWarsPlayer>, SCancellableEvent {
    private final GameImpl game;
    private final BedWarsPlayer customer;
    private final List<Upgrade> upgrades;
    private final double addLevels;
    private final UpgradeStorage storage;
    private boolean cancelled;
}
