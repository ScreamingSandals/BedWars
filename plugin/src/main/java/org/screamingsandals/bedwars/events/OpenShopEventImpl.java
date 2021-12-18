package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.OpenShopEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameStoreImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.entity.EntityBasic;
import org.screamingsandals.lib.event.SEvent;

@Data
public class OpenShopEventImpl implements OpenShopEvent<GameImpl, EntityBasic, BedWarsPlayer, GameStoreImpl>, SEvent {
    private final GameImpl game;
    @Nullable
    private final EntityBasic entity;
    private final BedWarsPlayer player;
    private final GameStoreImpl gameStore;
    @NotNull
    private OpenShopEvent.Result result = OpenShopEvent.Result.ALLOW;
}
