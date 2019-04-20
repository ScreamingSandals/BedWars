package misat11.bw.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import misat11.bw.Main;
import misat11.bw.api.ArenaTime;
import misat11.bw.api.GameStatus;
import misat11.bw.api.GameStore;
import misat11.bw.api.InGameConfigBooleanConstants;
import misat11.bw.api.RunningTeam;
import misat11.bw.api.events.BedwarsGameEndEvent;
import misat11.bw.api.events.BedwarsGameStartEvent;
import misat11.bw.api.events.BedwarsGameStartedEvent;
import misat11.bw.api.events.BedwarsPlayerJoinEvent;
import misat11.bw.api.events.BedwarsPlayerJoinTeamEvent;
import misat11.bw.api.events.BedwarsPlayerJoinedEvent;
import misat11.bw.api.events.BedwarsPlayerLeaveEvent;
import misat11.bw.api.events.BedwarsPostRebuildingEvent;
import misat11.bw.api.events.BedwarsPreRebuildingEvent;
import misat11.bw.api.events.BedwarsResourceSpawnEvent;
import misat11.bw.api.events.BedwarsTargetBlockDestroyedEvent;
import misat11.bw.api.special.SpecialItem;
import misat11.bw.legacy.LegacyRegion;
import misat11.bw.statistics.PlayerStatistic;
import misat11.bw.utils.GameSign;
import misat11.bw.utils.IRegion;
import misat11.bw.utils.Region;
import misat11.bw.utils.Sounds;
import misat11.bw.utils.SpawnEffects;
import misat11.bw.utils.TeamSelectorInventory;
import misat11.bw.utils.Title;

