package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.events.OpenShopEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameStore;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.entity.EntityBasic;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class OpenShopEventImpl extends AbstractEvent implements OpenShopEvent<Game, EntityBasic, BedWarsPlayer, GameStore> {
    private final Game game;
    private final EntityBasic entity;
    private final BedWarsPlayer player;
    private final GameStore gameStore;
    @NotNull
    private OpenShopEvent.Result result = OpenShopEvent.Result.ALLOW;
}
