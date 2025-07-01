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
import org.screamingsandals.bedwars.api.events.BedwarsOpenShopEvent;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStore;
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

    /**
     * Opens the default store using the built-in shop system. It does not fire any event.
     * <p>
     * The player should be in a game, otherwise the behaviour of this method is undefined.
     * This method does not verify whether the player is playing BedWars.
     * <p>
     * Unless you have a specific reason, you should use {@link #tryOpenDefaultStore(Player)}.
     *
     * @param player the player
     * @see #openDefaultStore(Player)
     * @since 0.2.38
     */
    default void openDefaultStore(Player player) {
        openCustomStore(player, null, false);
    }

    /**
     * Opens a custom store using the built-in shop system. It does not fire any event.
     * <p>
     * The player should be in a game, otherwise the behaviour of this method is undefined.
     * This method does not verify whether the player is playing BedWars.
     * <p>
     * Unless you have a specific reason, you should use {@link #tryOpenCustomStore(Player, String)}.
     *
     * @param fileName the file name
     * @param player the player
     * @see #tryOpenCustomStore(Player, String)
     * @since 0.2.38
     */
    default void openCustomStore(Player player, @Nullable String fileName) {
        openCustomStore(player, fileName, false);
    }

    /**
     * Opens a custom store using the built-in shop system. It does not fire any event.
     * <p>
     * The player should be in a game, otherwise the behaviour of this method is undefined.
     * This method does not verify whether the player is playing BedWars.
     * <p>
     * Unless you have a specific reason, you should use {@link #tryOpenCustomStore(Player, String, boolean)}.
     *
     * @param fileName the file name
     * @param player the player
     * @param useParent whether should the parent be used
     * @see #tryOpenCustomStore(Player, String, boolean)
     * @since 0.2.38
     */
    void openCustomStore(Player player, @Nullable String fileName, boolean useParent);

    /**
     * Tries opening a store using the provided GameStore instance.
     * <p>
     * It fires the {@link org.screamingsandals.bedwars.api.events.BedwarsOpenShopEvent}, giving plugins the ability to
     * cancel the request or replace the store.
     * <p>
     * The player must be in a game, otherwise this method simply returns null.
     *
     * @param player the player
     * @param gameStore the game store
     * @return result of the fired event or null on failure
     * @since 0.2.39
     */
    BedwarsOpenShopEvent.@Nullable Result tryOpenStore(Player player, GameStore gameStore);

    /**
     * Tries opening a default store.
     * <p>
     * It fires the {@link org.screamingsandals.bedwars.api.events.BedwarsOpenShopEvent}, giving plugins the ability to
     * cancel the request or replace the store.
     * <p>
     * The player must be in a game, otherwise this method simply returns null.
     *
     * @param player the player
     * @return result of the fired event or null on failure
     * @since 0.2.39
     */
    default BedwarsOpenShopEvent.@Nullable Result tryOpenDefaultStore(Player player) {
        return tryOpenCustomStore(player, null, false);
    }

    /**
     * Tries opening a custom store.
     * <p>
     * It fires the {@link org.screamingsandals.bedwars.api.events.BedwarsOpenShopEvent}, giving plugins the ability to
     * cancel the request or replace the store.
     * <p>
     * The player must be in a game, otherwise this method simply returns null.
     *
     * @param player the player
     * @param fileName the file name
     * @return result of the fired event or null on failure
     * @since 0.2.39
     */
    default BedwarsOpenShopEvent.@Nullable Result tryOpenCustomStore(Player player, String fileName) {
        return tryOpenCustomStore(player, fileName, false);
    }

    /**
     * Tries opening a custom store.
     * <p>
     * It fires the {@link org.screamingsandals.bedwars.api.events.BedwarsOpenShopEvent}, giving plugins the ability to
     * cancel the request or replace the store.
     * <p>
     * The player must be in a game, otherwise this method simply returns null.
     *
     * @param player the player
     * @param fileName the file name
     * @param useParent whether should the parent be used
     * @return result of the fired event or null on failure
     * @since 0.2.39
     */
    BedwarsOpenShopEvent.@Nullable Result tryOpenCustomStore(Player player, String fileName, boolean useParent);

    /**
     * @return Bedwars instance
     */
    static BedwarsAPI getInstance() {
        return Bukkit.getServicesManager().getRegistration(BedwarsAPI.class).getProvider();
    }
}
