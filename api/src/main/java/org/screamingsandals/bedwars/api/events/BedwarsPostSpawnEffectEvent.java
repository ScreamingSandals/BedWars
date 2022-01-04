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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsPostSpawnEffectEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private Player player;
    private String effectsGroupName;

    /**
     * @param game
     * @param player
     * @param effectsGroupName
     */
    public BedwarsPostSpawnEffectEvent(Game game, Player player, String effectsGroupName) {
        this.game = game;
        this.player = player;
        this.effectsGroupName = effectsGroupName;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPostSpawnEffectEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return effects group
     */
    public String getEffectsGroupName() {
        return this.effectsGroupName;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPostSpawnEffectEvent.handlers;
    }

}
