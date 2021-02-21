package org.screamingsandals.bedwars.api.game;

import java.util.List;
import java.util.Optional;

public interface GameManager<T extends Game> {
    /**
     * @param name Name of game
     * @return Optional with game or empty if game does not exist
     */
    Optional<T> getGame(String name);

    /**
     * @return List of available games
     */
    List<T> getGames();

    /**
     * @return List of names of all game
     */
    List<String> getGameNames();

    /**
     * @param name Name of game
     * @return true if game is exists
     */
    boolean hasGame(String name);

    /**
     * @return Free game that has highest players in it or empty optional
     */
    Optional<T> getGameWithHighestPlayers();

    /**
     * @return Free game that has lowest players in it or empty optional
     */
    Optional<T> getGameWithLowestPlayers();

    /**
     * @return Game in waiting state or empty optional
     */
    Optional<T> getFirstWaitingGame();

    /**
     * @return Game in running state or empty optional
     */
    Optional<T> getFirstRunningGame();
}
