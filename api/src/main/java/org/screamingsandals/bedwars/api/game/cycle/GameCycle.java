package org.screamingsandals.bedwars.api.game.cycle;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.phase.GamePhase;
import org.screamingsandals.lib.gamecore.core.GameState;

import java.util.List;
import java.util.Map;

public interface GameCycle {
    /**
     * @return current game for this cycle
     */
    Game getGame();

    /**
     * @return active phase that is currently running
     */
    GamePhase getCurrentPhase();

    /**
     * Sets the current phase of this cycle
     *
     * @param gamePhase the game phase
     */
    void setCurrentPhase(GamePhase gamePhase);

    /**
     * NOTE: these phases will not run as custom, only as default ones.
     * If one phase for that game state already exists, it will not be added.
     *
     * @return map of the phases, provided by base plugin
     */
    Map<GameState, GamePhase> getGamePhases();

    /**
     * @return list of custom phases, added by external plugins
     */
    List<GamePhase> getCustomPhases();

    /**
     * adds the phase to the map
     *
     * @param gameState state for the phase
     * @param gamePhase the phase itself
     */
    void addPhase(GameState gameState, GamePhase gamePhase);

    /**
     * removes the phase for registered state, useful if you want to replace default phase
     *
     * @param gameState state for the phase
     */
    void removePhase(GameState gameState);

    /**
     * Custom phase
     *
     * @param gamePhase the phase itself
     */
    void addCustomPhase(GamePhase gamePhase);

    /**
     * @return active type of the cycle, you can't change that while running
     */
    org.screamingsandals.lib.gamecore.core.cycle.GameCycle.Type getType();
}
