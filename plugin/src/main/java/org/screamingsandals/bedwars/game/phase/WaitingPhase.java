package org.screamingsandals.bedwars.game.phase;

import org.screamingsandals.lib.gamecore.core.GameFrame;
import org.screamingsandals.lib.gamecore.core.cycle.GameCycle;
import org.screamingsandals.lib.gamecore.core.phase.GamePhase;

public class WaitingPhase extends GamePhase {

    public WaitingPhase(GameCycle gameCycle, int runTime) {
        super(gameCycle, runTime);
    }

    @Override
    public void prepare(GameFrame gameFrame) {

    }

    @Override
    public void tick() {
        super.tick();
    }
}
