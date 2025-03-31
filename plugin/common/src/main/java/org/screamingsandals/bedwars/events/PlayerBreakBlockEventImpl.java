/*
 * Copyright (C) 2025 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.PlayerBreakBlockEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.event.CancellableEvent;

@Data
public class PlayerBreakBlockEventImpl implements PlayerBreakBlockEvent, CancellableEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final TeamImpl team;
    private final BlockPlacement block;
    private boolean drops;
    private boolean cancelled;

    public PlayerBreakBlockEventImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, BlockPlacement block, boolean drops) {
        this.game = game;
        this.player = player;
        this.team = team;
        this.block = block;
        this.drops = drops;
    }

    @Override
    public boolean cancelled() {
        return cancelled;
    }

    @Override
    public void cancelled(boolean cancel) {
        cancelled = cancel;
    }
}
