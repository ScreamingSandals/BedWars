package org.screamingsandals.bedwars.api.player;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.UUID;

@ApiStatus.NonExtendable
public interface BWPlayer extends Wrapper {
    UUID getUuid();

    boolean isSpectator();

    @Nullable
    String getLatestGameName();

    @Nullable
    Game getGame();
}
