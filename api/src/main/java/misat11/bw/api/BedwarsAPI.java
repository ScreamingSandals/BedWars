package misat11.bw.api;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface BedwarsAPI {

	/**
	 * 
	 * @return List of available games
	 */
	public List<Game> getGames();

	/**
	 * 
	 * @param name Name of game
	 * @return true if game is exists
	 */
	public boolean isGameWithNameExists(String name);

	/**
	 * 
	 * @param name Name of game
	 * @return Game or null if game is not exists
	 */
	public Game getGameByName(String name);

	/**
	 * 
	 * @param player Player
	 * @return Player's Game or null if player isn't in game
	 */
	public Game getGameOfPlayer(Player player);

	/**
	 * 
	 * @param player Player
	 * @return true if player is in any game
	 */
	public boolean isPlayerPlayingAnyGame(Player player);

	/**
	 * 
	 * @return List of existing spawner types
	 */
	public List<ItemSpawnerType> getItemSpawnerTypes();

	/**
	 * 
	 * @param name Name of item spawner type
	 * @return boolean Is spawner type registered
	 */
	public boolean isItemSpawnerTypeRegistered(String name);

	/**
	 * 
	 * @param name Name of item spawner type
	 * @return ItemSpawnerType by name or null if type isn't exists
	 */
	public ItemSpawnerType getItemSpawnerTypeByName(String name);

	/**
	 * 
	 * @param entity Entity
	 * @return true if entity is in game
	 */
	public boolean isEntityInGame(Entity entity);

	/**
	 * 
	 * @param entity Entity
	 * @return Game of entity or null
	 */
	public Game getGameOfEntity(Entity entity);

	/**
	 * 
	 * @param entity Entity
	 * @param game Game
	 */
	public void registerEntityToGame(Entity entity, Game game);

	/**
	 * 
	 * @param entity Entity
	 */
	public void unregisterEntityFromGame(Entity entity);
	
	/**
	 * 
	 * @return String of Bedwars Version
	 */
	public String getBedwarsVersion();

	/**
	 * 
	 * @return String of Bedwars API Version
	 */
	default String getAPIVersion() {
		return GetConstants.loadConfig().getString("version");
	}
	
	/**
	 * 
	 * @return Bedwars instance
	 */
	public static BedwarsAPI getInstance() {
		return Bukkit.getServicesManager().getRegistration(BedwarsAPI.class).getProvider();
	}
}
