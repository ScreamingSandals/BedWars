package org.screamingsandals.bedwars.api;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.List;

/**
 * <p>Abstract team API.</p>
 *
 * @author ScreamingSandals
 * @param <L> the location impl (LocationHolder)
 * @param <C> the color impl (TeamColorImpl)
 * @param <G> the game impl (GameImpl)
 * @param <I> the inventory impl (Container)
 * @param <P> the player impl (BedWarsPlayer)
 */
@ApiStatus.NonExtendable
public interface Team<L extends Wrapper, C extends TeamColor, G extends Game<?, ?, ?, ?, ?, ?, ?, ?, ?>, I extends Wrapper, P extends BWPlayer> {
    /**
     * <p>Gets the team's current game.</p>
     *
     * @return the game
     */
    G getGame();

    /**
     * <p>Gets the team's name.</p>
     *
     * @return the name
     */
    String getName();

    /**
     * <p>Gets the team's color.</p>
     *
     * @return the color
     */
    C getColor();

    /**
     * <p>Gets the team's spawn location.</p>
     *
     * @return the spawn location
     */
    L getTeamSpawn();

    /**
     * <p>Gets the team's target block (e.g. bed) location.</p>
     *
     * @return the target block location
     */
    L getTargetBlock();

    /**
     * <p>Gets the maximal amount of players which can be present in this team.</p>
     *
     * @return the max amount of players
     */
    int getMaxPlayers();

    /**
     * <p>Gets the inventory of the team's shared chest.</p>
     *
     * @return the team chest inventory
     */
    I getTeamChestInventory();

    /**
     * <p>Adds a new team chest at the specified location.</p>
     * <p>If a team chest is already present at the specified location, this method will fail silently (do nothing).</p>
     *
     * @param location the team chest location or block
     */
    void addTeamChest(Object location);

    /**
     * <p>Removes a team chest at the specified location.</p>
     * <p>If a team chest is not present at the specified location, this method will fail silently (do nothing).</p>
     *
     * @param location the team chest location or block
     */
    void removeTeamChest(Object location);

    /**
     * <p>Determines if a team chest is present at the specified location.</p>
     *
     * @param location the team chest location
     * @return is a team chest present at the specified location?
     */
    boolean isTeamChestRegistered(Object location);

    /**
     * <p>Gets the amount of chests bound to this team.</p>
     *
     * @return the amount of team chests
     */
    int countTeamChests();

    /**
     * <p>Determines if the team's target block (e.g. bed) is intact (not broken).</p>
     *
     * @return is the target block intact?
     */
    boolean isTargetBlockIntact();

    /**
     * <p>Gets the amount of players currently connected in this team.</p>
     *
     * @return the amount of players
     */
    int countConnectedPlayers();

    /**
     * <p>Gets a {@link List} of players currently connected to this team.</p>
     *
     * @return the {@link List} of players
     */
    List<P> getPlayers();

    /**
     * <p>Determines if the supplied player is a member of this team.</p>
     *
     * @param player the player
     * @return is the supplied player a member of this team?
     */
    boolean isPlayerInTeam(P player);

    /**
     * <p>Determines if this team is dead (eliminated from the game).</p>
     *
     * @return is the team dead?
     */
    boolean isDead();

    /**
     * <p>Determines if this team is alive.</p>
     *
     * @return is the team alive?
     */
    default boolean isAlive() {
        return !isDead();
    }
}
