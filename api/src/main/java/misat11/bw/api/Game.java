package misat11.bw.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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
	
	// LOBBY
	
	public World getLobbyWorld();
	
	public Location getLobbySpawn();
	
	public int getLobbyCountdown();
	
	public int countTeamChests();
	
	public int countTeamChests(RunningTeam team);
	
	public RunningTeam getTeamOfChest(Location location);
	
	public RunningTeam getTeamOfChest(Block block);
	
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
}
