package misat11.bw.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import misat11.bw.Main;
import misat11.bw.api.GameStore;
import misat11.bw.utils.BedUtils;
import misat11.bw.utils.TeamJoinMetaDataValue;

import static misat11.bw.utils.I18n.i18n;

public class GameCreator {

	public static final String BEDWARS_TEAM_JOIN_METADATA = "bw-addteamjoin";

	private Game game;
	private HashMap<String, Location> villagerstores = new HashMap<String, Location>();

	public GameCreator(Game game) {
		this.game = game;
		List<GameStore> gs = game.getGameStores();
		if (!gs.isEmpty()) {
			for (GameStore store : gs) {
				villagerstores.put(store.loc.getBlockX() + ";" + store.loc.getBlockY() + ";" + store.loc.getBlockZ(),
						store.loc);
			}
		}
	}

	public Game getGame() {
		return game;
	}

	public boolean cmd(Player player, String action, String[] args) {
		boolean isArenaSaved = false;
		String response = null;
		if (action.equalsIgnoreCase("lobby")) {
			response = setLobbySpawn(player.getLocation());
		} else if (action.equalsIgnoreCase("spec")) {
			response = setSpecSpawn(player.getLocation());
		} else if (action.equalsIgnoreCase("pos1")) {
			response = setPos1(player.getLocation());
		} else if (action.equalsIgnoreCase("pos2")) {
			response = setPos2(player.getLocation());
		} else if (action.equalsIgnoreCase("pausecountdown")) {
			if (args.length >= 1) {
				response = setPauseCountdown(Integer.parseInt(args[0]));
			}
		} else if (action.equalsIgnoreCase("time")) {
			if (args.length >= 1) {
				response = setGameTime(Integer.parseInt(args[0]));
			}
		} else if (action.equalsIgnoreCase("minplayers")) {
			if (args.length >= 1) {
				response = setMinPlayers(Integer.parseInt(args[0]));
			}
		} else if (action.equalsIgnoreCase("team")) {
			if (args.length >= 2) {
				if (args[0].equalsIgnoreCase("add")) {
					if (args.length >= 4) {
						response = addTeam(args[1], args[2], Integer.parseInt(args[3]));
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					response = removeTeam(args[1]);
				} else if (args[0].equalsIgnoreCase("color")) {
					if (args.length >= 3) {
						response = setTeamColor(args[1], args[2]);
					}
				} else if (args[0].equalsIgnoreCase("maxplayers")) {
					if (args.length >= 3) {
						response = setTeamMaxPlayers(args[1], Integer.parseInt(args[2]));
					}
				} else if (args[0].equalsIgnoreCase("spawn")) {
					response = setTeamSpawn(args[1], player.getLocation());
				} else if (args[0].equalsIgnoreCase("bed")) {
					response = setTeamBed(args[1], player.getTargetBlock((Set<Material>) null, 5));
				}
			}
		} else if (action.equalsIgnoreCase("spawner")) {
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("add")) {
					if (args.length >= 2) {
						response = addSpawner(args[1], player.getLocation());
					}
				} else if (args[0].equalsIgnoreCase("reset")) {
					response = resetAllSpawners();
				}
			}
		} else if (action.equalsIgnoreCase("store"))

