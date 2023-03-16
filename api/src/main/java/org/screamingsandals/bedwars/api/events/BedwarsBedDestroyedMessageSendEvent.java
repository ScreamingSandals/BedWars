/*
 * Copyright (C) 2023 ScreamingSandals
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

package org.screamingsandals.bedwars.api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;

/**
 * @author Bedwars Team
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BedwarsBedDestroyedMessageSendEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player destroyer;
    private final Player victim;
    private final Game game;
    private final Team destroyedTeam;

    @NotNull
    private String message;
    private boolean cancelled = false;

    public BedwarsBedDestroyedMessageSendEvent(Player destroyer, Player victim, Game game, Team destroyedTeam, @NotNull String message) {
        this.destroyer = destroyer;
        this.victim = victim;
        this.game = game;
        this.destroyedTeam = destroyedTeam;
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return BedwarsBedDestroyedMessageSendEvent.handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
