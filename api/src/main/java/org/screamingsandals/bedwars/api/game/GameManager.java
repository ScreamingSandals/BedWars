package org.screamingsandals.bedwars.api.game;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApiStatus.NonExtendable
public interface GameManager<T extends Game<?, ?, ?, ?, ?, ?, ?, ?, ?>> {
    /**
     * @param name Name of game or string representation of an uuid
     * @return Optional with game or empty if game does not exist
     * @see #getGame(UUID)
     */
    Optional<T> getGame(String name);

    /**
     * @param uuid Unique id of the game
     * @return Optional with the game or empty if the game does not exist
     */
    Optional<T> getGame(UUID uuid);

    /**
     * @return List of available games
     */
    List<T> getGames();

    /**
     * @return List of names of all game
     */
    List<String> getGameNames();

    /**
     * @param name Name of game or string representation of an uuid
     * @return true if the game exists
     */
    boolean hasGame(String name);

    /**
     * @param uuid Unique id of the game
     * @return true if the game exists
     */
    boolean hasGame(UUID uuid);

    /**
     * @return Free game that has the highest players in it or empty optional
     */
    Optional<T> getGameWithHighestPlayers();

    /**
     * @return Free game that has the lowest players in it or empty optional
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
