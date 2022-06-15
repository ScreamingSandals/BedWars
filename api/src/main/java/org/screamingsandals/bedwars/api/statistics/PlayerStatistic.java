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

package org.screamingsandals.bedwars.api.statistics;

import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

/**
 * @author ScreamingSandals
 */
@ApiStatus.NonExtendable
public interface PlayerStatistic {
    /**
     * @return player's deaths
     */
    int getDeaths();

    /**
     * @return number of beds destroyed by this player
     */
    int getDestroyedBeds();

    /**
     * @return player's kills
     */
    int getKills();

    /**
     * @return player's loses
     */
    int getLoses();

    /**
     * @return player's name
     */
    String getName();

    /**
     * @return player's score
     */
    int getScore();

    /**
     * Calculates player's level based on settings and his total score
     *
     * @return calculated player's level
     */
    int getLevel();

    /**
     * Calculates how many scores a player has earned since last level
     *
     * @return calculated player's new score
     */
    int getScoreSincePreviousLevel();

    /**
     * Returns the requirement for the next level
     *
     * @return next level requirement
     */
    int getNeededScoreToNextLevel();

    /**
     * @return uuid of this player
     */
    UUID getUuid();

    /**
     * @return number of wins
     */
    int getWins();

    /**
     * @return K/D ratio
     */
    double getKD();

    /**
     * @return number of played games
     */
    int getGames();

    /**
     * @param deaths Number of new deaths
     */
    void addDeaths(int deaths);

    /**
     * @param destroyedBeds Number of new destroyed beds
     */
    void addDestroyedBeds(int destroyedBeds);

    /**
     * @param kills Number of new kills
     */
    void addKills(int kills);

    /**
     * @param loses Number of new loses
     */
    void addLoses(int loses);

    /**
     * @param score Number of new score
     */
    void addScore(int score);

    /**
     * @param wins Number of new wins
     */
    void addWins(int wins);
}
