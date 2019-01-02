package misat11.bw.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import misat11.bw.Main;
import misat11.bw.utils.BedUtils;
import misat11.bw.utils.I18n;
import misat11.bw.utils.TeamSelectorInventory;
import misat11.bw.utils.Title;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

	private String name;
	private Location pos1;
	private Location pos2;
	private Location lobbySpawn;
	private Location specSpawn;
	private List<Team> teams = new ArrayList<Team>();
	private List<ItemSpawner> spawners = new ArrayList<ItemSpawner>();
	private int pauseCountdown;
	private int gameTime;
	private List<GamePlayer> players = new ArrayList<GamePlayer>();
	private World world;
	private List<GameStore> gameStore = new ArrayList<GameStore>();

	// STATUS
	private GameStatus status = GameStatus.DISABLED;
	private GameStatus afterRebuild = GameStatus.WAITING;
	private int countdown = -1;
	private int calculatedMaxPlayers;
	private BukkitTask task;
	private List<CurrentTeam> teamsInGame = new ArrayList<CurrentTeam>();
	private List<Location> buildedBlocks = new ArrayList<Location>();
	private Map<Location, BlockData> breakedOriginalBlocks = new HashMap<Location, BlockData>();
	private TeamSelectorInventory teamSelectorInventory;
	private Scoreboard gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	private BossBar bossbar;

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
		return status == GameStatus.RUNNING && buildedBlocks.contains(loc);
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
			if (replaced.getType() == Material.WATER || replaced.getType() == Material.LAVA) {
				breakedOriginalBlocks.put(block.getLocation(), replaced.getBlockData());
			} else {
				return false;
			}
		}
		buildedBlocks.add(block.getLocation());
		return true;
	}

	public boolean blockBreak(GamePlayer player, Block block) {
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
		if (buildedBlocks.contains(block.getLocation())) {
			buildedBlocks.remove(block.getLocation());
			return true;
		}
		if (block.getBlockData() instanceof Bed) {
			Location loc = block.getLocation();
			Bed bed = (Bed) block.getBlockData();
			if (bed.getPart() != Part.HEAD) {
				loc = BedUtils.getBedNeighbor(block).getLocation();
			}
			if (getPlayerTeam(player).teamInfo.bed.equals(loc)) {
				return false;
			}
			block.getDrops().clear();
			bedDestroyed(loc);
			breakedOriginalBlocks.put(block.getLocation(), block.getBlockData());
			if (block.getLocation().equals(loc)) {
				Block neighbor = BedUtils.getBedNeighbor(block);
				breakedOriginalBlocks.put(neighbor.getLocation(), neighbor.getBlockData());
			} else {
				breakedOriginalBlocks.put(loc, BedUtils.getBedNeighbor(block).getBlockData());
			}
			return true;
		}
		return false;
	}

	public CurrentTeam getPlayerTeam(GamePlayer player) {
		for (CurrentTeam team : teamsInGame) {
			if (team.players.contains(player)) {
				return team;
			}
		}
		return null;
	}

	protected void bedDestroyed(Location loc) {
		if (status == GameStatus.RUNNING) {
			for (CurrentTeam team : teamsInGame) {
				if (team.teamInfo.bed.equals(loc)) {
					team.isBed = false;
					updateScoreboard();
					for (GamePlayer player : players) {
						Title.send(player.player,
								I18n._("bed_is_destroyed", false).replace("%team%",
										team.teamInfo.color.chatColor + team.teamInfo.name),
								I18n._("bed_is_destroyed_subtitle", false));
						player.player.sendMessage(I18n._("bed_is_destroyed").replace("%team%",
								team.teamInfo.color.chatColor + team.teamInfo.name));
						player.player.playSound(player.player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
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
			player.player.sendMessage(I18n._("game_is_rebuilding").replace("%arena%", this.name));
			player.changeGame(null);
			return;
		}
		if (status == GameStatus.RUNNING) {
			player.player.sendMessage(I18n._("game_already_running").replace("%arena%", this.name));
			player.changeGame(null);
			return;
		}
		if (players.size() >= calculatedMaxPlayers) {
			player.player.sendMessage(I18n._("game_is_full").replace("%arena%", this.name));
			player.changeGame(null);
			return;
		}
		boolean isEmpty = players.isEmpty();
		if (!players.contains(player)) {
			players.add(player);
		}

		player.player.teleport(lobbySpawn);
		String message = I18n._("join").replace("%name%", player.player.getDisplayName())
				.replace("%players%", Integer.toString(players.size()))
				.replaceAll("%maxplayers%", Integer.toString(calculatedMaxPlayers));
		for (GamePlayer p : players)
			p.player.sendMessage(message);

		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta metaCompass = compass.getItemMeta();
		metaCompass.setDisplayName(I18n._("compass_selector_team", false));
		compass.setItemMeta(metaCompass);
		ItemStack leave = new ItemStack(Material.SLIME_BALL);
		ItemMeta leaveMeta = leave.getItemMeta();
		leaveMeta.setDisplayName(I18n._("leave_from_game_item", false));
		leave.setItemMeta(leaveMeta);
		player.player.getInventory().setItem(0, compass);
		player.player.getInventory().setItem(8, leave);

		if (isEmpty) {
			runTask();
		} else {
			bossbar.addPlayer(player.player);
		}
	}

	public void leavePlayer(GamePlayer player) {
		if (status == GameStatus.DISABLED) {
			return;
		}
		if (players.contains(player)) {
			players.remove(player);
		}

		String message = I18n._("leave").replace("%name%", player.player.getDisplayName())
				.replace("%players%", Integer.toString(players.size()))
				.replaceAll("%maxplayers%", Integer.toString(calculatedMaxPlayers));
		bossbar.removePlayer(player.player);
		player.player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		if (status == GameStatus.RUNNING) {
			CurrentTeam team = getPlayerTeam(player);
			if (team != null) {
				team.players.remove(player);
				updateScoreboard();
			}
		}
		for (GamePlayer p : players)
			p.player.sendMessage(message);
		if (players.isEmpty()) {
			if (status == GameStatus.RUNNING) {
				status = GameStatus.REBUILDING;
				afterRebuild = GameStatus.WAITING;
			} else {
				status = GameStatus.WAITING;
				cancelTask();
			}
			countdown = -1;
			gameScoreboard.clearSlot(DisplaySlot.BELOW_NAME);
			for (CurrentTeam team : teamsInGame) {
				team.getScoreboardTeam().unregister();
			}
			teamsInGame.clear();
			for (GameStore store : gameStore) {
				store.forceKill();
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
		if (configMap.isSet("spawners")) {
			List<Map<String, String>> spawners = (List<Map<String, String>>) configMap.getList("spawners");
			for (Map<String, String> spawner : spawners) {
				ItemSpawner sa = new ItemSpawner(readLocationFromString(game.world, spawner.get("location")),
						readTypeFromString(spawner.get("type")));
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
				game.teams.add(t);
			}
		}
		if (configMap.isSet("stores")) {
			List<String> stores = (List<String>) configMap.getList("stores");
			for (String store : stores) {
				game.gameStore.add(new GameStore(readLocationFromString(game.world, store)));
			}
		}
		game.start();
		Main.getInstance().getLogger().info("Arena " + game.name + " loaded!");
		Main.addGame(game);
		return game;
	}

	public static ItemSpawnerType readTypeFromString(String string) {
		ItemSpawnerType type = ItemSpawnerType.BRONZE;
		if (string.equalsIgnoreCase("iron")) {
			type = ItemSpawnerType.IRON;
		} else if (string.equalsIgnoreCase("gold")) {
			type = ItemSpawnerType.GOLD;
		}
		return type;
	}

	public static String setTypeToString(ItemSpawnerType type) {
		return type.toString();
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
		List<Map<String, String>> nS = new ArrayList<Map<String, String>>();
		for (ItemSpawner spawner : spawners) {
			Map<String, String> spawnerMap = new HashMap<String, String>();
			spawnerMap.put("location", setLocationToString(spawner.loc));
			spawnerMap.put("type", setTypeToString(spawner.type));
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
				nL.add(setLocationToString(store.loc));
			}
			configMap.set("stores", nL);
		}

		try {
			configMap.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Game createGame(String name) {
		Game game = new Game();
		game.name = name;

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
		}
	}

	public void stop() {
		List<GamePlayer> clonedPlayers = (List<GamePlayer>) ((ArrayList<GamePlayer>) players).clone();
		for (GamePlayer p : clonedPlayers)
			p.changeGame(null);
		if (status != GameStatus.REBUILDING) {
			status = GameStatus.DISABLED;
		} else {
			afterRebuild = GameStatus.DISABLED;
		}
	}

	public void joinToGame(Player player) {
		if (status == GameStatus.DISABLED) {
			return;
		}
		if (status == GameStatus.REBUILDING) {
			player.sendMessage(I18n._("game_is_rebuilding").replace("%arena%", this.name));
			return;
		}
		if (status == GameStatus.RUNNING) {
			player.sendMessage(I18n._("game_already_running").replace("%arena%", this.name));
			return;
		}
		if (players.size() >= calculatedMaxPlayers) {
			player.sendMessage(I18n._("game_is_full").replace("%arena%", this.name));
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

	public void run() {
		if (this.status == GameStatus.RUNNING) {
			updateScoreboardTimer();
			if (countdown == 0) {
				this.status = GameStatus.REBUILDING;
				this.countdown = -1;
				teamsInGame.clear();
				for (GameStore store : gameStore) {
					store.forceKill();
				}
				String message = I18n._("game_end");
				for (GamePlayer player : (List<GamePlayer>) ((ArrayList<GamePlayer>) players).clone()) {
					player.player.sendMessage(message);
					player.changeGame(null);
				}
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
							String message = I18n._("team_win", true)
									.replace("%team%", t.teamInfo.color.chatColor + t.teamInfo.name)
									.replace("%time%", time);
							String subtitle = I18n._("team_win", false)
									.replace("%team%", t.teamInfo.color.chatColor + t.teamInfo.name)
									.replace("%time%", time);
							for (GamePlayer player : players) {
								player.player.sendMessage(message);
								if (getPlayerTeam(player) == t) {
									Title.send(player.player, I18n._("you_won", false), subtitle);
									Main.depositPlayer(player.player, Main.getVaultWinReward());
								} else {
									Title.send(player.player, I18n._("you_lost", false), subtitle);
								}
							}
							break;
						}
					}
				}
				countdown = 0;
			} else {
				bossbar.setProgress((double) countdown / (double) gameTime);
				countdown--;
				for (ItemSpawner spawner : spawners) {
					ItemSpawnerType type = spawner.type;
					int cycle = Main.getConfigurator().config.getInt("spawners." + type.name().toLowerCase());
					if ((countdown % cycle) == 0) {
						Location loc = spawner.loc.clone().add(0, 1, 0);
						ItemStack stack = new ItemStack(type.material);
						ItemMeta stackMeta = stack.getItemMeta();
						stackMeta.setDisplayName(type.color + I18n._("resource_" + type.name().toLowerCase(), false));
						stack.setItemMeta(stackMeta);
						loc.getWorld().dropItemNaturally(loc, stack);
					}
				}
			}
		} else if (this.status == GameStatus.WAITING) {
			if (countdown == -1) {
				countdown = pauseCountdown;
				String title = I18n._("bossbar_waiting", false);
				bossbar = Bukkit.createBossBar(title, BarColor.RED, BarStyle.SEGMENTED_20);
				bossbar.setColor(BarColor.YELLOW);
				bossbar.setProgress(0);
				for (GamePlayer p : players) {
					bossbar.addPlayer(p.player);
				}
				if (teamSelectorInventory == null) {
					teamSelectorInventory = new TeamSelectorInventory(Main.getInstance(), this);
				}
			}
			if (teamsInGame.size() <= 1) {
				return;
			}
			if (countdown <= 10 && countdown >= 1) {
				for (GamePlayer player : players) {
					Title.send(player.player, ChatColor.YELLOW + Integer.toString(countdown), "");
					player.player.playSound(player.player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
				}
			}
			if (countdown == 0) {
				this.status = GameStatus.RUNNING;
				this.countdown = this.gameTime;
				bossbar.setTitle(I18n._("bossbar_running", false));
				bossbar.setProgress(0);
				bossbar.setColor(BarColor.GREEN);
				if (teamSelectorInventory != null)
					teamSelectorInventory.destroy();
				teamSelectorInventory = null;
				updateScoreboard();
				for (GameStore store : gameStore) {
					store.spawn();
				}
				String gameStartTitle = I18n._("game_start_title", false);
				String gameStartSubtitle = I18n._("game_start_subtitle", false).replace("%arena%", this.name);
				for (GamePlayer player : players) {
					CurrentTeam team = getPlayerTeam(player);
					player.player.getInventory().clear();
					player.player.playSound(player.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
					Title.send(player.player, gameStartTitle, gameStartSubtitle);
					if (team == null) {
						player.isSpectator = true;
						player.player.teleport(specSpawn);
						player.player.setAllowFlight(true);
						player.player.setFlying(true);
						player.player
								.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
						player.player.setCollidable(false);
					} else {
						player.player.teleport(team.teamInfo.spawn);
					}
				}
				return;
			}
			bossbar.setProgress((double) countdown / (double) pauseCountdown);
			countdown--;
		} else if (this.status == GameStatus.REBUILDING) {
			for (Location block : buildedBlocks) {
				Chunk chunk = block.getChunk();
				if (!chunk.isLoaded()) {
					chunk.load();
				}
				block.getBlock().setType(Material.AIR);
			}
			for (Map.Entry<Location, BlockData> block : breakedOriginalBlocks.entrySet()) {
				Chunk chunk = block.getKey().getChunk();
				if (!chunk.isLoaded()) {
					chunk.load();
				}
				block.getKey().getBlock().setBlockData(block.getValue());
			}
			this.status = this.afterRebuild;
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
			playerGameProfile.player.closeInventory();
			for (Team team : teams) {
				if (displayName.equals(team.color.chatColor + team.name)) {
					CurrentTeam current = null;
					for (CurrentTeam t : teamsInGame) {
						if (t.teamInfo == team) {
							current = t;
							break;
						}
					}
					if (current == null) {
						current = new CurrentTeam(team);
						org.bukkit.scoreboard.Team scoreboardTeam = gameScoreboard.getTeam(team.name);
						if (scoreboardTeam == null) {
							scoreboardTeam = gameScoreboard.registerNewTeam(team.name);
						}
						scoreboardTeam.setColor(team.color.chatColor);

						current.setScoreboardTeam(scoreboardTeam);
					}
					CurrentTeam cur = getPlayerTeam(playerGameProfile);
					if (cur == current) {
						playerGameProfile.player.sendMessage(
								I18n._("team_already_selected").replace("%team%", team.color.chatColor + team.name)
										.replace("%players%", Integer.toString(current.players.size()))
										.replaceAll("%maxplayers%", Integer.toString(current.teamInfo.maxPlayers)));
						return;
					}
					if (current.players.size() >= current.teamInfo.maxPlayers) {
						if (cur != null) {
							playerGameProfile.player.sendMessage(I18n._("team_is_full_you_are_staying")
									.replace("%team%", team.color.chatColor + team.name)
									.replace("%oldteam%", cur.teamInfo.color.chatColor + cur.teamInfo.name));
						} else {
							playerGameProfile.player.sendMessage(
									I18n._("team_is_full").replace("%team%", team.color.chatColor + team.name));
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
							.sendMessage(I18n._("team_selected").replace("%team%", team.color.chatColor + team.name)
									.replace("%players%", Integer.toString(current.players.size()))
									.replaceAll("%maxplayers%", Integer.toString(current.teamInfo.maxPlayers)));
					ItemStack stack = TeamSelectorInventory.materializeColorToWool(team.color);
					ItemMeta stackMeta = stack.getItemMeta();
					stackMeta.setDisplayName(team.color.chatColor + team.name);
					stack.setItemMeta(stackMeta);
					playerGameProfile.player.getInventory().setItem(1, stack);
					if (!teamsInGame.contains(current)) {
						teamsInGame.add(current);
					}
					break;
				}
			}
		}
	}

	public void updateScoreboard() {
		if (status != GameStatus.RUNNING || !Main.getConfigurator().config.getBoolean("scoreboard.enable")) {
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
		if (this.status != GameStatus.RUNNING || !Main.getConfigurator().config.getBoolean("scoreboard.enable")) {
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

}
