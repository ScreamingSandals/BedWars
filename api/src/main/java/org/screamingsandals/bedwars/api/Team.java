package org.screamingsandals.bedwars.api;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.List;

/**
 * @author Bedwars Team
 * @param <L> Location
 * @param <C> Color
 * @param <G> Game
 * @param <I> Inventory
 */
@ApiStatus.NonExtendable
public interface Team<L extends Wrapper, C extends TeamColor, G extends Game<?,?,?,?,?,?,?,?,?>, I extends Wrapper, P extends BWPlayer> {
    /**
     * @return
     */
    G getGame();

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    C getColor();

    /**
     * @return
     */
    L getTeamSpawn();

    /**
     * @return
     */
    L getTargetBlock();

    /**
     * @return
     */
    int getMaxPlayers();

    I getTeamChestInventory();

    /**
     * @param location
     */
    void addTeamChest(Object location);

    /**
     * @param location
     */
    void removeTeamChest(Object location);

    /**
     * @param location
     * @return
     */
    boolean isTeamChestRegistered(Object location);

    /**
     * @return
     */
    int countTeamChests();

    /**
     * @return
     */
    boolean isTargetBlockIntact();
    /**
     * @return
     */
    int countConnectedPlayers();

    /**
     * @return
     */
    List<P> getPlayers();

    /**
     * @param player
     * @return
     */
    boolean isPlayerInTeam(P player);

    /**
     * @return
     */
    boolean isDead();

    /**
     * @return
     */
    boolean isAlive();
}
