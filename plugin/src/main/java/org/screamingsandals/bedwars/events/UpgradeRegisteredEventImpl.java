package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.UpgradeRegisteredEvent;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpgradeRegisteredEventImpl extends AbstractEvent implements UpgradeRegisteredEvent<GameImpl> {
    private final GameImpl game;
    private final Upgrade upgrade;
    private final UpgradeStorage storage;
}
