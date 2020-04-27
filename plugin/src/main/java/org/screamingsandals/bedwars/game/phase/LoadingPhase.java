package org.screamingsandals.bedwars.game.phase;

import org.screamingsandals.lib.gamecore.core.GameFrame;
import org.screamingsandals.lib.gamecore.core.GameState;
import org.screamingsandals.lib.gamecore.core.cycle.GameCycle;
import org.screamingsandals.lib.gamecore.core.phase.GamePhase;

public class LoadingPhase extends GamePhase {

    public LoadingPhase(GameCycle gameCycle, int runTime) {
        super(gameCycle, runTime);
    }

    @Override
    public void prepare(GameFrame gameFrame) {
        //prepare arena shits here!

        getGameFrame().setActiveState(GameState.WAITING);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
