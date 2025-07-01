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

import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsOpenShopEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private @Nullable Entity clickedEntity;
    private Game game;
    private Player player;
    private GameStore store;
    private Result result = Result.ALLOW;

    /**
     * @param game
     * @param player
     * @param store
     * @param clickedEntity
     */
    public BedwarsOpenShopEvent(Game game, Player player, GameStore store, @Nullable Entity clickedEntity) {
        this.player = player;
        this.game = game;
        this.clickedEntity = clickedEntity;
        this.store = store;
    }

    /**
     * @return
     */
    public static HandlerList getHandlerList() {
        return BedwarsOpenShopEvent.handlers;
    }

    /**
     * @return
     */
    public @Nullable Entity getEntity() {
        return this.clickedEntity;
    }

    /**
     * @return
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsOpenShopEvent.handlers;
    }

    /**
     * @return
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return
     */
    public GameStore getStore() {
        return this.store;
    }

    @Deprecated
    @Override
    public boolean isCancelled() {
        return result != Result.ALLOW;
    }

    @Deprecated
    @Override
    public void setCancelled(boolean cancel) {
        result = cancel ? Result.DISALLOW_UNKNOWN : Result.ALLOW;
    }

    /**
     * @return
     */
    public Result getResult() {
        return result;
    }

    /**
     * @param result
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * @author Bedwars Team
     */
    public static enum Result {
        ALLOW,
        DISALLOW_THIRD_PARTY_SHOP,
        DISALLOW_LOCKED_FOR_THIS_PLAYER,
        DISALLOW_UNKNOWN,
        DISALLOW_WRONG_TEAM;
    }

}
