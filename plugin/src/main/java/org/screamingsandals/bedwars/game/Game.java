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
import org.screamingsandals.lib.gamecore.core.GameType;
import org.screamingsandals.lib.gamecore.core.cycle.GameCycle;

public class Game extends GameFrame {

    public Game(String gameName, GameType gameType) {
        super(gameName, gameType);
    }

    @Override
    public void prepare() {
        super.prepare();

        final GameCycle gameCycle = prepareGameCycle();
        setGameCycle(gameCycle);

        gameCycle.getGamePhases().put(GameState.LOADING, new LoadingPhase(gameCycle, -1));
        gameCycle.getGamePhases().put(GameState.WAITING, new WaitingPhase(gameCycle, getLobbyTime()));
    }

    private GameCycle prepareGameCycle() {
        GameCycle toReturn = null;
        final var gameType = getGameType();
        final var event = new BedWarsGameCyclePrepareEvent(this);
        if (GameCore.fireEvent(event)) {
            toReturn = event.getGameCycle();
        }

        if (toReturn != null) {
            return toReturn;
        }

        if (Main.getMainConfig().getBoolean("bungeecord.enabled")) {
            if (gameType == GameType.MULTI_GAME_BUNGEE) {
                toReturn = new MultiGameBungeeCycle(this);
            } else if (gameType == GameType.SINGLE_GAME_BUNGEE) {
                toReturn = new SingleGameBungeeCycle(this);
            }
            return toReturn;
        }

        if (gameType == GameType.MULTI_GAME) {
            toReturn = new MultiGameCycle(this);
        }

        return toReturn;
    }
}
