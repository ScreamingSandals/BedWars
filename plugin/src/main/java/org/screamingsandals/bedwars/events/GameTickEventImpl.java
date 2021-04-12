package org.screamingsandals.bedwars.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.GameTickEvent;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.lib.event.AbstractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GameTickEventImpl extends AbstractEvent implements GameTickEvent<Game> {
    private final Game game;
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
