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

package org.screamingsandals.bedwars.api;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * @author Bedwars Team
 */
public interface RunningTeam extends Team {
    /**
     * @return
     */
    int countConnectedPlayers();

    /**
     * @return
     */
    List<Player> getConnectedPlayers();

    /**
     * @param player
     * @return
     */
    boolean isPlayerInTeam(Player player);

    /**
     * @return
     */
    boolean isDead();

    /**
     * @return
     */
    boolean isAlive();

    /**
     * @return
     */
    boolean isTargetBlockExists();

    /**
     * @return
     */
    org.bukkit.scoreboard.Team getScoreboardTeam();

    /**
     * @param location
     */
    void addTeamChest(Location location);

    /**
     * @param block
     */
    void addTeamChest(Block block);

    /**
     * @param location
     */
    void removeTeamChest(Location location);

    /**
     * @param block
     */
    void removeTeamChest(Block block);

    /**
     * @param location
     * @return
     */
    boolean isTeamChestRegistered(Location location);

    /**
     * @param block
     * @return
     */
    boolean isTeamChestRegistered(Block block);

    /**
     * @return
     */
    Inventory getTeamChestInventory();

    /**
     * @return
     */
    int countTeamChests();
}
