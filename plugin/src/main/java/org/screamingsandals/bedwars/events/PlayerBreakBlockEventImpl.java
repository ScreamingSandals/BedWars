package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.PlayerBreakBlockEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.block.BlockHolder;
import org.screamingsandals.lib.event.SCancellableEvent;

@Data
public class PlayerBreakBlockEventImpl implements PlayerBreakBlockEvent<GameImpl, BedWarsPlayer, TeamImpl, BlockHolder>, SCancellableEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final TeamImpl team;
    private final BlockHolder block;
    private boolean drops;
    private boolean cancelled;

    public PlayerBreakBlockEventImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, BlockHolder block, boolean drops) {
        this.game = game;
        this.player = player;
        this.team = team;
        this.block = block;
        this.drops = drops;
    }
}
