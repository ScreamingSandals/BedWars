package org.screamingsandals.bedwars.api.player;

import org.screamingsandals.bedwars.api.game.GameParticipant;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.UUID;

public interface BWPlayer extends Wrapper, GameParticipant {
    UUID getUuid();
}
