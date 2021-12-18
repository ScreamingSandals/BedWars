package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.UpgradeRegisteredEvent;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.SEvent;

@Data
public class UpgradeRegisteredEventImpl implements UpgradeRegisteredEvent<GameImpl>, SEvent {
    private final GameImpl game;
    private final Upgrade upgrade;
    private final UpgradeStorage storage;
}
