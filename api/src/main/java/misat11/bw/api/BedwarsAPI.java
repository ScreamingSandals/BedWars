package misat11.bw.api;

import misat11.bw.api.utils.ColorChanger;
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
    String getBedwarsVersion();

    /**
     * @return Color changer for coloring ItemStacks
     */
    ColorChanger getColorChanger();

    /**
     * @return String of Bedwars API Version
     */
    default String getAPIVersion() {
        return GetConstants.loadConfig().getString("version");
    }

    /**
     * @return Bedwars instance
     */
    static BedwarsAPI getInstance() {
        return Bukkit.getServicesManager().getRegistration(BedwarsAPI.class).getProvider();
    }
}
