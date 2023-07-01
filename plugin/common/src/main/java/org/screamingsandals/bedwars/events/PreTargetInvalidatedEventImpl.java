/*
 * Copyright (C) 2022 ScreamingSandals
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.PreTargetInvalidatedEvent;
import org.screamingsandals.bedwars.api.events.TargetInvalidationReason;
import org.screamingsandals.bedwars.api.game.target.Target;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.event.CancellableEvent;

@Data
public class PreTargetInvalidatedEventImpl implements PreTargetInvalidatedEvent, CancellableEvent {
    private final @NotNull GameImpl game;
    private final @NotNull TeamImpl team;
    private final @NotNull Target target;
    private final @NotNull TargetInvalidationReason reason;
    private final @Nullable Block blockType;
    private final @Nullable BedWarsPlayer initiator;

    private boolean cancelled;

    @Override
    public boolean cancelled() {
        return cancelled;
    }

    @Override
    public void cancelled(boolean cancel) {
        cancelled = cancel;
    }
}
