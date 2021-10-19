package org.screamingsandals.bedwars.lobby;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.utils.HologramLocation;
import org.screamingsandals.lib.npc.skin.NPCSkin;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@ConfigSerializable
public class LobbyNPC {
    private final HologramLocation location;
    @Nullable
    private NPCSkin skin;
    private String gamesInventoryName;

}
