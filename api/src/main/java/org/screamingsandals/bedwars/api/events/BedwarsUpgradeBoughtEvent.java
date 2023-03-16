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
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * @author Bedwars Team
 */
public class BedwarsUpgradeBoughtEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Game game;
    private List<Upgrade> upgrades;
    private Player customer;
    private UpgradeStorage storage;
    private double addLevels;

    /**
     * @param game
     * @param storage
     * @param upgrades
     * @param customer
     * @param addLevels
     */
    public BedwarsUpgradeBoughtEvent(Game game, UpgradeStorage storage, List<Upgrade> upgrades, Player customer,
									 double addLevels) {
        this.game = game;
        this.upgrades = upgrades;
        this.customer = customer;
        this.addLevels = addLevels;
        this.storage = storage;
    }

    public static HandlerList getHandlerList() {
        return BedwarsUpgradeBoughtEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsUpgradeBoughtEvent.handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * @return list of upgrades
     */
    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    /**
     * @return customer
     */
    public Player getCustomer() {
        return customer;
    }

    /**
     * @return addition
     */
    public double getAddLevels() {
        return addLevels;
    }

    /**
     * @return storage for these upgrades type
     */
    public UpgradeStorage getStorage() {
        return storage;
    }

}
