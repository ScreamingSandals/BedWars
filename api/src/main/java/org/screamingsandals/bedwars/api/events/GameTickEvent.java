package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;

public interface GameTickEvent<G extends Game> {

    G getGame();

    int getCountdown();

    GameStatus getStatus();

    int getNextCountdown();

    void setNextCountdown(int nextCountdown);

    GameStatus getNextStatus();

    void setNextStatus(GameStatus nextStatus);

    int getPreviousCountdown();

    GameStatus getPreviousStatus();


    void preventContinuation(boolean prevent);

    boolean isNextCountdownChanged();

    boolean isNextStatusChanged();

    default boolean isNextTickChanged() {
        return isNextCountdownChanged() || isNextStatusChanged();
    }
}
