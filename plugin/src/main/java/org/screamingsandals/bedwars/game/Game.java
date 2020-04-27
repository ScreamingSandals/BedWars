package org.screamingsandals.bedwars.game;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.events.game.BedWarsGameCyclePrepareEvent;
import org.screamingsandals.bedwars.game.cycle.MultiGameBungeeCycle;
import org.screamingsandals.bedwars.game.cycle.MultiGameCycle;
import org.screamingsandals.bedwars.game.cycle.SingleGameBungeeCycle;
import org.screamingsandals.bedwars.game.phase.LoadingPhase;
import org.screamingsandals.bedwars.game.phase.WaitingPhase;
import org.screamingsandals.lib.gamecore.GameCore;
import org.screamingsandals.lib.gamecore.core.GameFrame;
import org.screamingsandals.lib.gamecore.core.GameState;
import org.screamingsandals.lib.gamecore.core.cycle.GameCycle;

public class Game extends GameFrame {

    public Game(String gameName) {
        super(gameName);
    }

    @Override
    public void prepare() {
        super.prepare();

        final GameCycle gameCycle = prepareGameCycle();

        setGameCycle(gameCycle);
        setCycleType(gameCycle.getType());

        gameCycle.getGamePhases().put(GameState.LOADING, new LoadingPhase(gameCycle, -1));
        gameCycle.getGamePhases().put(GameState.WAITING, new WaitingPhase(gameCycle, getLobbyTime()));
    }

    private GameCycle prepareGameCycle() {
        GameCycle toReturn = null;
        final BedWarsGameCyclePrepareEvent event = new BedWarsGameCyclePrepareEvent(this);
        if (GameCore.fireEvent(event)) {
            toReturn = event.getGameCycle();
        }

        if (toReturn != null) {
            return toReturn;
        }

        if (Main.getMainConfig().getBoolean("bungeecord.enabled")) {
            if (Main.getMainConfig().getBoolean("bungeecord.multi-arena-setup")) {
                toReturn = new MultiGameBungeeCycle(this);
            } else {
                toReturn = new SingleGameBungeeCycle(this);
            }
        } else {
            toReturn = new MultiGameCycle(this);
        }

        return toReturn;
    }
}
