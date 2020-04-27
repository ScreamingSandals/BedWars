package org.screamingsandals.bedwars.game.cycle;

import org.screamingsandals.lib.gamecore.core.GameFrame;
import org.screamingsandals.lib.gamecore.core.cycle.GameCycle;

public class MultiGameCycle extends GameCycle {

    public MultiGameCycle(GameFrame gameFrame) {
        super(gameFrame);
        setType(Type.MULTI_GAME);
    }
}
