package org.screamingsandals.bedwars.api.game;

import org.screamingsandals.bedwars.api.*;
import org.screamingsandals.bedwars.api.boss.StatusBar;
import org.screamingsandals.bedwars.api.old.Game0xx;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.api.utils.DelayFactory;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author ScreamingSandals
 */
public interface Game extends Game0xx /* must extends old API here for correct compatibility */ {
	/**
	 * @return Arena name
	 */
	String getName();

	/**
	 * @return GameStatus of the arena
	 */
	GameStatus getStatus();

	/**
	 *
	 */
	void start();

	/**
	 *
	 */
	void stop();

	/**
	 * @return true if GameStatus is different than DISABLED
	 */
	default boolean isActivated() {
		return getStatus() != GameStatus.DISABLED;
	}

	// PLAYER MANAGEMENT

	/**
	 * @param player
	 */
	void joinToGame(Player player);

	/**
	 * @param player
	 */
	void leaveFromGame(Player player);

	/**
	 * @param player
	 * @param team
	 */
	void selectPlayerTeam(Player player, Team team);

	/**
	 * @param player
	 */
	void selectPlayerRandomTeam(Player player);

	/**
	 * @return defined world of the game
	 */
	World getGameWorld();

	/**
	 * @return
	 */
	Location getPos1();

	/**
	 * @return
	 */
	Location getPos2();

	/**
	 * @return
	 */
	Location getSpectatorSpawn();

	/**
	 * @return configured time of the game
	 */
	int getGameTime();

	/**
	 * @return configured minimal players to start the game
	 */
	int getMinPlayers();

	/**
	 * @return configured maximal players of the arena
	 */
	int getMaxPlayers();

	/**
	 * @return players in game
	 */
	int countConnectedPlayers();

	/**
	 * @return list of players in game
	 */
	List<Player> getConnectedPlayers();

	/**
	 * @return list of game stores
	 */
	List<GameStore> getGameStores();

	/**
	 * @return
	 */
	int countGameStores();

	/**
	 * @return Team instance from the name
	 */
	Team getTeamFromName(String name);

	/**
	 * @return
	 */
	List<Team> getAvailableTeams();

	/**
	 * @return
	 */
	int countAvailableTeams();

	/**
	 * @return
	 */
	List<RunningTeam> getRunningTeams();

	/**
	 * @return
	 */
	int countRunningTeams();

	/**
	 * @param player
	 * @return
	 */
	RunningTeam getTeamOfPlayer(Player player);

	/**
	 * @param player
	 * @return
	 */
	boolean isPlayerInAnyTeam(Player player);

	/**
	 * @param player
	 * @param team
	 * @return
	 */
	boolean isPlayerInTeam(Player player, RunningTeam team);

	/**
	 * @param location
	 * @return
	 */
	boolean isLocationInArena(Location location);

	/**
	 * @param location
	 * @return
	 */
	boolean isBlockAddedDuringGame(Location location);

	/**
	 * @return
	 */
	List<SpecialItem> getActivedSpecialItems();

	/**
	 * @param type
	 * @return
	 */
	List<SpecialItem> getActivedSpecialItems(Class<? extends SpecialItem> type);

	/**
	 * @param team
	 * @return
	 */
	List<SpecialItem> getActivedSpecialItemsOfTeam(Team team);

	/**
	 * @param team
	 * @param type
	 * @return
	 */
	List<SpecialItem> getActivedSpecialItemsOfTeam(Team team, Class<? extends SpecialItem> type);

	/**
	 * @param team
	 * @return
	 */
	SpecialItem getFirstActivedSpecialItemOfTeam(Team team);

	/**
	 * @param team
	 * @param type
	 * @return
	 */
	SpecialItem getFirstActivedSpecialItemOfTeam(Team team, Class<? extends SpecialItem> type);

	/**
	 * @param player
	 * @return
	 */
	List<SpecialItem> getActivedSpecialItemsOfPlayer(Player player);

	/**
	 * @param player
	 * @param type
	 * @return
	 */
	List<SpecialItem> getActivedSpecialItemsOfPlayer(Player player, Class<? extends SpecialItem> type);

	/**
	 * @param player
	 * @return
	 */
	SpecialItem getFirstActivedSpecialItemOfPlayer(Player player);

	/**
	 * @param player
	 * @param type
	 * @return
	 */
	SpecialItem getFirstActivedSpecialItemOfPlayer(Player player, Class<? extends SpecialItem> type);

	/**
	 * @return
	 */
	List<DelayFactory> getActiveDelays();

	/**
	 * @param player
	 * @return
	 */
	List<DelayFactory> getActiveDelaysOfPlayer(Player player);

	/**
	 * @param player
	 * @param specialItem
	 * @return
	 */
	DelayFactory getActiveDelay(Player player, Class<? extends SpecialItem> specialItem);

	/**
	 * @param delayFactory
	 */
	void registerDelay(DelayFactory delayFactory);

	/**
	 * @param delayFactory
	 */
	void unregisterDelay(DelayFactory delayFactory);

	/**
	 * @param player
	 * @param specialItem
	 * @return
	 */
	boolean isDelayActive(Player player, Class<? extends SpecialItem> specialItem);

	/**
	 * @param item
	 */
	void registerSpecialItem(SpecialItem item);

	/**
	 * @param item
	 */
	void unregisterSpecialItem(SpecialItem item);

	/**
	 * @param item
	 * @return
	 */
	boolean isRegisteredSpecialItem(SpecialItem item);

	/**
	 * @return
	 */
	List<ItemSpawner> getItemSpawners();

	/**
	 * @return
	 */
	Region getRegion();

	/**
	 * @return
	 */
	StatusBar getStatusBar();

	// LOBBY

	/**
	 * @return
	 */
	World getLobbyWorld();

	/**
	 * @return
	 */
	Location getLobbySpawn();

	/**
	 * @return
	 */
	int getLobbyCountdown();

	/**
	 * @return
	 */
	int countTeamChests();

	/**
	 * @param team
	 * @return
	 */
	int countTeamChests(RunningTeam team);

	/**
	 * @param location
	 * @return
	 */
	RunningTeam getTeamOfChest(Location location);

	/**
	 * @param block
	 * @return
	 */
	RunningTeam getTeamOfChest(Block block);

	/**
	 * @param entity
	 * @return
	 */
	boolean isEntityShop(Entity entity);

	/**
	 * @return
	 */
	boolean getBungeeEnabled();

	/**
	 * @return
	 */
	ArenaTime getArenaTime();

	/**
	 * @return
	 */
	WeatherType getArenaWeather();

	/**
	 * @return
	 */
	BarColor getLobbyBossBarColor();

	/**
	 * @return
	 */
	BarColor getGameBossBarColor();

	/**
	 * @return
	 */
	boolean isProtectionActive(Player player);

	/**
	 * @return config manager of this game
	 */
	GameConfigManager getConfigManager();


}
