package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.OpenShopEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameStoreImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.entity.EntityBasic;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class OpenShopEventImpl extends AbstractEvent implements OpenShopEvent<GameImpl, EntityBasic, BedWarsPlayer, GameStoreImpl> {
    private final GameImpl game;
    @Nullable
    private final EntityBasic entity;
    private final BedWarsPlayer player;
    private final GameStoreImpl gameStore;
    @NotNull
    private OpenShopEvent.Result result = OpenShopEvent.Result.ALLOW;
}
