package org.screamingsandals.bedwars.api.game;

/**
 * This class stores boolean configuration of game. 
 * 
 * @author ScreamingSandals
 */
public interface GameConfigManager {
	/**
	 * @param key option
	 * @return true if option is enabled
	 */
	public boolean get(ConfigVariables key);
	
	/**
	 * @param key option
	 * @return true if option is changed for this game, false if it's got from config.yml
	 */
	public boolean isChanged(ConfigVariables key);
	
	/**
	 * @param key option
	 * @param value boolean value (true = enabled, false = disabled)
	 * @return true if option was updated, false if update wasn't successful (game is running)
	 */
	public boolean set(ConfigVariables key, boolean value);
	
	/**
	 * @param key option, that will be reset
	 * @return true if option was updated, false if update wasn't successful (game is running)
	 */
	public boolean reset(ConfigVariables key);
	
	/**
	 * @param key option
	 * @return true if option is enabled
	 */
	public boolean get(String key);
	
	/**
	 * @param key option
	 * @return true if option is changed for this game, false if it's got from config.yml
	 */
	public boolean isChanged(String key);
	
	/**
	 * @param key option
	 * @param value boolean value (true = enabled, false = disabled)
	 * @return true if option was updated, false if update wasn't successful (game is running)
	 */
	public boolean set(String key, boolean value);
	
	/**
	 * @param key option, that will be reset
	 * @return true if option was updated, false if update wasn't successful (game is running)
	 */
	public boolean reset(String key);
}
