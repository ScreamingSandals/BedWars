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
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsUpgradeImprovedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private UpgradeStorage storage;
    private Upgrade upgrade;
    private double oldLevel;
    private double newLevel;

    /**
     * @param game
     * @param storage
     * @param upgrade
     * @param oldLevel
     * @param newLevel
     */
    public BedwarsUpgradeImprovedEvent(Game game, UpgradeStorage storage, Upgrade upgrade, double oldLevel,
                                       double newLevel) {
        this.game = game;
        this.storage = storage;
        this.upgrade = upgrade;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        upgrade.setLevel(newLevel);
    }

    public static HandlerList getHandlerList() {
        return BedwarsUpgradeImprovedEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsUpgradeImprovedEvent.handlers;
    }

    @Override
    public boolean isCancelled() {
        return upgrade.getLevel() == oldLevel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        upgrade.setLevel(cancel ? oldLevel : newLevel);
    }

    /**
     * @return upgrade
     */
    public Upgrade getUpgrade() {
        return upgrade;
    }

    /**
     * @return storage of this upgrades type
     */
    public UpgradeStorage getStorage() {
        return storage;
    }

    /**
     * @return new level
     */
    public double getNewLevel() {
        return upgrade.getLevel();
    }

    /**
     * @return old level
     */
    public double getOldLevel() {
        return oldLevel;
    }

    /**
     * @return new level (not edited by this event)
     */
    public double getOriginalNewLevel() {
        return newLevel;
    }

    /**
     * @param newLevel
     */
    public void setNewLevel(double newLevel) {
        upgrade.setLevel(newLevel);
    }

}
