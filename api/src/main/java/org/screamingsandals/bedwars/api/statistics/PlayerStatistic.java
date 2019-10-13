package org.screamingsandals.bedwars.api.statistics;

import java.util.UUID;

/**
 * @author Bedwars Team
 */
public interface PlayerStatistic {
    /**
     * @return
     */
    int getCurrentDeaths();

    /**
     * @param currentDeaths
     */
    void setCurrentDeaths(int currentDeaths);

    /**
     * @return
     */
    int getCurrentDestroyedBeds();

    /**
     * @param currentDestroyedBeds
     */
    void setCurrentDestroyedBeds(int currentDestroyedBeds);

    /**
     * @return
     */
    int getCurrentKills();

    /**
     * @param currentKills
     */
    void setCurrentKills(int currentKills);

    /**
     * @return
     */
    int getCurrentLoses();

    /**
     * @param currentLoses
     */
    void setCurrentLoses(int currentLoses);

    /**
     * @return
     */
    int getCurrentScore();

    /**
     * @param currentScore
     */
    void setCurrentScore(int currentScore);

    /**
     * @return
     */
    int getCurrentWins();

    /**
     * @param currentWins
     */
    void setCurrentWins(int currentWins);

    /**
     * @return
     */
    double getCurrentKD();

    /**
     * @return
     */
    int getCurrentGames();

    /**
     * @return
     */
    int getDeaths();

    /**
     * @return
     */
    int getDestroyedBeds();

    /**
     * @return
     */
    int getKills();

    /**
     * @return
     */
    int getLoses();

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    int getScore();

    /**
     * @return
     */
    UUID getUuid();

    /**
     * @return
     */
    int getWins();

    /**
     * @return
     */
    double getKD();

    /**
     * @return
     */
    int getGames();
}
