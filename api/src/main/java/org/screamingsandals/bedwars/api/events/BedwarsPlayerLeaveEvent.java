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

package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsPlayerLeaveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private Player player;
    private RunningTeam team;

    /**
     * @param game
     * @param player
     * @param team
     */
    public BedwarsPlayerLeaveEvent(Game game, Player player, RunningTeam team) {
        this.game = game;
        this.player = player;
        this.team = team;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPlayerLeaveEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPlayerLeaveEvent.handlers;
    }

    /**
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return team of player
     */
    public RunningTeam getTeam() {
        return this.team;
    }

}
