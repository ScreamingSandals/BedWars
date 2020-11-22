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
    @Deprecated
    default int getCurrentDeaths() {
        return getDeaths();
    }

    /**
     * @see PlayerStatistic#addDeaths(int)
     */
    @Deprecated
    default void setCurrentDeaths(int currentDeaths) {
        addDeaths(currentDeaths - getDeaths());
    }

    /**
     * @see PlayerStatistic#getDestroyedBeds()
     */
    @Deprecated
    default int getCurrentDestroyedBeds() {
        return getDestroyedBeds();
    }

    /**
     * @see PlayerStatistic#addDestroyedBeds(int)
     */
    @Deprecated
    default void setCurrentDestroyedBeds(int currentDestroyedBeds) {
        addDestroyedBeds(currentDestroyedBeds - getDestroyedBeds());
    }

    /**
     * @see PlayerStatistic#getKills()
     */
    @Deprecated
    default int getCurrentKills() {
        return getKills();
    }

    /**
     * @see PlayerStatistic#addKills(int)
     */
    @Deprecated
    default void setCurrentKills(int currentKills) {
        addKills(currentKills - getKills());
    }

    /**
     * @see PlayerStatistic#getLoses()
     */
    @Deprecated
    default int getCurrentLoses() {
        return getLoses();
    }

    /**
     * @see PlayerStatistic#addLoses(int)
     */
    @Deprecated
    default void setCurrentLoses(int currentLoses) {
        addLoses(currentLoses - getLoses());
    }

    /**
     * @see PlayerStatistic#getScore()
     */
    @Deprecated
    default int getCurrentScore() {
        return getScore();
    }

    /**
     * @see PlayerStatistic#addScore(int)
     */
    @Deprecated
    default void setCurrentScore(int currentScore) {
        addScore(currentScore - getScore());
    }

    /**
     * @see PlayerStatistic#getWins()
     */
    @Deprecated
    default int getCurrentWins() {
        return getWins();
    }

    /**
     * @see PlayerStatistic#addWins(int)
     */
    @Deprecated
    default void setCurrentWins(int currentWins) {
        addWins(currentWins - getWins());
    }

    /**
     * @see PlayerStatistic#getKD()
     */
    @Deprecated
    default double getCurrentKD() {
        return getKD();
    }

    /**
     * @see PlayerStatistic#getGames()
     */
    @Deprecated
    default int getCurrentGames() {
        return getGames();
    }
}
