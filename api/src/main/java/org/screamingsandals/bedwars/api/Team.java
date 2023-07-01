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

package org.screamingsandals.bedwars.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnknownNullability;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.target.Target;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.api.Wrapper;

import java.util.List;

/**
 * <p>Abstract team API.</p>
 *
 * @author ScreamingSandals
 */
@ApiStatus.NonExtendable
public interface Team {
    /**
     * <p>Gets the team's current game.</p>
     *
     * @return the game
     */
    Game getGame();

    /**
     * <p>Gets the team's name.</p>
     *
     * @return the name
     */
    String getName();

    /**
     * <p>Gets the team's color.</p>
     *
     * @return the color
     */
    TeamColor getColor();

    /**
     * <p>Gets the team's spawn locations.</p>
     *
     * @return the spawn locations
     */
    List<? extends Wrapper> getTeamSpawns();

    /**
     * <p>Gets one of the team's spawn locations.</p>
     *
     * @return the spawn location
     */
    Wrapper getRandomSpawn();

    /**
     * Gets the team's target (e.g. block, countdown).
     * Other teams have to invalidate this target, for example by destroying if the target is {@link org.screamingsandals.bedwars.api.game.target.TargetBlock}
     *
     * @return the target
     * @since 0.3.0
     */
    @UnknownNullability("Shouldn't be null if the game is in WAITING or RUNNING state")
    Target getTarget();

    /**
     * <p>Gets the maximal amount of players which can be present in this team.</p>
     *
     * @return the max amount of players
     */
    int getMaxPlayers();

    /**
     * <p>Gets the inventory of the team's shared chest.</p>
     *
     * @return the team chest inventory
     */
    Wrapper getTeamChestInventory();

    /**
     * <p>Adds a new team chest at the specified location.</p>
     * <p>If a team chest is already present at the specified location, this method will fail silently (do nothing).</p>
     *
     * @param location the team chest location or block
     */
    void addTeamChest(Object location);

    /**
     * <p>Removes a team chest at the specified location.</p>
     * <p>If a team chest is not present at the specified location, this method will fail silently (do nothing).</p>
     *
     * @param location the team chest location or block
     */
    void removeTeamChest(Object location);

    /**
     * <p>Determines if a team chest is present at the specified location.</p>
     *
     * @param location the team chest location
     * @return is a team chest present at the specified location?
     */
    boolean isTeamChestRegistered(Object location);

    /**
     * <p>Gets the amount of chests bound to this team.</p>
     *
     * @return the amount of team chests
     */
    int countTeamChests();

    /**
     * <p>Gets the amount of players currently connected in this team.</p>
     *
     * @return the amount of players
     */
    int countConnectedPlayers();

    /**
     * <p>Gets a {@link List} of players currently connected to this team.</p>
     *
     * @return the {@link List} of players
     */
    List<? extends BWPlayer> getPlayers();

    /**
     * <p>Determines if the supplied player is a member of this team.</p>
     *
     * @param player the player
     * @return is the supplied player a member of this team?
     */
    boolean isPlayerInTeam(BWPlayer player);

    /**
     * <p>Determines if this team is dead (eliminated from the game).</p>
     *
     * @return is the team dead?
     */
    boolean isDead();

    /**
     * <p>Determines if this team is alive.</p>
     *
     * @return is the team alive?
     */
    default boolean isAlive() {
        return !isDead();
    }
}
