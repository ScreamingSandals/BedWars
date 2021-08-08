package org.screamingsandals.bedwars.special;

import lombok.Data;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;

@Data
public abstract class SpecialItem implements org.screamingsandals.bedwars.api.special.SpecialItem<Game, BedWarsPlayer, CurrentTeam> {
    protected final Game game;
    protected final BedWarsPlayer player;
    protected final CurrentTeam team;
}
