package misat11.bw.api;

import java.util.ArrayList;
import java.util.List;

import misat11.bw.api.utils.DelayFactory;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import misat11.bw.api.boss.StatusBar;
import misat11.bw.api.special.SpecialItem;


/**
 * @author Bedwars Team
 *
 */
public interface Game {
	/**
	 * @return
	 */
	public String getName();

	/**
	 * @return
	 */
	public GameStatus getStatus();

	/**
	 * 
	 */
	public void start();

	/**
	 * 
	 */
	public void stop();

	/**
	 * @return
	 */
	default boolean isActivated() {
		return getStatus() != GameStatus.DISABLED;
	}

	// PLAYER MANAGEMENT

	/**
	 * @param player
	 */
	public void joinToGame(Player player);

	/**
	 * @param player
	 */
	public void leaveFromGame(Player player);

	/**
	 * @param player
	 * @param team
	 */
	public void selectPlayerTeam(Player player, Team team);

	/**
	 * @param player
	 */
	public void selectPlayerRandomTeam(Player player);

	/**
	 * @return
	 */
	public World getGameWorld();

	/**
	 * @return
	 */
	public Location getPos1();

	/**
	 * @return
	 */
	public Location getPos2();

	/**
	 * @return
	 */
	public Location getSpectatorSpawn();

	/**
	 * @return
	 */
	public int getGameTime();

	/**
	 * @return
	 */
	public int getMinPlayers();

	/**
	 * @return
	 */
	public int getMaxPlayers();

	/**
	 * @return
	 */
	public int countConnectedPlayers();

	/**
	 * @return
	 */
	public List<Player> getConnectedPlayers();

	/**
	 * @return
	 */
	public List<GameStore> getGameStores();

	/**
	 * @return
	 */
	public int countGameStores();

	/**
	 * @return
	 */
	Team getTeamFromName(String name);

	/**
	 * @return
	 */
	public List<Team> getAvailableTeams();

	/**
	 * @return
	 */
	public int countAvailableTeams();

	/**
	 * @return
	 */
	public List<RunningTeam> getRunningTeams();

	/**
	 * @return
	 */
	public int countRunningTeams();

	/**
	 * @param player
	 * @return
	 */
	public RunningTeam getTeamOfPlayer(Player player);

	/**
	 * @param player
	 * @return
	 */
	public boolean isPlayerInAnyTeam(Player player);

	/**
	 * @param player
	 * @param team
	 * @return
	 */
	public boolean isPlayerInTeam(Player player, RunningTeam team);

	/**
	 * @param location
	 * @return
	 */
	public boolean isLocationInArena(Location location);

	/**
	 * @param location
	 * @return
	 */
	public boolean isBlockAddedDuringGame(Location location);

	/**
	 * @return
	 */
	public List<SpecialItem> getActivedSpecialItems();

	/**
	 * @param type
	 * @return
	 */
	public List<SpecialItem> getActivedSpecialItems(Class<? extends SpecialItem> type);

	/**
	 * @param team
	 * @return
	 */
	public List<SpecialItem> getActivedSpecialItemsOfTeam(Team team);

	/**
	 * @param team
	 * @param type
	 * @return
	 */
	public List<SpecialItem> getActivedSpecialItemsOfTeam(Team team, Class<? extends SpecialItem> type);

	/**
	 * @param team
	 * @return
	 */
	public SpecialItem getFirstActivedSpecialItemOfTeam(Team team);

	/**
	 * @param team
	 * @param type
	 * @return
	 */
	public SpecialItem getFirstActivedSpecialItemOfTeam(Team team, Class<? extends SpecialItem> type);

	/**
	 * @param player
	 * @return
	 */
	public List<SpecialItem> getActivedSpecialItemsOfPlayer(Player player);

	/**
	 * @param player
	 * @param type
	 * @return
	 */
	public List<SpecialItem> getActivedSpecialItemsOfPlayer(Player player, Class<? extends SpecialItem> type);

