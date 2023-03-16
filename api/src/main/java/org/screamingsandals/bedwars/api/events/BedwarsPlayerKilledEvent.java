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

import org.screamingsandals.bedwars.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Bedwars Team
 */
public class BedwarsPlayerKilledEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private Player killer;
    private Player player;
    private List<ItemStack> drops;

    /**
     * @param game
     * @param player
     * @param killer
     * @param drops
     */
    public BedwarsPlayerKilledEvent(Game game, Player player, Player killer, List<ItemStack> drops) {
        this.player = player;
        this.killer = killer;
        this.game = game;
        this.drops = drops;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPlayerKilledEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPlayerKilledEvent.handlers;
    }

    /**
     * @return killer
     */
    public Player getKiller() {
        return this.killer;
    }

    /**
     * @return victim
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return victim's drops
     */
    public List<ItemStack> getDrops() {
        return this.drops;
    }

}