import static misat11.lib.lang.I18n.i18n;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Game implements misat11.bw.api.Game {

	private String name;
	private Location pos1;
	private Location pos2;
	private Location lobbySpawn;
	private Location specSpawn;
	private List<Team> teams = new ArrayList<Team>();
	private List<ItemSpawner> spawners = new ArrayList<ItemSpawner>();
	private int pauseCountdown;
	private int gameTime;
	private int minPlayers;
	private List<GamePlayer> players = new ArrayList<GamePlayer>();
	private World world;
	private List<GameStore> gameStore = new ArrayList<GameStore>();
	private ArenaTime arenaTime = ArenaTime.WORLD;
	private WeatherType arenaWeather = null;

	// Boolean settings
	public static final String COMPASS_ENABLED = "compass-enabled";
	private InGameConfigBooleanConstants compassEnabled = InGameConfigBooleanConstants.INHERIT;

	public static final String JOIN_RANDOM_TEAM_AFTER_LOBBY = "join-randomly-after-lobby-timeout";
	private InGameConfigBooleanConstants joinRandomTeamAfterLobby = InGameConfigBooleanConstants.INHERIT;

	public static final String JOIN_RANDOM_TEAM_ON_JOIN = "join-randomly-on-lobby-join";
	private InGameConfigBooleanConstants joinRandomTeamOnJoin = InGameConfigBooleanConstants.INHERIT;

	public static final String ADD_WOOL_TO_INVENTORY_ON_JOIN = "add-wool-to-inventory-on-join";
	private InGameConfigBooleanConstants addWoolToInventoryOnJoin = InGameConfigBooleanConstants.INHERIT;

	public static final String PREVENT_KILLING_VILLAGERS = "prevent-killing-villagers";
	private InGameConfigBooleanConstants preventKillingVillagers = InGameConfigBooleanConstants.INHERIT;

	public static final String SPECTATOR_GM_3 = "spectator-gm3";
	private InGameConfigBooleanConstants spectatorGm3 = InGameConfigBooleanConstants.INHERIT;

	public static final String PLAYER_DROPS = "player-drops";
	private InGameConfigBooleanConstants playerDrops = InGameConfigBooleanConstants.INHERIT;

	public static final String FRIENDLY_FIRE = "friendlyfire";
	private InGameConfigBooleanConstants friendlyfire = InGameConfigBooleanConstants.INHERIT;

	public static final String COLORED_LEATHER_BY_TEAM_IN_LOBBY = "in-lobby-colored-leather-by-team";
	private InGameConfigBooleanConstants coloredLeatherByTeamInLobby = InGameConfigBooleanConstants.INHERIT;

	public static final String KEEP_INVENTORY = "keep-inventory-on-death";
	private InGameConfigBooleanConstants keepInventory = InGameConfigBooleanConstants.INHERIT;

	public static final String CRAFTING = "allow-crafting";
	private InGameConfigBooleanConstants crafting = InGameConfigBooleanConstants.INHERIT;

	public static final String GLOBAL_LOBBY_BOSSBAR = "bossbar.lobby.enable";
	public static final String LOBBY_BOSSBAR = "lobbybossbar";
	private InGameConfigBooleanConstants lobbybossbar = InGameConfigBooleanConstants.INHERIT;

	public static final String GLOBAL_GAME_BOSSBAR = "bossbar.game.enable";
	public static final String GAME_BOSSBAR = "bossbar";
	private InGameConfigBooleanConstants gamebossbar = InGameConfigBooleanConstants.INHERIT;

	public static final String GLOBAL_SCOREBOARD = "scoreboard.enable";
	public static final String SCOREBOARD = "scoreboard";
	private InGameConfigBooleanConstants ascoreboard = InGameConfigBooleanConstants.INHERIT;

	public static final String GLOBAL_LOBBY_SCOREBOARD = "lobby-scoreboard.enabled";
	public static final String LOBBY_SCOREBOARD = "lobbyscoreboard";
	private InGameConfigBooleanConstants lobbyscoreboard = InGameConfigBooleanConstants.INHERIT;
	
	public static final String PREVENT_SPAWNING_MOBS = "prevent-spawning-mobs";
	private InGameConfigBooleanConstants preventSpawningMobs = InGameConfigBooleanConstants.INHERIT;

	// STATUS
	private GameStatus status = GameStatus.DISABLED;
	private GameStatus afterRebuild = GameStatus.WAITING;
	private int countdown = -1;
	private int calculatedMaxPlayers;
	private BukkitTask task;
	private List<CurrentTeam> teamsInGame = new ArrayList<CurrentTeam>();
	private IRegion region = Main.isLegacy() ? new LegacyRegion() : new Region();
	private TeamSelectorInventory teamSelectorInventory;
	private Scoreboard gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	private BossBar bossbar;
	private List<Location> usedChests = new ArrayList<Location>();
	private List<SpecialItem> activeSpecialItems = new ArrayList<SpecialItem>();

	private Game() {

	}

	public String getName() {
		return name;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		if (this.world == null) {
			this.world = world;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getPos1() {
		return pos1;
	}

	public void setPos1(Location pos1) {
		this.pos1 = pos1;
	}

	public Location getPos2() {
		return pos2;
	}

	public void setPos2(Location pos2) {
		this.pos2 = pos2;
	}

	public Location getLobbySpawn() {
		return lobbySpawn;
	}

	public void setLobbySpawn(Location lobbySpawn) {
		this.lobbySpawn = lobbySpawn;
	}

	public int getPauseCountdown() {
		return pauseCountdown;
	}

	public void setPauseCountdown(int pauseCountdown) {
		this.pauseCountdown = pauseCountdown;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	public int countPlayers() {
		return this.players.size();
	}

	public List<GameStore> getGameStores() {
		return gameStore;
	}

	public Location getSpecSpawn() {
		return specSpawn;
	}

	public void setSpecSpawn(Location specSpawn) {
		this.specSpawn = specSpawn;
	}

	public int getGameTime() {
		return gameTime;
	}

	public void setGameTime(int gameTime) {
		this.gameTime = gameTime;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public List<ItemSpawner> getSpawners() {
		return spawners;
	}

	public void setGameStores(List<GameStore> gameStore) {
		this.gameStore = gameStore;
	}

	public TeamSelectorInventory getTeamSelectorInventory() {
		return teamSelectorInventory;
	}

	public boolean isBlockAddedDuringGame(Location loc) {
		return status == GameStatus.RUNNING && region.isBlockAddedDuringGame(loc);
	}

	public boolean blockPlace(GamePlayer player, Block block, BlockState replaced) {
		if (status != GameStatus.RUNNING) {
			return false; // ?
		}
		if (player.isSpectator) {
			return false;
		}
		if (Main.isFarmBlock(block.getType())) {
			return true;
		}
		if (!GameCreator.isInArea(block.getLocation(), pos1, pos2)) {
			return false;
		}
		if (replaced.getType() != Material.AIR) {
			if (region.isLiquid(replaced.getType())) {
				region.putOriginalBlock(block.getLocation(), replaced);
			} else {
				return false;
			}
		}
		region.addBuildedDuringGame(block.getLocation());

		if (block.getType() == Material.ENDER_CHEST) {
			CurrentTeam team = getPlayerTeam(player);
			team.addTeamChest(block);
			String message = i18n("team_chest_placed");
			for (GamePlayer gp : team.players) {
				gp.player.sendMessage(message);
			}
		}
		return true;
	}

	public boolean blockBreak(GamePlayer player, Block block, BlockBreakEvent event) {
		if (status != GameStatus.RUNNING) {
			return false; // ?
		}
		if (player.isSpectator) {
			return false;
		}
		if (Main.isFarmBlock(block.getType())) {
			return true;
		}
		if (!GameCreator.isInArea(block.getLocation(), pos1, pos2)) {
			return false;
		}
		if (region.isBlockAddedDuringGame(block.getLocation())) {
			region.removeBlockBuildedDuringGame(block.getLocation());

			if (block.getType() == Material.ENDER_CHEST) {
				CurrentTeam team = getTeamOfChest(block);
				if (team != null) {
					team.removeTeamChest(block);
					String message = i18n("team_chest_broken");
					for (GamePlayer gp : team.players) {
						gp.player.sendMessage(message);
					}
				}
			}

			return true;
		}
		if (region.isBedBlock(block.getState())) {
			Location loc = block.getLocation();
			if (!region.isBedHead(block.getState())) {
				loc = region.getBedNeighbor(block).getLocation();
			}
			if (getPlayerTeam(player).teamInfo.bed.equals(loc)) {
				return false;
			}
			bedDestroyed(loc, player.player);
			region.putOriginalBlock(block.getLocation(), block.getState());
			if (block.getLocation().equals(loc)) {
				Block neighbor = region.getBedNeighbor(block);
				region.putOriginalBlock(neighbor.getLocation(), neighbor.getState());
			} else {
				region.putOriginalBlock(loc, region.getBedNeighbor(block).getState());
			}
			try {
				event.setDropItems(false);
			} catch (Throwable tr) {
				if (region.isBedHead(block.getState())) {
					region.getBedNeighbor(block).setType(Material.AIR);
				} else {
					block.setType(Material.AIR);
				}
			}
			return true;
		}
		return false;
	}

	public IRegion getRegion() {
		return region;
	}

	public CurrentTeam getPlayerTeam(GamePlayer player) {
		for (CurrentTeam team : teamsInGame) {
			if (team.players.contains(player)) {
				return team;
			}
		}
		return null;
	}

	protected void bedDestroyed(Location loc, Player broker) {
		if (status == GameStatus.RUNNING) {
			for (CurrentTeam team : teamsInGame) {
				if (team.teamInfo.bed.equals(loc)) {
					team.isBed = false;
					updateScoreboard();
					for (GamePlayer player : players) {
						Title.send(player.player,
								i18n("bed_is_destroyed", false).replace("%team%",
										team.teamInfo.color.chatColor + team.teamInfo.name),
								i18n("bed_is_destroyed_subtitle", false));
						player.player.sendMessage(i18n("bed_is_destroyed").replace("%team%",
								team.teamInfo.color.chatColor + team.teamInfo.name));
						SpawnEffects.spawnEffect(this, player.player, "game-effects.beddestroy");
						Sounds.playSound(player.player, player.player.getLocation(),
								Main.getConfigurator().config.getString("sounds.on_bed_destroyed"),
								Sounds.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
					}
					BedwarsTargetBlockDestroyedEvent targetBlockDestroyed = new BedwarsTargetBlockDestroyedEvent(this,
							broker, team);
					Main.getInstance().getServer().getPluginManager().callEvent(targetBlockDestroyed);

					if (Main.isPlayerStatisticsEnabled()) {
						PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(broker);
						statistic.setCurrentDestroyedBeds(statistic.getCurrentDestroyedBeds() + 1);
						statistic.setCurrentScore(statistic.getCurrentScore()
								+ Main.getConfigurator().config.getInt("statistics.scores.bed-destroy", 25));
					}
				}
			}
		}
	}

	public void joinPlayer(GamePlayer player) {
		if (status == GameStatus.DISABLED) {
			player.changeGame(null);
			return;
		}
		if (status == GameStatus.REBUILDING) {
			player.player.sendMessage(i18n("game_is_rebuilding").replace("%arena%", this.name));
			player.changeGame(null);
			return;
		}
		if (status == GameStatus.RUNNING) {
			player.player.sendMessage(i18n("game_already_running").replace("%arena%", this.name));
			player.changeGame(null);
			return;
		}
		if (players.size() >= calculatedMaxPlayers) {
			player.player.sendMessage(i18n("game_is_full").replace("%arena%", this.name));
			player.changeGame(null);
			return;
		}

		BedwarsPlayerJoinEvent joinEvent = new BedwarsPlayerJoinEvent(this, player.player);
		Main.getInstance().getServer().getPluginManager().callEvent(joinEvent);

		if (joinEvent.isCancelled()) {
			String message = joinEvent.getCancelMessage();
			if (message != null && !message.equals("")) {
				player.player.sendMessage(message);
			}
			player.changeGame(null);
			return;
		}

		boolean isEmpty = players.isEmpty();
		if (!players.contains(player)) {
			players.add(player);
		}
		updateSigns();

		if (Main.isPlayerStatisticsEnabled()) {
			// Load
			Main.getPlayerStatisticsManager().getStatistic(player.player);
		}
		
		if (arenaTime.time >= 0) {
			player.player.setPlayerTime(arenaTime.time, false);
		}
		
		if (arenaWeather != null) {
			player.player.setPlayerWeather(arenaWeather);
		}

		player.player.teleport(lobbySpawn);
		SpawnEffects.spawnEffect(this, player.player, "game-effects.lobbyjoin");
		String message = i18n("join").replace("%name%", player.player.getDisplayName())
				.replace("%players%", Integer.toString(players.size()))
				.replaceAll("%maxplayers%", Integer.toString(calculatedMaxPlayers));
		for (GamePlayer p : players)
			p.player.sendMessage(message);

		if (getOriginalOrInheritedJoinRandomTeamOnJoin()) {
			joinRandomTeam(player);
		}

		if (getOriginalOrInheritedCompassEnabled()) {
			ItemStack compass = Main.getConfigurator().readDefinedItem("jointeam", "COMPASS");
			ItemMeta metaCompass = compass.getItemMeta();
			metaCompass.setDisplayName(i18n("compass_selector_team", false));
			compass.setItemMeta(metaCompass);
			player.player.getInventory().setItem(0, compass);
		}

		ItemStack leave = Main.getConfigurator().readDefinedItem("leavegame", "SLIME_BALL");
		ItemMeta leaveMeta = leave.getItemMeta();
		leaveMeta.setDisplayName(i18n("leave_from_game_item", false));
		leave.setItemMeta(leaveMeta);
		player.player.getInventory().setItem(8, leave);

		if (isEmpty) {
			runTask();
		} else {
			try {
				bossbar.addPlayer(player.player);
			} catch (Throwable tr) {

			}
		}

		BedwarsPlayerJoinedEvent joinedEvent = new BedwarsPlayerJoinedEvent(this, getPlayerTeam(player), player.player);
		Main.getInstance().getServer().getPluginManager().callEvent(joinedEvent);
	}

	public void leavePlayer(GamePlayer player) {
		if (status == GameStatus.DISABLED) {
			return;
		}

		BedwarsPlayerLeaveEvent event = new BedwarsPlayerLeaveEvent(this, player.player, getPlayerTeam(player));
		Main.getInstance().getServer().getPluginManager().callEvent(event);

		if (players.contains(player)) {
			players.remove(player);
		}
		updateSigns();

		if (status == GameStatus.WAITING) {
			SpawnEffects.spawnEffect(this, player.player, "game-effects.lobbyleave");
		}

		String message = i18n("leave").replace("%name%", player.player.getDisplayName())
				.replace("%players%", Integer.toString(players.size()))
				.replaceAll("%maxplayers%", Integer.toString(calculatedMaxPlayers));
		try {
			bossbar.removePlayer(player.player);
		} catch (Throwable tr) {

		}
		player.player.sendMessage(message);
		player.player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		if (status == GameStatus.RUNNING || status == GameStatus.WAITING) {
			CurrentTeam team = getPlayerTeam(player);
			if (team != null) {
				team.players.remove(player);
				if (status == GameStatus.WAITING) {
					team.getScoreboardTeam().removeEntry(player.player.getName());
					if (team.players.isEmpty()) {
						teamsInGame.remove(team);
						team.getScoreboardTeam().unregister();
					}
				} else {
					updateScoreboard();
				}
			}
		}
		for (GamePlayer p : players) {
			p.player.sendMessage(message);
		}

		if (Main.isPlayerStatisticsEnabled()) {
			PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(player.player);
			Main.getPlayerStatisticsManager().storeStatistic(statistic);

			Main.getPlayerStatisticsManager().unloadStatistic(player.player);
		}

		if (players.isEmpty()) {
			if (status == GameStatus.RUNNING) {
				status = GameStatus.REBUILDING;
				afterRebuild = GameStatus.WAITING;
				updateSigns();
			} else {
				status = GameStatus.WAITING;
				cancelTask();
			}
			countdown = -1;
			if (gameScoreboard.getObjective("display") != null) {
				gameScoreboard.getObjective("display").unregister();
			}
			if (gameScoreboard.getObjective("lobby") != null) {
				gameScoreboard.getObjective("lobby").unregister();
			}
			gameScoreboard.clearSlot(DisplaySlot.SIDEBAR);
			for (CurrentTeam team : teamsInGame) {
				team.getScoreboardTeam().unregister();
			}
			teamsInGame.clear();
			for (GameStore store : gameStore) {
				Villager villager = store.kill();
				if (villager != null) {
					Main.unregisterGameEntity(villager);
				}
			}
		}
	}

	public static Game loadGame(File file) {
		if (!file.exists()) {
			return null;
		}
		FileConfiguration configMap = new YamlConfiguration();
		try {
			configMap.load(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			return null;
		}

		Game game = new Game();
		game.name = configMap.getString("name");
		game.pauseCountdown = configMap.getInt("pauseCountdown");
		game.gameTime = configMap.getInt("gameTime");
		game.world = Bukkit.getWorld(configMap.getString("world"));
		game.pos1 = readLocationFromString(game.world, configMap.getString("pos1"));
		game.pos2 = readLocationFromString(game.world, configMap.getString("pos2"));
		game.specSpawn = readLocationFromString(game.world, configMap.getString("specSpawn"));
		game.lobbySpawn = readLocationFromString(Bukkit.getWorld(configMap.getString("lobbySpawnWorld")),
				configMap.getString("lobbySpawn"));
		game.minPlayers = configMap.getInt("minPlayers", 2);
		if (configMap.isSet("spawners")) {
			List<Map<String, String>> spawners = (List<Map<String, String>>) configMap.getList("spawners");
			for (Map<String, String> spawner : spawners) {
				ItemSpawner sa = new ItemSpawner(readLocationFromString(game.world, spawner.get("location")),
						Main.getSpawnerType(spawner.get("type").toLowerCase()));
				game.spawners.add(sa);
			}
		}
		if (configMap.isSet("teams")) {
			for (String teamN : configMap.getConfigurationSection("teams").getKeys(false)) {
				ConfigurationSection team = configMap.getConfigurationSection("teams").getConfigurationSection(teamN);
				Team t = new Team();
				t.color = TeamColor.valueOf(team.getString("color"));
				t.name = teamN;
				t.bed = readLocationFromString(game.world, team.getString("bed"));
				t.maxPlayers = team.getInt("maxPlayers");
				t.spawn = readLocationFromString(game.world, team.getString("spawn"));
				t.game = game;
				game.teams.add(t);
			}
		}
		if (configMap.isSet("stores")) {
			List<String> stores = (List<String>) configMap.getList("stores");
			for (String store : stores) {
				game.gameStore.add(new GameStore(readLocationFromString(game.world, store)));
			}
		}

		game.compassEnabled = readBooleanConstant(configMap.getString("constant." + COMPASS_ENABLED, "inherit"));
		game.addWoolToInventoryOnJoin = readBooleanConstant(
				configMap.getString("constant." + ADD_WOOL_TO_INVENTORY_ON_JOIN, "inherit"));
		game.coloredLeatherByTeamInLobby = readBooleanConstant(
				configMap.getString("constant." + COLORED_LEATHER_BY_TEAM_IN_LOBBY, "inherit"));
		game.crafting = readBooleanConstant(configMap.getString("constant." + CRAFTING, "inherit"));
		game.friendlyfire = readBooleanConstant(configMap.getString("constant." + FRIENDLY_FIRE, "inherit"));
		game.joinRandomTeamAfterLobby = readBooleanConstant(
				configMap.getString("constant." + JOIN_RANDOM_TEAM_AFTER_LOBBY, "inherit"));
		game.joinRandomTeamOnJoin = readBooleanConstant(
				configMap.getString("constant." + JOIN_RANDOM_TEAM_ON_JOIN, "inherit"));
		game.keepInventory = readBooleanConstant(configMap.getString("constant." + KEEP_INVENTORY, "inherit"));
		game.preventKillingVillagers = readBooleanConstant(
				configMap.getString("constant." + PREVENT_KILLING_VILLAGERS, "inherit"));
		game.spectatorGm3 = readBooleanConstant(configMap.getString("constant." + SPECTATOR_GM_3, "inherit"));
		game.playerDrops = readBooleanConstant(configMap.getString("constant." + PLAYER_DROPS, "inherit"));
		game.lobbybossbar = readBooleanConstant(configMap.getString("constant." + LOBBY_BOSSBAR, "inherit"));
		game.gamebossbar = readBooleanConstant(configMap.getString("constant." + GAME_BOSSBAR, "inherit"));
		game.ascoreboard = readBooleanConstant(configMap.getString("constant." + SCOREBOARD, "inherit"));
		game.lobbyscoreboard = readBooleanConstant(configMap.getString("constant." + LOBBY_SCOREBOARD, "inherit"));
		game.preventSpawningMobs = readBooleanConstant(configMap.getString("constant." + PREVENT_SPAWNING_MOBS, "inherit"));
		
		game.arenaTime = ArenaTime.valueOf(configMap.getString("arenaTime", ArenaTime.WORLD.name()).toUpperCase());
		game.arenaWeather = loadWeather(configMap.getString("arenaWeather", "default").toUpperCase());

		game.start();
		Main.getInstance().getLogger().info("Arena " + game.name + " loaded!");
		Main.addGame(game);
		return game;
	}
	
	public static WeatherType loadWeather(String weather) {
		try {
			return WeatherType.valueOf(weather);
		} catch (Exception e) {
			return null;
		}
	}

	public static Location readLocationFromString(World world, String location) {
		int lpos = 0;
		double x = 0;
		double y = 0;
		double z = 0;
		float yaw = 0;
		float pitch = 0;
		for (String pos : location.split(";")) {
			lpos++;
			switch (lpos) {
			case 1:
				x = Double.parseDouble(pos);
				break;
			case 2:
				y = Double.parseDouble(pos);
				break;
			case 3:
				z = Double.parseDouble(pos);
				break;
			case 4:
				yaw = Float.parseFloat(pos);
				break;
			case 5:
				pitch = Float.parseFloat(pos);
				break;
			default:
				break;
			}
		}
		return new Location(world, x, y, z, yaw, pitch);
	}

	public static String setLocationToString(Location location) {
		return location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";"
				+ location.getPitch();
	}

	public static InGameConfigBooleanConstants readBooleanConstant(String s) {
		if ("true".equalsIgnoreCase(s)) {
			return InGameConfigBooleanConstants.TRUE;
		} else if ("false".equalsIgnoreCase(s)) {
			return InGameConfigBooleanConstants.FALSE;
		}

		return InGameConfigBooleanConstants.INHERIT;
	}

	public static String writeBooleanConstant(InGameConfigBooleanConstants constant) {
		switch (constant) {
		case TRUE:
			return "true";
		case FALSE:
			return "false";
		case INHERIT:
		default:
			return "inherit";
		}
	}

	public void saveToConfig() {
		File dir = new File(Main.getInstance().getDataFolder(), "arenas");
		if (!dir.exists())
			dir.mkdir();
		File file = new File(dir, name + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileConfiguration configMap = new YamlConfiguration();
		configMap.set("name", name);
		configMap.set("pauseCountdown", pauseCountdown);
		configMap.set("gameTime", gameTime);
		configMap.set("world", world.getName());
		configMap.set("pos1", setLocationToString(pos1));
		configMap.set("pos2", setLocationToString(pos2));
		configMap.set("specSpawn", setLocationToString(specSpawn));
		configMap.set("lobbySpawn", setLocationToString(lobbySpawn));
		configMap.set("lobbySpawnWorld", lobbySpawn.getWorld().getName());
		configMap.set("minPlayers", minPlayers);
		List<Map<String, String>> nS = new ArrayList<Map<String, String>>();
		for (ItemSpawner spawner : spawners) {
			Map<String, String> spawnerMap = new HashMap<String, String>();
			spawnerMap.put("location", setLocationToString(spawner.loc));
			spawnerMap.put("type", spawner.type.getConfigKey());
			nS.add(spawnerMap);
		}
		configMap.set("spawners", nS);
		if (!teams.isEmpty()) {
			for (Team t : teams) {
				configMap.set("teams." + t.name + ".color", t.color.name());
				configMap.set("teams." + t.name + ".maxPlayers", t.maxPlayers);
				configMap.set("teams." + t.name + ".bed", setLocationToString(t.bed));
				configMap.set("teams." + t.name + ".spawn", setLocationToString(t.spawn));
			}
		}
		if (!gameStore.isEmpty()) {
			List<String> nL = new ArrayList<String>();
			for (GameStore store : gameStore) {
				nL.add(setLocationToString(store.getStoreLocation()));
			}
			configMap.set("stores", nL);
		}

		configMap.set("constant." + COMPASS_ENABLED, writeBooleanConstant(compassEnabled));
		configMap.set("constant." + ADD_WOOL_TO_INVENTORY_ON_JOIN, writeBooleanConstant(addWoolToInventoryOnJoin));
		configMap.set("constant." + COLORED_LEATHER_BY_TEAM_IN_LOBBY,
				writeBooleanConstant(coloredLeatherByTeamInLobby));
		configMap.set("constant." + CRAFTING, writeBooleanConstant(crafting));
		configMap.set("constant." + JOIN_RANDOM_TEAM_AFTER_LOBBY, writeBooleanConstant(joinRandomTeamAfterLobby));
		configMap.set("constant." + JOIN_RANDOM_TEAM_ON_JOIN, writeBooleanConstant(joinRandomTeamOnJoin));
		configMap.set("constant." + KEEP_INVENTORY, writeBooleanConstant(keepInventory));
		configMap.set("constant." + PREVENT_KILLING_VILLAGERS, writeBooleanConstant(preventKillingVillagers));
		configMap.set("constant." + SPECTATOR_GM_3, writeBooleanConstant(spectatorGm3));
		configMap.set("constant." + PLAYER_DROPS, writeBooleanConstant(playerDrops));
		configMap.set("constant." + FRIENDLY_FIRE, writeBooleanConstant(friendlyfire));
		configMap.set("constant." + LOBBY_BOSSBAR, writeBooleanConstant(lobbybossbar));
		configMap.set("constant." + GAME_BOSSBAR, writeBooleanConstant(gamebossbar));
		configMap.set("constant." + LOBBY_SCOREBOARD, writeBooleanConstant(lobbyscoreboard));
		configMap.set("constant." + SCOREBOARD, writeBooleanConstant(ascoreboard));
		configMap.set("constant." + PREVENT_SPAWNING_MOBS, writeBooleanConstant(preventSpawningMobs));
		
		configMap.set("arenaTime", arenaTime.name());
		configMap.set("arenaWeather", arenaWeather == null ? "default" : arenaWeather.name());

		try {
			configMap.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Game createGame(String name) {
		Game game = new Game();
		game.name = name;
		game.pauseCountdown = 60;
		game.gameTime = 3600;
		game.minPlayers = 2;

		return game;
	}

	public void start() {
		if (status == GameStatus.DISABLED) {
			status = GameStatus.WAITING;
			countdown = -1;
			calculatedMaxPlayers = 0;
			for (Team team : teams) {
				calculatedMaxPlayers += team.maxPlayers;
			}
			new BukkitRunnable() {
				public void run() {
					updateSigns();
				}
			}.runTask(Main.getInstance());
		}
	}

	public void stop() {
		List<GamePlayer> clonedPlayers = (List<GamePlayer>) ((ArrayList<GamePlayer>) players).clone();
		for (GamePlayer p : clonedPlayers)
			p.changeGame(null);
		if (status != GameStatus.REBUILDING) {
			status = GameStatus.DISABLED;
			updateSigns();
		} else {
			afterRebuild = GameStatus.DISABLED;
		}
	}

	public void joinToGame(Player player) {
		if (status == GameStatus.DISABLED) {
			return;
		}
		if (status == GameStatus.REBUILDING) {
			player.sendMessage(i18n("game_is_rebuilding").replace("%arena%", this.name));
			return;
		}
		if (status == GameStatus.RUNNING) {
			player.sendMessage(i18n("game_already_running").replace("%arena%", this.name));
			return;
		}
		if (players.size() >= calculatedMaxPlayers) {
			player.sendMessage(i18n("game_is_full").replace("%arena%", this.name));
			return;
		}
		GamePlayer gPlayer = Main.getPlayerGameProfile(player);
		gPlayer.changeGame(this);
	}

	public void leaveFromGame(Player player) {
		if (status == GameStatus.DISABLED) {
			return;
		}
		if (Main.isPlayerInGame(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);

			if (gPlayer.getGame() == this) {
				gPlayer.changeGame(null);
				if (status == GameStatus.RUNNING) {
					updateScoreboard();
				}
			}
		}
	}

	public CurrentTeam getCurrentTeamByTeam(Team team) {
		for (CurrentTeam current : teamsInGame) {
			if (current.teamInfo == team) {
				return current;
			}
		}
		return null;
	}

	public Team getFirstTeamThatIsntInGame() {
		for (Team team : teams) {
			if (getCurrentTeamByTeam(team) == null) {
				return team;
			}
		}
		return null;
	}

	public CurrentTeam getTeamWithLowestPlayers() {
		CurrentTeam lowest = null;

		for (CurrentTeam team : teamsInGame) {
			if (lowest == null) {
				lowest = team;
			}

			if (lowest.players.size() > team.players.size()) {
				lowest = team;
			}
		}

		return lowest;
	}

	public void joinRandomTeam(GamePlayer player) {
		Team teamForJoin;
		if (teamsInGame.size() < 2) {
			teamForJoin = getFirstTeamThatIsntInGame();
		} else {
			CurrentTeam current = getTeamWithLowestPlayers();
			if (current.players.size() >= current.getMaxPlayers()) {
				teamForJoin = getFirstTeamThatIsntInGame();
			} else {
				teamForJoin = current.teamInfo;
			}
		}

		if (teamForJoin == null) {
			return;
		}

		CurrentTeam current = null;
		for (CurrentTeam t : teamsInGame) {
			if (t.teamInfo == teamForJoin) {
				current = t;
				break;
			}
		}
		CurrentTeam cur = getPlayerTeam(player);

		BedwarsPlayerJoinTeamEvent event = new BedwarsPlayerJoinTeamEvent(current, player.player, cur);
		Main.getInstance().getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		if (current == null) {
			current = new CurrentTeam(teamForJoin, this);
			org.bukkit.scoreboard.Team scoreboardTeam = gameScoreboard.getTeam(teamForJoin.name);
			if (scoreboardTeam == null) {
				scoreboardTeam = gameScoreboard.registerNewTeam(teamForJoin.name);
			}
			if (!Main.isLegacy()) {
				scoreboardTeam.setColor(teamForJoin.color.chatColor);
			} else {
				scoreboardTeam.setPrefix(teamForJoin.color.chatColor.toString());
			}
			scoreboardTeam.setAllowFriendlyFire(getOriginalOrInheritedFriendlyfire());

			current.setScoreboardTeam(scoreboardTeam);
		}
		if (cur == current) {
			player.player.sendMessage(
					i18n("team_already_selected").replace("%team%", teamForJoin.color.chatColor + teamForJoin.name)
							.replace("%players%", Integer.toString(current.players.size()))
							.replaceAll("%maxplayers%", Integer.toString(current.teamInfo.maxPlayers)));
			return;
		}
		if (current.players.size() >= current.teamInfo.maxPlayers) {
			if (cur != null) {
				player.player.sendMessage(i18n("team_is_full_you_are_staying")
						.replace("%team%", teamForJoin.color.chatColor + teamForJoin.name)
						.replace("%oldteam%", cur.teamInfo.color.chatColor + cur.teamInfo.name));
			} else {
				player.player.sendMessage(
						i18n("team_is_full").replace("%team%", teamForJoin.color.chatColor + teamForJoin.name));
			}
			return;
		}
		if (cur != null) {
			cur.players.remove(player);
			cur.getScoreboardTeam().removeEntry(player.player.getName());
			if (cur.players.isEmpty()) {
				teamsInGame.remove(cur);
				cur.getScoreboardTeam().unregister();
			}
		}
		current.players.add(player);
		current.getScoreboardTeam().addEntry(player.player.getName());
		player.player
				.sendMessage(i18n("team_selected").replace("%team%", teamForJoin.color.chatColor + teamForJoin.name)
						.replace("%players%", Integer.toString(current.players.size()))
						.replaceAll("%maxplayers%", Integer.toString(current.teamInfo.maxPlayers)));

		if (getOriginalOrInheritedAddWoolToInventoryOnJoin()) {
			ItemStack stack = TeamSelectorInventory.materializeColorToWool(teamForJoin.color);
			ItemMeta stackMeta = stack.getItemMeta();
			stackMeta.setDisplayName(teamForJoin.color.chatColor + teamForJoin.name);
			stack.setItemMeta(stackMeta);
			player.player.getInventory().setItem(1, stack);
		}

		if (getOriginalOrInheritedColoredLeatherByTeamInLobby()) {
			ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
			meta.setColor(teamForJoin.color.leatherColor);
			chestplate.setItemMeta(meta);
			player.player.getInventory().setChestplate(chestplate);
		}

		if (!teamsInGame.contains(current)) {
			teamsInGame.add(current);
		}
	}

	public void makeSpectator(GamePlayer player) {
		player.isSpectator = true;
		player.player.teleport(specSpawn);
		player.player.setAllowFlight(true);
		player.player.setFlying(true);
		if (getOriginalOrInheritedSpectatorGm3()) {
			player.player.setGameMode(GameMode.SPECTATOR);
		} else {
			player.player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
		}

		ItemStack leave = Main.getConfigurator().readDefinedItem("leavegame", "SLIME_BALL");
		ItemMeta leaveMeta = leave.getItemMeta();
		leaveMeta.setDisplayName(i18n("leave_from_game_item", false));
		leave.setItemMeta(leaveMeta);
		player.player.getInventory().setItem(8, leave);

	}

	public void run() {
		if (this.status == GameStatus.RUNNING) {
			updateScoreboardTimer();
			if (countdown == 0) {
				teamsInGame.clear();
				activeSpecialItems.clear();
				for (GameStore store : gameStore) {
					Villager villager = store.kill();
					if (villager != null) {
						Main.unregisterGameEntity(villager);
					}
				}
				String message = i18n("game_end");
				for (GamePlayer player : (List<GamePlayer>) ((ArrayList<GamePlayer>) players).clone()) {
					player.player.sendMessage(message);
					player.changeGame(null);
				}

				BedwarsGameEndEvent event = new BedwarsGameEndEvent(this);
				Main.getInstance().getServer().getPluginManager().callEvent(event);
				return;
			}
			int runningTeams = 0;
			for (CurrentTeam t : teamsInGame) {
				runningTeams += t.isAlive() ? 1 : 0;
			}
			if (runningTeams <= 1) {
				if (runningTeams == 1) {
					for (CurrentTeam t : teamsInGame) {
						if (t.isAlive()) {
							String time = getFormattedTimeLeft(this.gameTime - this.countdown);
							String message = i18n("team_win", true)
									.replace("%team%", t.teamInfo.color.chatColor + t.teamInfo.name)
									.replace("%time%", time);
							String subtitle = i18n("team_win", false)
									.replace("%team%", t.teamInfo.color.chatColor + t.teamInfo.name)
									.replace("%time%", time);
							boolean madeRecord = false; // TODO
							for (GamePlayer player : players) {
								player.player.sendMessage(message);
								if (getPlayerTeam(player) == t) {
									Title.send(player.player, i18n("you_won", false), subtitle);
									Main.depositPlayer(player.player, Main.getVaultWinReward());

									SpawnEffects.spawnEffect(this, player.player, "game-effects.end");

									if (Main.isPlayerStatisticsEnabled()) {
										PlayerStatistic statistic = Main.getPlayerStatisticsManager()
												.getStatistic(player.player);
										statistic.setCurrentWins(statistic.getCurrentWins() + 1);
										statistic.setCurrentScore(statistic.getCurrentScore()
												+ Main.getConfigurator().config.getInt("statistics.scores.win", 50));

										if (madeRecord) {
											statistic.setCurrentScore(
													statistic.getCurrentScore() + Main.getConfigurator().config
															.getInt("statistics.scores.record", 100));
										}
										
									    if (Main.isHologramsEnabled()) {
									          Main.getHologramInteraction().updateHolograms(player.player);
									        }

										if (Main.getConfigurator().config.getBoolean("statistics.show-on-game-end")) {
											Main.getInstance().getServer().dispatchCommand(player.player, "bw stats");
										}

									}
								} else {
									Title.send(player.player, i18n("you_lost", false), subtitle);
								}
							}
							break;
						}
					}
				}
				countdown = 0;
			} else {
				try {
					bossbar.setProgress((double) countdown / (double) gameTime);
				} catch (Throwable t) {

				}
				countdown--;
				for (ItemSpawner spawner : spawners) {
					ItemSpawnerType type = spawner.type;
					int cycle = type.getInterval();
					if ((countdown % cycle) == 0) {

						BedwarsResourceSpawnEvent resourceSpawnEvent = new BedwarsResourceSpawnEvent(this, spawner.loc,
								type, type.getStack());
						Main.getInstance().getServer().getPluginManager().callEvent(resourceSpawnEvent);

						if (resourceSpawnEvent.isCancelled()) {
							return;
						}

						Location loc = spawner.loc.clone().add(0, 1, 0);
						Item item = loc.getWorld().dropItem(loc, resourceSpawnEvent.getResource());
						item.setPickupDelay(0);
					}
				}
			}
		} else if (this.status == GameStatus.WAITING) {
			if (countdown == -1) {
				countdown = pauseCountdown;
				String title = i18n("bossbar_waiting", false);
				try {
					bossbar = Bukkit.createBossBar(title, BarColor.RED, BarStyle.SOLID);
					bossbar.setVisible(false);
					bossbar.setProgress(0);
					for (GamePlayer p : players) {
						bossbar.addPlayer(p.player);
					}
					bossbar.setColor(BarColor.valueOf(Main.getConfigurator().config.getString("bossbar.lobby.color")));
					bossbar.setStyle(BarStyle.valueOf(Main.getConfigurator().config.getString("bossbar.lobby.style")));
					bossbar.setVisible(getOriginalOrInheritedLobbyBossbar());
				} catch (Throwable t) {
				}
				if (teamSelectorInventory == null) {
					teamSelectorInventory = new TeamSelectorInventory(Main.getInstance(), this);
				}
				updateSigns();
			}
			updateLobbyScoreboard();
			if (teamsInGame.size() <= 1 || players.size() < minPlayers) {
				// Countdown reset
				countdown = pauseCountdown;
				return;
			}
			if (countdown <= 10 && countdown >= 1) {
				for (GamePlayer player : players) {
					Title.send(player.player, ChatColor.YELLOW + Integer.toString(countdown), "");
					Sounds.playSound(player.player, player.player.getLocation(),
							Main.getConfigurator().config.getString("sounds.on_countdown"), Sounds.UI_BUTTON_CLICK, 1,
							1);
				}
			}
			if (countdown == 0) {
				BedwarsGameStartEvent event = new BedwarsGameStartEvent(this);
				Main.getInstance().getServer().getPluginManager().callEvent(event);

				if (event.isCancelled()) {
					// reset timer
					countdown = pauseCountdown;
					return;
				}

				if (getOriginalOrInheritedJoinRandomTeamAfterLobby()) {
					for (GamePlayer player : players) {
						if (getPlayerTeam(player) == null) {
							joinRandomTeam(player);
						}
					}
				}

				this.status = GameStatus.RUNNING;
				this.countdown = this.gameTime;
				try {
					bossbar.setTitle(i18n("bossbar_running", false));
					bossbar.setProgress(0);
					bossbar.setColor(BarColor.valueOf(Main.getConfigurator().config.getString("bossbar.game.color")));
					bossbar.setStyle(BarStyle.valueOf(Main.getConfigurator().config.getString("bossbar.game.style")));
					bossbar.setVisible(getOriginalOrInheritedGameBossbar());
				} catch (Throwable tr) {

				}
				if (teamSelectorInventory != null)
					teamSelectorInventory.destroy();
				teamSelectorInventory = null;
				if (gameScoreboard.getObjective("lobby") != null) {
					gameScoreboard.getObjective("lobby").unregister();
				}
				gameScoreboard.clearSlot(DisplaySlot.SIDEBAR);
				updateScoreboard();
				updateSigns();
				for (GameStore store : gameStore) {
					Villager villager = store.spawn();
					if (villager != null) {
						Main.registerGameEntity(villager, this);
					}
				}
				String gameStartTitle = i18n("game_start_title", false);
				String gameStartSubtitle = i18n("game_start_subtitle", false).replace("%arena%", this.name);
				for (GamePlayer player : players) {
					CurrentTeam team = getPlayerTeam(player);
					player.player.getInventory().clear();
					Sounds.playSound(player.player, player.player.getLocation(),
							Main.getConfigurator().config.getString("sounds.on_game_start"),
							Sounds.ENTITY_PLAYER_LEVELUP, 1, 1);
					Title.send(player.player, gameStartTitle, gameStartSubtitle);
					if (team == null) {
						makeSpectator(player);
					} else {
						player.player.teleport(team.teamInfo.spawn);
						SpawnEffects.spawnEffect(this, player.player, "game-effects.start");
					}
				}

				BedwarsGameStartedEvent startedEvent = new BedwarsGameStartedEvent(this);
				Main.getInstance().getServer().getPluginManager().callEvent(startedEvent);
				return;
			}
			try {
				bossbar.setProgress((double) countdown / (double) pauseCountdown);
			} catch (Throwable tr) {

			}
			countdown--;
		} else if (this.status == GameStatus.REBUILDING) {
			BedwarsPreRebuildingEvent preRebuildingEvent = new BedwarsPreRebuildingEvent(this);
			Main.getInstance().getServer().getPluginManager().callEvent(preRebuildingEvent);

			region.regen();
			// Remove items
			Iterator<Entity> entityIterator = this.world.getEntities().iterator();
			while (entityIterator.hasNext()) {
				Entity e = entityIterator.next();
				if (GameCreator.isInArea(e.getLocation(), pos1, pos2)) {
					if (e instanceof Item) {
						e.remove();
					}
				}
			}

			// Chest clearing
			for (Location location : usedChests) {
				Block block = location.getBlock();
				if (block.getState() instanceof Chest) {
					Chest chest = (Chest) block.getState();
					chest.getBlockInventory().clear();
				}
			}
			usedChests.clear();

			BedwarsPostRebuildingEvent postRebuildingEvent = new BedwarsPostRebuildingEvent(this);
			Main.getInstance().getServer().getPluginManager().callEvent(postRebuildingEvent);

			this.status = this.afterRebuild;
			updateSigns();
			cancelTask();
		}
	}

	public GameStatus getStatus() {
		return status;
	}

	private void runTask() {
		if (task != null) {
			if (Bukkit.getScheduler().isQueued(task.getTaskId())) {
				task.cancel();
			}
			task = null;
		}
		task = (new BukkitRunnable() {

			public void run() {
				Game.this.run();
			}

		}.runTaskTimer(Main.getInstance(), 0, 20));
	}

	private void cancelTask() {
		if (task != null) {
			if (Bukkit.getScheduler().isQueued(task.getTaskId())) {
				task.cancel();
			}
			task = null;
		}
	}

	public void selectTeam(GamePlayer playerGameProfile, String displayName) {
		if (status == GameStatus.WAITING) {
			displayName = ChatColor.stripColor(displayName);
			playerGameProfile.player.closeInventory();
			for (Team team : teams) {
				if (displayName.equals(team.name)) {
					CurrentTeam current = null;
					for (CurrentTeam t : teamsInGame) {
						if (t.teamInfo == team) {
							current = t;
							break;
						}
					}
					CurrentTeam cur = getPlayerTeam(playerGameProfile);

					BedwarsPlayerJoinTeamEvent event = new BedwarsPlayerJoinTeamEvent(current, playerGameProfile.player,
							cur);
					Main.getInstance().getServer().getPluginManager().callEvent(event);

					if (event.isCancelled()) {
						return;
					}

					if (current == null) {
						current = new CurrentTeam(team, this);
						org.bukkit.scoreboard.Team scoreboardTeam = gameScoreboard.getTeam(team.name);
						if (scoreboardTeam == null) {
							scoreboardTeam = gameScoreboard.registerNewTeam(team.name);
						}
						if (!Main.isLegacy()) {
							scoreboardTeam.setColor(team.color.chatColor);
						} else {
							scoreboardTeam.setPrefix(team.color.chatColor.toString());
						}
						scoreboardTeam.setAllowFriendlyFire(getOriginalOrInheritedFriendlyfire());

						current.setScoreboardTeam(scoreboardTeam);
					}
					if (cur == current) {
						playerGameProfile.player.sendMessage(
								i18n("team_already_selected").replace("%team%", team.color.chatColor + team.name)
										.replace("%players%", Integer.toString(current.players.size()))
										.replaceAll("%maxplayers%", Integer.toString(current.teamInfo.maxPlayers)));
						return;
					}
					if (current.players.size() >= current.teamInfo.maxPlayers) {
						if (cur != null) {
							playerGameProfile.player.sendMessage(i18n("team_is_full_you_are_staying")
									.replace("%team%", team.color.chatColor + team.name)
									.replace("%oldteam%", cur.teamInfo.color.chatColor + cur.teamInfo.name));
						} else {
							playerGameProfile.player.sendMessage(
									i18n("team_is_full").replace("%team%", team.color.chatColor + team.name));
						}
						return;
					}
					if (cur != null) {
						cur.players.remove(playerGameProfile);
						cur.getScoreboardTeam().removeEntry(playerGameProfile.player.getName());
						if (cur.players.isEmpty()) {
							teamsInGame.remove(cur);
							cur.getScoreboardTeam().unregister();
						}
					}
					current.players.add(playerGameProfile);
					current.getScoreboardTeam().addEntry(playerGameProfile.player.getName());
					playerGameProfile.player
							.sendMessage(i18n("team_selected").replace("%team%", team.color.chatColor + team.name)
									.replace("%players%", Integer.toString(current.players.size()))
									.replaceAll("%maxplayers%", Integer.toString(current.teamInfo.maxPlayers)));

					if (getOriginalOrInheritedAddWoolToInventoryOnJoin()) {
						ItemStack stack = TeamSelectorInventory.materializeColorToWool(team.color);
						ItemMeta stackMeta = stack.getItemMeta();
						stackMeta.setDisplayName(team.color.chatColor + team.name);
						stack.setItemMeta(stackMeta);
						playerGameProfile.player.getInventory().setItem(1, stack);
					}

					if (getOriginalOrInheritedColoredLeatherByTeamInLobby()) {
						ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
						LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
						meta.setColor(team.color.leatherColor);
						chestplate.setItemMeta(meta);
						playerGameProfile.player.getInventory().setChestplate(chestplate);
					}

					if (!teamsInGame.contains(current)) {
						teamsInGame.add(current);
					}
					break;
				}
			}
		}
	}

	public void updateScoreboard() {
		if (status != GameStatus.RUNNING || !getOriginalOrInheritedScoreaboard()) {
			return;
		}

		Objective obj = this.gameScoreboard.getObjective("display");
		if (obj == null) {
			obj = this.gameScoreboard.registerNewObjective("display", "dummy");
		}

		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(this.formatScoreboardTitle());

		for (CurrentTeam team : teamsInGame) {
			this.gameScoreboard.resetScores(this.formatScoreboardTeam(team, false));
			this.gameScoreboard.resetScores(this.formatScoreboardTeam(team, true));

			Score score = obj.getScore(this.formatScoreboardTeam(team, !team.isBed));
			score.setScore(team.players.size());
		}

		for (GamePlayer player : players) {
			player.player.setScoreboard(gameScoreboard);
		}
	}

	private String formatScoreboardTeam(CurrentTeam team, boolean destroy) {
		if (team == null) {
			return "";
		}

		return Main.getConfigurator().config.getString("scoreboard.teamTitle")
				.replace("%color%", team.teamInfo.color.chatColor.toString()).replace("%team%", team.teamInfo.name)
				.replace("%bed%", destroy ? bedLostString() : bedExistString());
	}

	public static String bedExistString() {
		return Main.getConfigurator().config.getString("scoreboard.bedExists");
	}

	public static String bedLostString() {
		return Main.getConfigurator().config.getString("scoreboard.bedLost");
	}

	private void updateScoreboardTimer() {
		if (this.status != GameStatus.RUNNING || !getOriginalOrInheritedScoreaboard()) {
			return;
		}

		Objective obj = this.gameScoreboard.getObjective("display");
		if (obj == null) {
			obj = this.gameScoreboard.registerNewObjective("display", "dummy");
		}

		obj.setDisplayName(this.formatScoreboardTitle());

		for (GamePlayer player : players) {
			player.player.setScoreboard(gameScoreboard);
		}
	}

	private String formatScoreboardTitle() {
		return Main.getConfigurator().config.getString("scoreboard.title").replace("%game%", this.name)
				.replace("%time%", this.getFormattedTimeLeft());
	}

	private String getFormattedTimeLeft() {
		return getFormattedTimeLeft(this.countdown);
	}

	private String getFormattedTimeLeft(int countdown) {
		int min = 0;
		int sec = 0;
		String minStr = "";
		String secStr = "";

		min = (int) Math.floor(countdown / 60);
		sec = countdown % 60;

		minStr = (min < 10) ? "0" + String.valueOf(min) : String.valueOf(min);
		secStr = (sec < 10) ? "0" + String.valueOf(sec) : String.valueOf(sec);

		return minStr + ":" + secStr;
	}

	public void updateSigns() {
		List<GameSign> gameSigns = Main.getSignsForGame(this);

		if (gameSigns.isEmpty()) {
			return;
		}

		String line2 = "";
		String line3 = "";
		switch (status) {
		case DISABLED:
			line2 = i18n("sign_status_disabled", false);
			line3 = i18n("sign_status_disabled_players", false);
			break;
		case REBUILDING:
			line2 = i18n("sign_status_rebuilding", false);
			line3 = i18n("sign_status_rebuilding_players", false);
			break;
		case RUNNING:
			line2 = i18n("sign_status_running", false);
			line3 = i18n("sign_status_running_players", false);
			break;
		case WAITING:
			line2 = i18n("sign_status_waiting", false);
			line3 = i18n("sign_status_waiting_players", false);
			break;
		}
		line3 = line3.replace("%players%", Integer.toString(players.size()));
		line3 = line3.replace("%maxplayers%", Integer.toString(calculatedMaxPlayers));

		for (GameSign sign : gameSigns) {
			if (sign.getLocation().getChunk().isLoaded()) {
				Block block = sign.getLocation().getBlock();
				if (block.getState() instanceof Sign) {
					Sign state = (Sign) block.getState();
					state.setLine(2, line2);
					state.setLine(3, line3);
					state.update();
				}
			}
		}
	}

	private void updateLobbyScoreboard() {
		if (status != GameStatus.WAITING || !getOriginalOrInheritedLobbyScoreaboard()) {
			return;
		}
		gameScoreboard.clearSlot(DisplaySlot.SIDEBAR);

		Objective obj = gameScoreboard.getObjective("lobby");
		if (obj != null) {
			obj.unregister();
		}

		obj = gameScoreboard.registerNewObjective("lobby", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(this.formatLobbyScoreboardString(
				Main.getConfigurator().config.getString("lobby-scoreboard.title", "§eBEDWARS")));

		List<String> rows = Main.getConfigurator().config.getStringList("lobby-scoreboard.content");
		int rowMax = rows.size();
		if (rows == null || rows.isEmpty()) {
			return;
		}

		for (String row : rows) {
			if (row.trim().equals("")) {
				for (int i = 0; i <= rowMax; i++) {
					row = row + " ";
				}
			}

			Score score = obj.getScore(this.formatLobbyScoreboardString(row));
			score.setScore(rowMax);
			rowMax--;
		}

		for (GamePlayer player : players) {
			player.player.setScoreboard(gameScoreboard);
		}
	}

	private String formatLobbyScoreboardString(String str) {
		String finalStr = str;

		finalStr = finalStr.replace("%arena%", name);
		finalStr = finalStr.replace("%players%", String.valueOf(players.size()));
		finalStr = finalStr.replace("%maxplayers%", String.valueOf(calculatedMaxPlayers));

		return finalStr;
	}

	@Override
	public void selectPlayerTeam(Player player, misat11.bw.api.Team team) {
		if (!Main.isPlayerInGame(player)) {
			return;
		}
		GamePlayer profile = Main.getPlayerGameProfile(player);
		if (profile.getGame() != this) {
			return;
		}

		selectTeam(profile, team.getName());
	}

	@Override
	public World getGameWorld() {
		return world;
	}

	@Override
	public Location getSpectatorSpawn() {
		return specSpawn;
	}

	@Override
	public int countConnectedPlayers() {
		return players.size();
	}

	@Override
	public List<Player> getConnectedPlayers() {
		List<Player> playerList = new ArrayList<Player>();
		for (GamePlayer player : players) {
			playerList.add(player.player);
		}
		return playerList;
	}

	@Override
	public List<misat11.bw.api.Team> getAvailableTeams() {
		return new ArrayList<misat11.bw.api.Team>(teams);
	}

	@Override
	public List<RunningTeam> getRunningTeams() {
		return new ArrayList<misat11.bw.api.RunningTeam>(teamsInGame);
	}

	@Override
	public RunningTeam getTeamOfPlayer(Player player) {
		if (!Main.isPlayerInGame(player)) {
			return null;
		}
		return getPlayerTeam(Main.getPlayerGameProfile(player));
	}

	@Override
	public boolean isLocationInArena(Location location) {
		return GameCreator.isInArea(location, pos1, pos2);
	}

	@Override
	public World getLobbyWorld() {
		return lobbySpawn.getWorld();
	}

	@Override
	public int getLobbyCountdown() {
		return pauseCountdown;
	}

	@Override
	public CurrentTeam getTeamOfChest(Location location) {
		for (CurrentTeam team : teamsInGame) {
			if (team.isTeamChestRegistered(location)) {
				return team;
			}
		}
		return null;
	}

	@Override
	public CurrentTeam getTeamOfChest(Block block) {
		for (CurrentTeam team : teamsInGame) {
			if (team.isTeamChestRegistered(block)) {
				return team;
			}
		}
		return null;
	}

	public void addChestForFutureClear(Location loc) {
		if (!usedChests.contains(loc)) {
			usedChests.add(loc);
		}
	}

	@Override
	public int getMaxPlayers() {
		return calculatedMaxPlayers;
	}

	@Override
	public int countGameStores() {
		return gameStore.size();
	}

	@Override
	public int countAvailableTeams() {
		return teams.size();
	}

	@Override
	public int countRunningTeams() {
		return teamsInGame.size();
	}

	@Override
	public boolean isPlayerInAnyTeam(Player player) {
		return getTeamOfPlayer(player) != null;
	}

	@Override
	public boolean isPlayerInTeam(Player player, RunningTeam team) {
		return getTeamOfPlayer(player) == team;
	}

	@Override
	public int countTeamChests() {
		int total = 0;
		for (CurrentTeam team : teamsInGame) {
			total += team.countTeamChests();
		}
		return total;
	}

	@Override
	public int countTeamChests(RunningTeam team) {
		return team.countTeamChests();
	}

	@Override
	public List<SpecialItem> getActivedSpecialItems() {
		return new ArrayList<SpecialItem>(activeSpecialItems);
	}

	@Override
	public List<SpecialItem> getActivedSpecialItems(Class<? extends SpecialItem> type) {
		List<SpecialItem> items = new ArrayList<SpecialItem>();
		for (SpecialItem item : activeSpecialItems) {
			if (type.isInstance(item)) {
				items.add(item);
			}
		}
		return items;
	}

	@Override
	public List<SpecialItem> getActivedSpecialItemsOfTeam(misat11.bw.api.Team team) {
		List<SpecialItem> items = new ArrayList<SpecialItem>();
		for (SpecialItem item : activeSpecialItems) {
			if (item.getTeam() == team) {
				items.add(item);
			}
		}
		return items;
	}

	@Override
	public List<SpecialItem> getActivedSpecialItemsOfTeam(misat11.bw.api.Team team, Class<? extends SpecialItem> type) {
		List<SpecialItem> items = new ArrayList<SpecialItem>();
		for (SpecialItem item : activeSpecialItems) {
			if (type.isInstance(item) && item.getTeam() == team) {
				items.add(item);
			}
		}
		return items;
	}

	@Override
	public SpecialItem getFirstActivedSpecialItemOfTeam(misat11.bw.api.Team team) {
		for (SpecialItem item : activeSpecialItems) {
			if (item.getTeam() == team) {
				return item;
			}
		}
		return null;
	}

	@Override
	public SpecialItem getFirstActivedSpecialItemOfTeam(misat11.bw.api.Team team, Class<? extends SpecialItem> type) {
		for (SpecialItem item : activeSpecialItems) {
			if (item.getTeam() == team && type.isInstance(item)) {
				return item;
			}
		}
		return null;
	}

	@Override
	public List<SpecialItem> getActivedSpecialItemsOfPlayer(Player player) {
		List<SpecialItem> items = new ArrayList<SpecialItem>();
		for (SpecialItem item : activeSpecialItems) {
			if (item.getPlayer() == player) {
				items.add(item);
			}
		}
		return items;
	}

	@Override
	public List<SpecialItem> getActivedSpecialItemsOfPlayer(Player player, Class<? extends SpecialItem> type) {
		List<SpecialItem> items = new ArrayList<SpecialItem>();
		for (SpecialItem item : activeSpecialItems) {
			if (item.getPlayer() == player && type.isInstance(item)) {
				items.add(item);
			}
		}
		return items;
	}

	@Override
	public SpecialItem getFirstActivedSpecialItemOfPlayer(Player player) {
		for (SpecialItem item : activeSpecialItems) {
			if (item.getPlayer() == player) {
				return item;
			}
		}
		return null;
	}

	@Override
	public SpecialItem getFirstActivedSpecialItemOfPlayer(Player player, Class<? extends SpecialItem> type) {
		for (SpecialItem item : activeSpecialItems) {
			if (item.getPlayer() == player && type.isInstance(item)) {
				return item;
			}
		}
		return null;
	}

	@Override
	public void registerSpecialItem(SpecialItem item) {
		if (!activeSpecialItems.contains(item)) {
			activeSpecialItems.add(item);
		}
	}

	@Override
	public void unregisterSpecialItem(SpecialItem item) {
		if (activeSpecialItems.contains(item)) {
			activeSpecialItems.remove(item);
		}
	}

	@Override
	public boolean isRegisteredSpecialItem(SpecialItem item) {
		return activeSpecialItems.contains(item);
	}

	public InGameConfigBooleanConstants getCompassEnabled() {
		return compassEnabled;
	}

	public void setCompassEnabled(InGameConfigBooleanConstants compassEnabled) {
		this.compassEnabled = compassEnabled;
	}

	public InGameConfigBooleanConstants getJoinRandomTeamAfterLobby() {
		return joinRandomTeamAfterLobby;
	}

	public void setJoinRandomTeamAfterLobby(InGameConfigBooleanConstants joinRandomTeamAfterLobby) {
		this.joinRandomTeamAfterLobby = joinRandomTeamAfterLobby;
	}

	public InGameConfigBooleanConstants getJoinRandomTeamOnJoin() {
		return joinRandomTeamOnJoin;
	}

	public void setJoinRandomTeamOnJoin(InGameConfigBooleanConstants joinRandomTeamOnJoin) {
		this.joinRandomTeamOnJoin = joinRandomTeamOnJoin;
	}

	public InGameConfigBooleanConstants getAddWoolToInventoryOnJoin() {
		return addWoolToInventoryOnJoin;
	}

	public void setAddWoolToInventoryOnJoin(InGameConfigBooleanConstants addWoolToInventoryOnJoin) {
		this.addWoolToInventoryOnJoin = addWoolToInventoryOnJoin;
	}

	public InGameConfigBooleanConstants getPreventKillingVillagers() {
		return preventKillingVillagers;
	}

	public void setPreventKillingVillagers(InGameConfigBooleanConstants preventKillingVillagers) {
		this.preventKillingVillagers = preventKillingVillagers;
	}

	public InGameConfigBooleanConstants getSpectatorGm3() {
		return spectatorGm3;
	}

	public void setSpectatorGm3(InGameConfigBooleanConstants spectatorGm3) {
		this.spectatorGm3 = spectatorGm3;
	}

	public InGameConfigBooleanConstants getPlayerDrops() {
		return playerDrops;
	}

	public void setPlayerDrops(InGameConfigBooleanConstants playerDrops) {
		this.playerDrops = playerDrops;
	}

	public InGameConfigBooleanConstants getFriendlyfire() {
		return friendlyfire;
	}

	public void setFriendlyfire(InGameConfigBooleanConstants friendlyfire) {
		this.friendlyfire = friendlyfire;
	}

	public InGameConfigBooleanConstants getColoredLeatherByTeamInLobby() {
		return coloredLeatherByTeamInLobby;
	}

	public void setColoredLeatherByTeamInLobby(InGameConfigBooleanConstants coloredLeatherByTeamInLobby) {
		this.coloredLeatherByTeamInLobby = coloredLeatherByTeamInLobby;
	}

	public InGameConfigBooleanConstants getKeepInventory() {
		return keepInventory;
	}

	public void setKeepInventory(InGameConfigBooleanConstants keepInventory) {
		this.keepInventory = keepInventory;
	}

	public InGameConfigBooleanConstants getCrafting() {
		return crafting;
	}

	public void setCrafting(InGameConfigBooleanConstants crafting) {
		this.crafting = crafting;
	}

	@Override
	public boolean getOriginalOrInheritedCompassEnabled() {
		return compassEnabled.isOriginal() ? compassEnabled.getValue()
				: Main.getConfigurator().config.getBoolean(COMPASS_ENABLED);
	}

	@Override
	public boolean getOriginalOrInheritedJoinRandomTeamAfterLobby() {
		return joinRandomTeamAfterLobby.isOriginal() ? joinRandomTeamAfterLobby.getValue()
				: Main.getConfigurator().config.getBoolean(JOIN_RANDOM_TEAM_AFTER_LOBBY);
	}

	@Override
	public boolean getOriginalOrInheritedJoinRandomTeamOnJoin() {
		return joinRandomTeamOnJoin.isOriginal() ? joinRandomTeamOnJoin.getValue()
				: Main.getConfigurator().config.getBoolean(JOIN_RANDOM_TEAM_ON_JOIN);
	}

	@Override
	public boolean getOriginalOrInheritedAddWoolToInventoryOnJoin() {
		return addWoolToInventoryOnJoin.isOriginal() ? addWoolToInventoryOnJoin.getValue()
				: Main.getConfigurator().config.getBoolean(ADD_WOOL_TO_INVENTORY_ON_JOIN);
	}

	@Override
	public boolean getOriginalOrInheritedPreventKillingVillagers() {
		return preventKillingVillagers.isOriginal() ? preventKillingVillagers.getValue()
				: Main.getConfigurator().config.getBoolean(PREVENT_KILLING_VILLAGERS);
	}

	@Override
	public boolean getOriginalOrInheritedSpectatorGm3() {
		return spectatorGm3.isOriginal() ? spectatorGm3.getValue()
				: Main.getConfigurator().config.getBoolean(SPECTATOR_GM_3);
	}

	@Override
	public boolean getOriginalOrInheritedPlayerDrops() {
		return playerDrops.isOriginal() ? playerDrops.getValue()
				: Main.getConfigurator().config.getBoolean(PLAYER_DROPS);
	}

	@Override
	public boolean getOriginalOrInheritedFriendlyfire() {
		return friendlyfire.isOriginal() ? friendlyfire.getValue()
				: Main.getConfigurator().config.getBoolean(FRIENDLY_FIRE);
	}

	@Override
	public boolean getOriginalOrInheritedColoredLeatherByTeamInLobby() {
		return coloredLeatherByTeamInLobby.isOriginal() ? coloredLeatherByTeamInLobby.getValue()
				: Main.getConfigurator().config.getBoolean(COLORED_LEATHER_BY_TEAM_IN_LOBBY);
	}

	@Override
	public boolean getOriginalOrInheritedKeepInventory() {
		return keepInventory.isOriginal() ? keepInventory.getValue()
				: Main.getConfigurator().config.getBoolean(KEEP_INVENTORY);
	}

	@Override
	public boolean getOriginalOrInheritedCrafting() {
		return crafting.isOriginal() ? crafting.getValue() : Main.getConfigurator().config.getBoolean(CRAFTING);
	}

	@Override
	public InGameConfigBooleanConstants getLobbyBossbar() {
		return lobbybossbar;
	}

	@Override
	public boolean getOriginalOrInheritedLobbyBossbar() {
		return lobbybossbar.isOriginal() ? lobbybossbar.getValue() : Main.getConfigurator().config.getBoolean(GLOBAL_LOBBY_BOSSBAR);
	}

	@Override
	public InGameConfigBooleanConstants getGameBossbar() {
		return gamebossbar;
	}

	@Override
	public boolean getOriginalOrInheritedGameBossbar() {
		return gamebossbar.isOriginal() ? gamebossbar.getValue() : Main.getConfigurator().config.getBoolean(GLOBAL_GAME_BOSSBAR);
	}

	@Override
	public InGameConfigBooleanConstants getScoreboard() {
		return ascoreboard;
	}

	@Override
	public boolean getOriginalOrInheritedScoreaboard() {
		return ascoreboard.isOriginal() ? ascoreboard.getValue() : Main.getConfigurator().config.getBoolean(GLOBAL_SCOREBOARD);
	}

	@Override
	public InGameConfigBooleanConstants getLobbyScoreboard() {
		return lobbyscoreboard;
	}

	@Override
	public boolean getOriginalOrInheritedLobbyScoreaboard() {
		return lobbyscoreboard.isOriginal() ? lobbyscoreboard.getValue() : Main.getConfigurator().config.getBoolean(GLOBAL_LOBBY_SCOREBOARD);
	}

	public void setLobbybossbar(InGameConfigBooleanConstants lobbybossbar) {
		this.lobbybossbar = lobbybossbar;
	}

	public void setGamebossbar(InGameConfigBooleanConstants gamebossbar) {
		this.gamebossbar = gamebossbar;
	}

	public void setAscoreboard(InGameConfigBooleanConstants ascoreboard) {
		this.ascoreboard = ascoreboard;
	}

	public void setLobbyscoreboard(InGameConfigBooleanConstants lobbyscoreboard) {
		this.lobbyscoreboard = lobbyscoreboard;
	}

	public void setPreventSpawningMobs(InGameConfigBooleanConstants preventSpawningMobs) {
		this.preventSpawningMobs = preventSpawningMobs;
	}

	@Override
	public InGameConfigBooleanConstants getPreventSpawningMobs() {
		return preventSpawningMobs;
	}

	@Override
	public boolean getOriginalOrInheritedPreventSpawningMobs() {
		return preventSpawningMobs.isOriginal() ? preventSpawningMobs.getValue() : Main.getConfigurator().config.getBoolean(PREVENT_SPAWNING_MOBS);
	}

	@Override
	public ArenaTime getArenaTime() {
		return arenaTime;
	}

	public void setArenaTime(ArenaTime arenaTime) {
		this.arenaTime = arenaTime;
	}

	@Override
	public WeatherType getArenaWeather() {
		return arenaWeather;
	}

	public void setArenaWeather(WeatherType arenaWeather) {
		this.arenaWeather = arenaWeather;
	}
	
	

}