	/**
	 * @param player
	 * @return
	 */
	public SpecialItem getFirstActivedSpecialItemOfPlayer(Player player);

	/**
	 * @param player
	 * @param type
	 * @return
	 */
	public SpecialItem getFirstActivedSpecialItemOfPlayer(Player player, Class<? extends SpecialItem> type);

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
	public void registerSpecialItem(SpecialItem item);

	/**
	 * @param item
	 */
	public void unregisterSpecialItem(SpecialItem item);

	/**
	 * @param item
	 * @return
	 */
	public boolean isRegisteredSpecialItem(SpecialItem item);

	/**
	 * @return
	 */
	public List<ItemSpawner> getItemSpawners();

	/**
	 * @return
	 */
	public Region getRegion();

	/**
	 * @return
	 */
	public StatusBar getStatusBar();

	// LOBBY

	/**
	 * @return
	 */
	public World getLobbyWorld();

	/**
	 * @return
	 */
	public Location getLobbySpawn();

	/**
	 * @return
	 */
	public int getLobbyCountdown();

	/**
	 * @return
	 */
	public int countTeamChests();

	/**
	 * @param team
	 * @return
	 */
	public int countTeamChests(RunningTeam team);

	/**
	 * @param location
	 * @return
	 */
	public RunningTeam getTeamOfChest(Location location);

	/**
	 * @param block
	 * @return
	 */
	public RunningTeam getTeamOfChest(Block block);

	/**
	 * @param entity
	 * @return
	 */
	public boolean isEntityShop(Entity entity);

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getCompassEnabled();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedCompassEnabled();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getJoinRandomTeamAfterLobby();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedJoinRandomTeamAfterLobby();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getJoinRandomTeamOnJoin();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedJoinRandomTeamOnJoin();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getAddWoolToInventoryOnJoin();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedAddWoolToInventoryOnJoin();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getPreventKillingVillagers();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedPreventKillingVillagers();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getSpectatorGm3();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedSpectatorGm3();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getPlayerDrops();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedPlayerDrops();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getFriendlyfire();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedFriendlyfire();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getColoredLeatherByTeamInLobby();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedColoredLeatherByTeamInLobby();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getKeepInventory();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedKeepInventory();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getCrafting();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedCrafting();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getLobbyBossbar();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedLobbyBossbar();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getGameBossbar();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedGameBossbar();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getScoreboard();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedScoreaboard();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getLobbyScoreboard();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedLobbyScoreaboard();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getPreventSpawningMobs();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedPreventSpawningMobs();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getSpawnerHolograms();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedSpawnerHolograms();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getSpawnerDisableMerge();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedSpawnerDisableMerge();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getGameStartItems();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedGameStartItems();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getPlayerRespawnItems();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedPlayerRespawnItems();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getSpawnerHologramsCountdown();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedSpawnerHologramsCountdown();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getDamageWhenPlayerIsNotInArena();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedDamageWhenPlayerIsNotInArena();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getRemoveUnusedTargetBlocks();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedRemoveUnusedTargetBlocks();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getAllowBlockFalling();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedAllowBlockFalling();

	/**
	 * @return
	 */
	public InGameConfigBooleanConstants getHoloAboveBed();

	/**
	 * @return
	 */
	public boolean getOriginalOrInheritedHoloAboveBed();

	/**
	 * @return
	 */
	public boolean getBungeeEnabled();

	/**
	 * @return
	 */
	public ArenaTime getArenaTime();

	/**
	 * @return
	 */
	public WeatherType getArenaWeather();

	/**
	 * @return
	 */
	public BarColor getLobbyBossBarColor();

	/**
	 * @return
	 */
	public BarColor getGameBossBarColor();

	/**
	 * @return
	 */
	boolean isProtectionActive(Player player);

	/**
	 * @return
	 */
	@Deprecated
	public boolean isUpgradesEnabled();


}
