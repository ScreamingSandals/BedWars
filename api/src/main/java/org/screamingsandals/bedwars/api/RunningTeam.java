package org.screamingsandals.bedwars.api;

import org.bukkit.inventory.Inventory;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.List;

/**
 * @author Bedwars Team
 */
public interface RunningTeam<E extends Wrapper, L extends Wrapper, B extends Wrapper, T extends TeamColor, G extends Game<?,?,?,?,?,?>> extends Team<L, T, G> {
    /**
     * @return
     */
    int countConnectedPlayers();

    /**
     * @return
     */
    List<E> getConnectedPlayers();

    /**
     * @param player
     * @return
     */
    boolean isPlayerInTeam(E player);

    /**
     * @return
     */
    boolean isDead();

    /**
     * @return
     */
    boolean isAlive();

    /**
     * @return
     */
    boolean isTargetBlockExists();

    /**
     * @return
     */
    org.bukkit.scoreboard.Team getScoreboardTeam();

    /**
     * @param location
     */
    void addTeamChest(L location);

    /**
     * @param block
     */
    void addTeamChestBlock(B block);

    /**
     * @param location
     */
    void removeTeamChest(L location);

    /**
     * @param block
     */
    void removeTeamChestBlock(B block);

    /**
     * @param location
     * @return
     */
    boolean isTeamChestRegistered(L location);

    /**
     * @param block
     * @return
     */
    boolean isTeamChestBlockRegistered(B block);

    /**
     * @return
     */
    Inventory getTeamChestInventory();

    /**
     * @return
     */
    int countTeamChests();
}
