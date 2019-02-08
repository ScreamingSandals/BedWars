package misat11.bw.api;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface BedwarsAPI {

	/**
	 * 
	 * @return List<Game> List of available games
	 */
	public List<Game> getGames();

	/**
	 * 
	 * @param String Name of game
	 * @return boolean Is game is exists
	 */
	public boolean isGameWithNameExists(String name);

	/**
	 * 
	 * @param String Name of game
	 * @return Game Game or null if game is not exists
	 */
	public Game getGameByName(String name);

	/**
	 * 
	 * @param String Name of player
	 * @return Game Player's Game or null if player isn't in game
	 */
	public Game getGameOfPlayer(Player player);

	/**
	 * 
	 * @param String Name of player
	 * @return boolean Is player in any game
	 */
	public boolean isPlayerPlayingAnyGame(Player player);

	/**
	 * 
	 * @return List<ItemSpawnerType> list of existing spawner types
	 */
	public List<ItemSpawnerType> getItemSpawnerTypes();

	/**
	 * 
	 * @param String Name of item spawner type
	 * @return boolean Is spawner type registered
	 */
	public boolean isItemSpawnerTypeRegistered(String name);

	/**
	 * 
	 * @param String Name of item spawner type
	 * @return ItemSpawnerType Type by name or null if type isn't exists
	 */
	public ItemSpawnerType getItemSpawnerTypeByName(String name);

	/**
	 * 
	 * @param Entity entity
	 * @return boolean Is entity in game
	 */
	public boolean isEntityInGame(Entity entity);

	/**
	 * 
	 * @param Entity entity
	 * @return Game game of entity or null
	 */
	public Game getGameOfEntity(Entity entity);

	/**
	 * 
	 * @param Entity entity
	 * @param Game game
	 * @return Nothing.
	 */
	public void registerEntityToGame(Entity entity, Game game);

	/**
	 * 
	 * @param Entity entity
	 * @return Nothing.
	 */
	public void unregisterEntityFromGame(Entity entity);
}
