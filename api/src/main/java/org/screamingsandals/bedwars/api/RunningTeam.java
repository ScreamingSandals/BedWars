package org.screamingsandals.bedwars.api;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
@Deprecated
public interface RunningTeam<E extends BWPlayer, L extends Wrapper, B extends Wrapper, T extends TeamColor, G extends Game<?,?,?,?,?,?,?,?,?>, I extends Wrapper> extends Team<L, T, G, I, E> {


}
