package org.screamingsandals.bedwars.api.statistics;

import java.util.UUID;

/**
 * @author Bedwars Team
 */
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

    /**
     * @see PlayerStatistic#getDeaths()
     */
    //@Deprecated
    //int getCurrentDeaths();

    /**
     * @see PlayerStatistic#addDeaths(int)
     */
    //@Deprecated
    //void setCurrentDeaths(int currentDeaths);

    /**
     * @see PlayerStatistic#getDestroyedBeds()
     */
    //@Deprecated
    //int getCurrentDestroyedBeds();

    /**
     * @see PlayerStatistic#addDestroyedBeds(int)
     */
    //@Deprecated
    //void setCurrentDestroyedBeds(int currentDestroyedBeds);

    /**
     * @see PlayerStatistic#getKills()
     */
    //@Deprecated
    //int getCurrentKills();

    /**
     * @see PlayerStatistic#addKills(int)
     */
    //@Deprecated
    //void setCurrentKills(int currentKills);

    /**
     * @see PlayerStatistic#getLoses()
     */
    //@Deprecated
    //int getCurrentLoses();

    /**
     * @see PlayerStatistic#addLoses(int)
     */
    //@Deprecated
    //void setCurrentLoses(int currentLoses);

    /**
     * @see PlayerStatistic#getScore()
     */
    //@Deprecated
    //int getCurrentScore();

    /**
     * @see PlayerStatistic#addScore(int)
     */
    //@Deprecated
    //void setCurrentScore(int currentScore);

    /**
     * @see PlayerStatistic#getWins()
     */
    //@Deprecated
    //int getCurrentWins();

    /**
     * @see PlayerStatistic#addWins(int)
     */
    //@Deprecated
    //void setCurrentWins(int currentWins);

    /**
     * @see PlayerStatistic#getKD()
     */
    //@Deprecated
   // double getCurrentKD();

    /**
     * @see PlayerStatistic#getGames()
     */
    //@Deprecated
    //int getCurrentGames();
}
