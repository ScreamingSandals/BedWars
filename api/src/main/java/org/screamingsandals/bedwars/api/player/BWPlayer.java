package org.screamingsandals.bedwars.api.player;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.UUID;

@ApiStatus.NonExtendable
public interface BWPlayer extends Wrapper {
    UUID getUuid();
}
