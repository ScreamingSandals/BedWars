package misat11.bw.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import misat11.bw.api.boss.StatusBar;
import misat11.bw.api.special.SpecialItem;

public interface Game {
	public String getName();
	
	public GameStatus getStatus();
	
	// Activate and deactivate arena
	
	public void start();
	
	public void stop();
	
	default boolean isActivated() {
		return getStatus() != GameStatus.DISABLED;
	}
	
	// PLAYER MANAGEMENT
	
	public void joinToGame(Player player);
	
	public void leaveFromGame(Player player);
	
	public void selectPlayerTeam(Player player, Team team);
	
	public void selectPlayerRandomTeam(Player player);
	
	// INGAME
	
	public World getGameWorld();
	
	public Location getPos1();
	
	public Location getPos2();
	
	public Location getSpectatorSpawn();
	
	public int getGameTime();
	
	public int getMinPlayers();
	
	public int getMaxPlayers();
	
	public int countConnectedPlayers();
	
	public List<Player> getConnectedPlayers();
	
	public List<GameStore> getGameStores();
	
	public int countGameStores();
	
	public List<Team> getAvailableTeams();
	
	public int countAvailableTeams();
	
	public List<RunningTeam> getRunningTeams();
	
	public int countRunningTeams();
	
	public RunningTeam getTeamOfPlayer(Player player);
	
	public boolean isPlayerInAnyTeam(Player player);
	
	public boolean isPlayerInTeam(Player player, RunningTeam team);
	
	public boolean isLocationInArena(Location location);
	
	public boolean isBlockAddedDuringGame(Location location);
	
	public List<SpecialItem> getActivedSpecialItems();
	
	public List<SpecialItem> getActivedSpecialItems(Class<? extends SpecialItem> type);

	public List<SpecialItem> getActivedSpecialItemsOfTeam(Team team);

	public List<SpecialItem> getActivedSpecialItemsOfTeam(Team team, Class<? extends SpecialItem> type);
	
	public SpecialItem getFirstActivedSpecialItemOfTeam(Team team);
	
	public SpecialItem getFirstActivedSpecialItemOfTeam(Team team, Class<? extends SpecialItem> type);
	
	public List<SpecialItem> getActivedSpecialItemsOfPlayer(Player player);
	
	public List<SpecialItem> getActivedSpecialItemsOfPlayer(Player player, Class<? extends SpecialItem> type);
	
	public SpecialItem getFirstActivedSpecialItemOfPlayer(Player player);
	
	public SpecialItem getFirstActivedSpecialItemOfPlayer(Player player, Class<? extends SpecialItem> type);
	
	public void registerSpecialItem(SpecialItem item);
	
	public void unregisterSpecialItem(SpecialItem item);
	
	public boolean isRegisteredSpecialItem(SpecialItem item);
	
	public List<ItemSpawner> getItemSpawners();
	
	public Region getRegion();
	
	public StatusBar getStatusBar();
	
	// LOBBY
	
	public World getLobbyWorld();
	
	public Location getLobbySpawn();
	
	public int getLobbyCountdown();
	
	public int countTeamChests();
	
	public int countTeamChests(RunningTeam team);
	
	public RunningTeam getTeamOfChest(Location location);
	
	public RunningTeam getTeamOfChest(Block block);
	
	public boolean isEntityShop(Entity entity);
	
	// BOOLEAN SETTINGS

	public InGameConfigBooleanConstants getCompassEnabled();
	public boolean getOriginalOrInheritedCompassEnabled();
	
	public InGameConfigBooleanConstants getJoinRandomTeamAfterLobby();
	public boolean getOriginalOrInheritedJoinRandomTeamAfterLobby();
	
	public InGameConfigBooleanConstants getJoinRandomTeamOnJoin();
	public boolean getOriginalOrInheritedJoinRandomTeamOnJoin();
	
	public InGameConfigBooleanConstants getAddWoolToInventoryOnJoin();
	public boolean getOriginalOrInheritedAddWoolToInventoryOnJoin();
	
	public InGameConfigBooleanConstants getPreventKillingVillagers();
	public boolean getOriginalOrInheritedPreventKillingVillagers();
	
	public InGameConfigBooleanConstants getSpectatorGm3();
	public boolean getOriginalOrInheritedSpectatorGm3();
	
	public InGameConfigBooleanConstants getPlayerDrops();
	public boolean getOriginalOrInheritedPlayerDrops();
	
	public InGameConfigBooleanConstants getFriendlyfire();
	public boolean getOriginalOrInheritedFriendlyfire();
	
	public InGameConfigBooleanConstants getColoredLeatherByTeamInLobby();
	public boolean getOriginalOrInheritedColoredLeatherByTeamInLobby();
	
	public InGameConfigBooleanConstants getKeepInventory();
	public boolean getOriginalOrInheritedKeepInventory();
	
	public InGameConfigBooleanConstants getCrafting();
	public boolean getOriginalOrInheritedCrafting();
	
	public InGameConfigBooleanConstants getLobbyBossbar();
	public boolean getOriginalOrInheritedLobbyBossbar();
	
	public InGameConfigBooleanConstants getGameBossbar();
	public boolean getOriginalOrInheritedGameBossbar();
	
	public InGameConfigBooleanConstants getScoreboard();
	public boolean getOriginalOrInheritedScoreaboard();
	
	public InGameConfigBooleanConstants getLobbyScoreboard();
	public boolean getOriginalOrInheritedLobbyScoreaboard();
	
	public InGameConfigBooleanConstants getPreventSpawningMobs();
	public boolean getOriginalOrInheritedPreventSpawningMobs();
	
	public InGameConfigBooleanConstants getSpawnerHolograms();
	public boolean getOriginalOrInheritedSpawnerHolograms();
	
	public InGameConfigBooleanConstants getSpawnerDisableMerge();
	public boolean getOriginalOrInheritedSpawnerDisableMerge();
	
	public InGameConfigBooleanConstants getGameStartItems();
	public boolean getOriginalOrInheritedGameStartItems();
	
	public InGameConfigBooleanConstants getPlayerRespawnItems();
	public boolean getOriginalOrInheritedPlayerRespawnItems();
	
	public InGameConfigBooleanConstants getSpawnerHologramsCountdown();
	public boolean getOriginalOrInheritedSpawnerHologramsCountdown();
	
	public InGameConfigBooleanConstants getDamageWhenPlayerIsNotInArena();
	public boolean getOriginalOrInheritedDamageWhenPlayerIsNotInArena();
	
	public InGameConfigBooleanConstants getRemoveUnusedTargetBlocks();
	public boolean getOriginalOrInheritedRemoveUnusedTargetBlocks();
	
	public InGameConfigBooleanConstants getAllowBlockFalling();
	public boolean getOriginalOrInheritedAllowBlockFalling();

	public boolean getBungeeEnabled();
	
	public ArenaTime getArenaTime();
	public WeatherType getArenaWeather();
	public BarColor getLobbyBossBarColor();
	public BarColor getGameBossBarColor();
	
	@Deprecated
	public boolean isUpgradesEnabled();
	
	
}
