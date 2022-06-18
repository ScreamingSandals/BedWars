package org.screamingsandals.bedwars.api.game;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface GroupManager {
    /**
     *
     * @param group name of the group
     * @param game the game that should be added to the group
     * @return true if successful
     * @since 0.3.0
     */
    boolean addToGroup(@Pattern("[a-zA-Z\\d\\-_]+") @NotNull String group, @NotNull Game game);

    /**
     *
     * @param group name of the group
     * @param game the game that should be removed from the group
     * @return true if successful
     * @since 0.3.0
     */
    boolean removeFromGroup(@Pattern("[a-zA-Z\\d\\-_]+") @NotNull String group, @NotNull Game game);

    /**
     *
     * @param group name of the group
     * @return list of all games inside the group
     * @since 0.3.0
     */
    @NotNull
    List<? extends @NotNull Game> getGamesInGroup(@Pattern("[a-zA-Z\\d\\-_]+") @NotNull String group);

    /**
     *
     * @return all groups that have at least one game
     * @since 0.3.0
     */
    List<String> getExistingGroups();
}