		{
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("add")) {
					response = addStore(player.getLocation());
				} else if (args[0].equalsIgnoreCase("remove")) {
					response = removeStore(player.getLocation());
				}
			}
		} else if (action.equalsIgnoreCase("jointeam")) {
			if (args.length >= 1) {
				response = addTeamJoinEntity(player, args[0]);
			}
		} else if (action.equalsIgnoreCase("save")) {
			List<GameStore> gamestores = new ArrayList<GameStore>();
			for (Map.Entry<String, Location> vloc : villagerstores.entrySet()) {
				gamestores.add(new GameStore(vloc.getValue()));
			}
			boolean isTeamsSetCorrectly = true;
			for (Team team : game.getTeams()) {
				if (team.bed == null) {
					response = i18n("admin_command_set_bed_for_team_before_save").replace("%team%", team.name);
					isTeamsSetCorrectly = false;
					break;
				} else if (team.spawn == null) {
					response = i18n("admin_command_set_spawn_for_team_before_save").replace("%team%", team.name);
					isTeamsSetCorrectly = false;
					break;
				}
			}
			if (isTeamsSetCorrectly) {
				game.setGameStores(gamestores);
				if (game.getTeams().size() < 2) {
					response = i18n("admin_command_need_min_2_teems");
				} else if (game.getPos1() == null || game.getPos2() == null) {
					response = i18n("admin_command_set_pos1_pos2_before_save");
				} else if (game.getLobbySpawn() == null) {
					response = i18n("admin_command_set_lobby_before_save");
				} else if (game.getSpecSpawn() == null) {
					response = i18n("admin_command_set_spec_before_save");
				} else if (game.getGameStores().isEmpty()) {
					response = i18n("admin_command_set_stores_before_save");
				} else if (game.getSpawners().isEmpty()) {
					response = i18n("admin_command_set_spawners_before_save");
				} else {
					game.saveToConfig();
					game.start();
					Main.addGame(game);
					response = i18n("admin_command_game_saved_and_started");
					isArenaSaved = true;
				}
			}
		}

		if (response == null) {
			response = i18n("unknown_command");
		}
		player.sendMessage(response);
		return isArenaSaved;
	}

	private String setMinPlayers(int minPlayers) {
		if (minPlayers < 2) {
			return i18n("admin_command_invalid_min_players");
		}
		game.setMinPlayers(minPlayers);
		return i18n("admin_command_min_players_set").replace("%min%", Integer.toString(minPlayers));
	}

	private String addTeamJoinEntity(final Player player, String name) {
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				if (player.hasMetadata(BEDWARS_TEAM_JOIN_METADATA)) {
					player.removeMetadata(BEDWARS_TEAM_JOIN_METADATA, Main.getInstance());
				}
				player.setMetadata(BEDWARS_TEAM_JOIN_METADATA, new TeamJoinMetaDataValue(t));

				new BukkitRunnable() {
					public void run() {
						if (!player.hasMetadata(BEDWARS_TEAM_JOIN_METADATA)) {
							return;
						}

						player.removeMetadata(BEDWARS_TEAM_JOIN_METADATA, Main.getInstance());
					}
				}.runTaskLater(Main.getInstance(), 200L);
				return i18n("admin_command_click_right_on_entity_to_set_join").replace("%team%", t.name);
			}
		}
		return i18n("admin_command_team_is_not_exists");
	}

	private String setTeamBed(String name, Block block) {
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				Location loc = block.getLocation();
				if (game.getPos1() == null || game.getPos2() == null) {
					return i18n("admin_command_set_pos1_pos2_first");
				}
				if (game.getWorld() != loc.getWorld()) {
					return i18n("admin_command_must_be_in_same_world");
				}
				if (!isInArea(loc, game.getPos1(), game.getPos2())) {
					return i18n("admin_command_spawn_must_be_in_area");
				}
				if (Main.isLegacy()) {
					// Legacy
					if (!(block.getState().getData() instanceof org.bukkit.material.Bed)) {
						return i18n("admin_command_block_is_not_bed");
					}

					org.bukkit.material.Bed bed = (org.bukkit.material.Bed) block.getState().getData();
					if (!bed.isHeadOfBed()) {
						t.bed = misat11.bw.legacy.LegacyBedUtils.getBedNeighbor(block).getLocation();
					} else {
						t.bed = loc;
					}

				} else {
					// 1.13+
					if (!(block.getBlockData() instanceof Bed)) {
						return i18n("admin_command_block_is_not_bed");
					}

					Bed bed = (Bed) block.getBlockData();
					if (bed.getPart() != Part.HEAD) {
						t.bed = BedUtils.getBedNeighbor(block).getLocation();
					} else {
						t.bed = loc;
					}
				}
				return i18n("admin_command_bed_setted").replace("%team%", t.name)
						.replace("%x%", Integer.toString(t.bed.getBlockX()))
						.replace("%y%", Integer.toString(t.bed.getBlockY()))
						.replace("%z%", Integer.toString(t.bed.getBlockZ()));
			}
		}
		return i18n("admin_command_team_is_not_exists");
	}

	private String setTeamSpawn(String name, Location loc) {
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				if (game.getPos1() == null || game.getPos2() == null) {
					return i18n("admin_command_set_pos1_pos2_first");
				}
				if (game.getWorld() != loc.getWorld()) {
					return i18n("admin_command_must_be_in_same_world");
				}
				if (!isInArea(loc, game.getPos1(), game.getPos2())) {
					return i18n("admin_command_spawn_must_be_in_area");
				}
				t.spawn = loc;
				return i18n("admin_command_team_spawn_setted").replace("%team%", t.name)
						.replace("%x%", Double.toString(t.spawn.getX())).replace("%y%", Double.toString(t.spawn.getY()))
						.replace("%z%", Double.toString(t.spawn.getZ()))
						.replace("%yaw%", Float.toString(t.spawn.getYaw()))
						.replace("%pitch%", Float.toString(t.spawn.getPitch()));
			}
		}
		return i18n("admin_command_team_is_not_exists");
	}

	private String setTeamMaxPlayers(String name, int maxPlayers) {
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				if (maxPlayers < 1) {
					return i18n("admin_command_max_players_fail");
				}

				t.maxPlayers = maxPlayers;

				return i18n("admin_command_team_maxplayers_setted").replace("%team%", t.name).replace("%maxplayers%",
						Integer.toString(t.maxPlayers));
			}
		}
		return i18n("admin_command_team_is_not_exists");
	}

	private String setTeamColor(String name, String color) {
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				TeamColor c;
				try {
					c = TeamColor.valueOf(color);
				} catch (Exception e) {
					return i18n("admin_command_invalid_color");
				}

				t.color = c;

				return i18n("admin_command_team_color_setted").replace("%team%", t.name).replace("%teamcolor%",
						t.color.chatColor + t.color.name());
			}
		}
		return i18n("admin_command_team_is_not_exists");
	}

	private String removeTeam(String name) {
		Team forRemove = null;
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				forRemove = t;
				break;
			}
		}
		if (forRemove != null) {
			game.getTeams().remove(forRemove);

			return i18n("admin_command_team_removed").replace("%team%", forRemove.name);
		}
		return i18n("admin_command_team_is_not_exists");
	}

	private String addTeam(String name, String color, int maxPlayers) {
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				return i18n("admin_command_team_is_already_exists");
			}
		}
		TeamColor c;
		try {
			c = TeamColor.valueOf(color);
		} catch (Exception e) {
			return i18n("admin_command_invalid_color");
		}

		if (maxPlayers < 1) {
			return i18n("admin_command_max_players_fail");
		}

		Team team = new Team();
		team.name = name;
		team.color = c;
		team.maxPlayers = maxPlayers;
		game.getTeams().add(team);

		return i18n("admin_command_team_created").replace("%team%", team.name)
				.replace("%teamcolor%", team.color.chatColor + team.color.name())
				.replace("%maxplayers%", Integer.toString(team.maxPlayers));
	}

	private String resetAllSpawners() {
		game.getSpawners().clear();
		return i18n("admin_command_spawners_reseted").replace("%arena%", game.getName());
	}

	private String addSpawner(String type, Location loc) {
		if (game.getPos1() == null || game.getPos2() == null) {
			return i18n("admin_command_set_pos1_pos2_first");
		}
		if (game.getWorld() != loc.getWorld()) {
			return i18n("admin_command_must_be_in_same_world");
		}
		if (!isInArea(loc, game.getPos1(), game.getPos2())) {
			return i18n("admin_command_spawn_must_be_in_area");
		}
		loc.setYaw(0);
		loc.setPitch(0);
		ItemSpawnerType spawnerType = Main.getSpawnerType(type);
		if (spawnerType != null) {
			game.getSpawners().add(new ItemSpawner(loc, spawnerType));
			return i18n("admin_command_spawner_added").replace("%resource%", spawnerType.getItemName())
					.replace("%x%", Integer.toString(loc.getBlockX())).replace("%y%", Integer.toString(loc.getBlockY()))
					.replace("%z%", Integer.toString(loc.getBlockZ()));
		} else {
			return i18n("admin_command_invalid_spawner_type");
		}
	}

	public String addStore(Location loc) {
		if (game.getPos1() == null || game.getPos2() == null) {
			return i18n("admin_command_set_pos1_pos2_first");
		}
		if (game.getWorld() != loc.getWorld()) {
			return i18n("admin_command_must_be_in_same_world");
		}
		if (!isInArea(loc, game.getPos1(), game.getPos2())) {
			return i18n("admin_command_spawn_must_be_in_area");
		}
		String location = loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
		if (villagerstores.containsKey(location)) {
			return i18n("admin_command_store_already_exists");
		}
		villagerstores.put(location, loc);
		return i18n("admin_command_store_added").replace("%x%", Double.toString(loc.getX()))
				.replace("%y%", Double.toString(loc.getY())).replace("%z%", Double.toString(loc.getZ()))
				.replace("%yaw%", Float.toString(loc.getYaw())).replace("%pitch%", Float.toString(loc.getPitch()));
	}

	public String removeStore(Location loc) {
		if (game.getWorld() != loc.getWorld()) {
			return i18n("admin_command_must_be_in_same_world");
		}
		String location = loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
		if (!villagerstores.containsKey(location)) {
			return i18n("admin_command_store_not_exists");
		}
		villagerstores.remove(location);
		return i18n("admin_command_store_removed").replace("%x%", Double.toString(loc.getX()))
				.replace("%y%", Double.toString(loc.getY())).replace("%z%", Double.toString(loc.getZ()))
				.replace("%yaw%", Float.toString(loc.getYaw())).replace("%pitch%", Float.toString(loc.getPitch()));
	}

	public String setLobbySpawn(Location loc) {
		game.setLobbySpawn(loc);
		return i18n("admin_command_lobby_spawn_setted").replace("%x%", Double.toString(loc.getX()))
				.replace("%y%", Double.toString(loc.getY())).replace("%z%", Double.toString(loc.getZ()))
				.replace("%yaw%", Float.toString(loc.getYaw())).replace("%pitch%", Float.toString(loc.getPitch()));
	}

	public String setSpecSpawn(Location loc) {
		if (game.getPos1() == null || game.getPos2() == null) {
			return i18n("admin_command_set_pos1_pos2_first");
		}
		if (game.getWorld() != loc.getWorld()) {
			return i18n("admin_command_must_be_in_same_world");
		}
		if (!isInArea(loc, game.getPos1(), game.getPos2())) {
			return i18n("admin_command_spawn_must_be_in_area");
		}
		game.setSpecSpawn(loc);
		return i18n("admin_command_spec_spawn_setted").replace("%x%", Double.toString(loc.getX()))
				.replace("%y%", Double.toString(loc.getY())).replace("%z%", Double.toString(loc.getZ()))
				.replace("%yaw%", Float.toString(loc.getYaw())).replace("%pitch%", Float.toString(loc.getPitch()));
	}

	public String setPos1(Location loc) {
		if (game.getWorld() == null) {
			game.setWorld(loc.getWorld());
		}
		if (game.getWorld() != loc.getWorld()) {
			return i18n("admin_command_must_be_in_same_world");
		}
		if (game.getPos2() != null) {
			if (Math.abs(game.getPos2().getBlockY() - loc.getBlockY()) <= 5) {
				return i18n("admin_command_pos1_pos2_difference_must_be_higher");
			}
		}
		game.setPos1(loc);
		return i18n("admin_command_pos1_setted").replace("%arena%", game.getName())
				.replace("%x%", Integer.toString(loc.getBlockX())).replace("%y%", Integer.toString(loc.getBlockY()))
				.replace("%z%", Integer.toString(loc.getBlockZ()));
	}

	public String setPos2(Location loc) {
		if (game.getWorld() == null) {
			game.setWorld(loc.getWorld());
		}
		if (game.getWorld() != loc.getWorld()) {
			return i18n("admin_command_must_be_in_same_world");
		}
		if (game.getPos1() != null) {
			if (Math.abs(game.getPos1().getBlockY() - loc.getBlockY()) <= 5) {
				return i18n("admin_command_pos1_pos2_difference_must_be_higher");
			}
		}
		game.setPos2(loc);
		return i18n("admin_command_pos2_setted").replace("%arena%", game.getName())
				.replace("%x%", Integer.toString(loc.getBlockX())).replace("%y%", Integer.toString(loc.getBlockY()))
				.replace("%z%", Integer.toString(loc.getBlockZ()));
	}

	public String setPauseCountdown(int countdown) {
		if (countdown >= 10 && countdown <= 600) {
			game.setPauseCountdown(countdown);
			return i18n("admin_command_pausecontdown_setted").replace("%countdown%", Integer.toString(countdown));
		}
		return i18n("admin_command_invalid_countdown");
	}

	public String setGameTime(int countdown) {
		if (countdown >= 10 && countdown <= 3600) {
			game.setGameTime(countdown);
			return i18n("admin_command_gametime_setted").replace("%time%", Integer.toString(countdown));
		}
		return i18n("admin_command_invalid_countdown2");
	}

	public static boolean isInArea(Location l, Location p1, Location p2) {
		Location min = new Location(p1.getWorld(), Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()),
				Math.min(p1.getZ(), p2.getZ()));
		Location max = new Location(p1.getWorld(), Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()),
				Math.max(p1.getZ(), p2.getZ()));
		return (min.getX() <= l.getX() && min.getY() <= l.getY() && min.getZ() <= l.getZ() && max.getX() >= l.getX()
				&& max.getY() >= l.getY() && max.getZ() >= l.getZ());
	}

	public static boolean isChunkInArea(Chunk l, Location p1, Location p2) {
		Chunk min = new Location(p1.getWorld(), Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()),
				Math.min(p1.getZ(), p2.getZ())).getChunk();
		Chunk max = new Location(p1.getWorld(), Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()),
				Math.max(p1.getZ(), p2.getZ())).getChunk();
		return (min.getX() <= l.getX() && min.getZ() <= l.getZ() && max.getX() >= l.getX() && max.getZ() >= l.getZ());
	}
}
