package org.screamingsandals.bedwars.api;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameManager;
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
     * @return Game manager of the bedwars plugin
     */
    GameManager<?> getGameManager();

    /**
     * @param player Player
     * @return Player's Game or null if player isn't in game
     */
    Game getGameOfPlayer(Player player);

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
    PlayerStatisticsManager<?> getStatisticsManager();

    /**
     * @return Bedwars instance
     */
    static BedwarsAPI getInstance() {
        return Bukkit.getServicesManager().getRegistration(BedwarsAPI.class).getProvider();
    }
}
