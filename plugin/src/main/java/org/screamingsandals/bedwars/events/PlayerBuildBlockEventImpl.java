package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PlayerBuildBlockEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.block.BlockHolder;
import org.screamingsandals.lib.block.state.BlockStateHolder;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerBuildBlockEventImpl extends CancellableAbstractEvent implements PlayerBuildBlockEvent<GameImpl, BedWarsPlayer, CurrentTeam, BlockHolder, BlockStateHolder, Item> {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final CurrentTeam team;
    private final BlockHolder block;
    private final BlockStateHolder replaced;
    private final Item itemInHand;
}
