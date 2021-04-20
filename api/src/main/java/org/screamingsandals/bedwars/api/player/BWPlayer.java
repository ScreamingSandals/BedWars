package org.screamingsandals.bedwars.api.player;

import org.screamingsandals.lib.utils.Wrapper;

import java.util.UUID;

public interface BWPlayer extends Wrapper {
    UUID getUuid();
}
