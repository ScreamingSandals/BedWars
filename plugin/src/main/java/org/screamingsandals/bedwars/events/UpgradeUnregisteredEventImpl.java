package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.UpgradeRegisteredEvent;
import org.screamingsandals.bedwars.api.events.UpgradeUnregisteredEvent;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpgradeUnregisteredEventImpl extends AbstractEvent implements UpgradeUnregisteredEvent<Game> {
    private final Game game;
    private final Upgrade upgrade;
    private final UpgradeStorage storage;
}
