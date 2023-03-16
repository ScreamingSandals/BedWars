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

import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsSavePlayerStatisticEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private PlayerStatistic playerStatistic = null;

    /**
     * @param playerStatistic
     */
    public BedwarsSavePlayerStatisticEvent(PlayerStatistic playerStatistic) {
        this.playerStatistic = playerStatistic;
    }

    public static HandlerList getHandlerList() {
        return BedwarsSavePlayerStatisticEvent.handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsSavePlayerStatisticEvent.handlers;
    }

    /**
     * @return statistics
     */
    public PlayerStatistic getPlayerStatistic() {
        return this.playerStatistic;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}