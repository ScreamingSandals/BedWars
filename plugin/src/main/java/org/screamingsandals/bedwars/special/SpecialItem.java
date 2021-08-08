package org.screamingsandals.bedwars.special;

import lombok.Data;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;

@Data
public abstract class SpecialItem implements org.screamingsandals.bedwars.api.special.SpecialItem<GameImpl, BedWarsPlayer, CurrentTeam> {
    protected final GameImpl game;
    protected final BedWarsPlayer player;
    protected final CurrentTeam team;
}
