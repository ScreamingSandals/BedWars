package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.UpgradeBoughtEvent;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpgradeBoughtEventImpl extends CancellableAbstractEvent implements UpgradeBoughtEvent<Game, BedWarsPlayer> {
    private final Game game;
    private final BedWarsPlayer customer;
    private final List<Upgrade> upgrades;
    private final double addLevels;
    private final UpgradeStorage storage;
}
