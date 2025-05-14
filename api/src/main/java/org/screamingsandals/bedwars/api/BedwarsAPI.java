/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.api;

import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.bedwars.api.statistics.PlayerStatisticsManager;
import org.screamingsandals.bedwars.api.utils.ColorChanger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Bedwars Team
 */
public interface BedwarsAPI {

    /**
     * @return List of available games
     */
    List<Game> getGames();

    /**
     * @param name Name of game
     * @return true if game is exists
     */
    boolean isGameWithNameExists(String name);

    /**
     * @param name Name of game
     * @return Game or null if game is not exists
     */
    Game getGameByName(String name);

    /**
     * @param player Player
     * @return Player's Game or null if player isn't in game
     */
    Game getGameOfPlayer(Player player);

    /**
     * @return Free game that has highest players in it
     */
    Game getGameWithHighestPlayers();

    /**
     * @return Free game that has lowest players in it
     */
    Game getGameWithLowestPlayers();

    /**
     * @param player Player
     * @return true if player is in any game
     */
    boolean isPlayerPlayingAnyGame(Player player);

    /**
     * @return List of existing spawner types
     */
    List<ItemSpawnerType> getItemSpawnerTypes();

    /**
     * @param name Name of item spawner type
     * @return boolean Is spawner type registered
     */
    boolean isItemSpawnerTypeRegistered(String name);

    /**
     * @param name Name of item spawner type
     * @return ItemSpawnerType by name or null if type isn't exists
     */
    ItemSpawnerType getItemSpawnerTypeByName(String name);

    /**
     * @param entity Entity
     * @return true if entity is in game
     */
    boolean isEntityInGame(Entity entity);

    /**
     * @param entity Entity
     * @return Game of entity or null
     */
    Game getGameOfEntity(Entity entity);

    /**
     * @return Game in waiting state or null
     */
    Game getFirstWaitingGame();

    /**
     * @return Game in running state or null
     */
    Game getFirstRunningGame();

    /**
     * @param entity Entity
     * @param game   Game
     */
    void registerEntityToGame(Entity entity, Game game);

    /**
     * @param entity Entity
     */
    void unregisterEntityFromGame(Entity entity);

    /**
     * @return String of Bedwars Version
     */
    String getPluginVersion();

    /**
     * @return Color changer for coloring ItemStacks
     */
    ColorChanger getColorChanger();

    /**
     *
     * @return hub server name from config
     */
    String getHubServerName();

    /**
     *
     * @return PlayerStatisticsManager if statistics are enabled; otherwise null
     */
    PlayerStatisticsManager getStatisticsManager();

    default void openDefaultStore(Player player) {
        openCustomStore(player, null, false);
    }

    default void openCustomStore(Player player, @Nullable String fileName) {
        openCustomStore(player, fileName, false);
    }

    void openCustomStore(Player player, @Nullable String fileName, boolean useParent);

    /**
     * @return Bedwars instance
     */
    static BedwarsAPI getInstance() {
        return Bukkit.getServicesManager().getRegistration(BedwarsAPI.class).getProvider();
    }
}
