package org.screamingsandals.bedwars.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.screamingsandals.bedwars.api.events.GameTickEvent;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.SEvent;

@Data
@AllArgsConstructor
public class GameTickEventImpl implements GameTickEvent<GameImpl>, SEvent {
    private final GameImpl game;
    private final int previousCountdown;
    private final GameStatus previousStatus;
    private final int countdown;
    private final GameStatus status;
    private final int originalNextCountdown;
    private final GameStatus originalNextStatus;
    private int nextCountdown;
    private GameStatus nextStatus;

    @Override
    public void preventContinuation(boolean prevent) {
        if (prevent) {
            this.nextCountdown = this.countdown;
            this.nextStatus = this.status;
        } else {
            this.nextCountdown = this.originalNextCountdown;
            this.nextStatus = this.originalNextStatus;
        }
    }

    @Override
    public boolean isNextCountdownChanged() {
        return this.nextCountdown != this.originalNextCountdown;
    }

    @Override
    public boolean isNextStatusChanged() {
        return this.nextStatus != this.originalNextStatus;
    }
}
