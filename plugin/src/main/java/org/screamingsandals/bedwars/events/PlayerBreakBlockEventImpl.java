package org.screamingsandals.bedwars.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PlayerBreakBlockEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.lib.block.BlockHolder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PlayerBreakBlockEventImpl extends CancellableAbstractEvent implements PlayerBreakBlockEvent<GameImpl, BedWarsPlayer, TeamImpl, BlockHolder> {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final TeamImpl team;
    private final BlockHolder block;
    private boolean drops;
}
