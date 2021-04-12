package org.screamingsandals.bedwars.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PlayerBreakBlockEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.lib.world.BlockHolder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PlayerBreakBlockEventImpl extends CancellableAbstractEvent implements PlayerBreakBlockEvent<Game, BedWarsPlayer, CurrentTeam, BlockHolder> {
    private final Game game;
    private final BedWarsPlayer player;
    private final CurrentTeam team;
    private final BlockHolder block;
    private boolean drops;
}
