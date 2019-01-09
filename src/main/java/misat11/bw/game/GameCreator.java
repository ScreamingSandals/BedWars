package misat11.bw.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.entity.Player;

import misat11.bw.Main;
import misat11.bw.utils.BedUtils;
import misat11.bw.utils.I18n;

public class GameCreator {

	private Game game;
	private HashMap<String, Location> villagerstores = new HashMap<String, Location>();

	public GameCreator(Game game) {
		this.game = game;
		List<GameStore> gs = game.getGameStores();
		if (!gs.isEmpty()) {
			for (GameStore store : gs) {
				villagerstores.put(store.loc.getBlockX() + ";" + store.loc.getBlockY() + ";" + store.loc.getBlockZ(), store.loc);
			}
		}
	}
	
	public Game getGame() {
		return game;
	}

	public void cmd(Player player, String action, String[] args) {
		CommandResponse response = null;
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
						response = setTeamMaxPlayers(args[1], Integer.parseInt(args[3]));
					}
				} else if (args[0].equalsIgnoreCase("spawn")) {
					response = setTeamSpawn(args[1], player.getLocation());
				} else if (args[0].equalsIgnoreCase("bed")) {
					response = setTeamBed(args[1], player.getTargetBlock(null, 5));
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
		} else if (action.equalsIgnoreCase("save")) {
			List<GameStore> gamestores = new ArrayList<GameStore>();
			for (Map.Entry<String, Location> vloc : villagerstores.entrySet()) {
				gamestores.add(new GameStore(vloc.getValue()));
			}
			game.setGameStores(gamestores);
			if (game.getTeams().size() < 2) {
				response = CommandResponse.NEED_MIN_2_TEAMS;
			} else {
				game.saveToConfig();
				game.start();
				Main.addGame(game);
				response = CommandResponse.SAVED_AND_STARTED;
			}
		}

		if (response == null) {
			response = CommandResponse.UNKNOWN_COMMAND;
		}
		player.sendMessage(response.i18n());
	}

	private CommandResponse setTeamBed(String name, Block block) {
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				Location loc = block.getLocation();
				if (game.getPos1() == null || game.getPos2() == null) {
					return CommandResponse.SET_POS1_POS2_FIRST;
				}
				if (game.getWorld() != loc.getWorld()) {
					return CommandResponse.MUST_BE_IN_SAME_WORLD;
				}
				if (!isInArea(loc, game.getPos1(), game.getPos2())) {
					return CommandResponse.SPAWN_MUST_BE_IN_MAIN_AREA;
				}
				if (!(block.getBlockData() instanceof Bed)) {
					return CommandResponse.BLOCK_IS_NOT_BED;
				}
				Bed bed = (Bed) block.getBlockData();
				if (bed.getPart() != Part.HEAD) {
					t.bed = BedUtils.getBedNeighbor(block).getLocation();
				} else {
					t.bed = loc;
				}
				return CommandResponse.SUCCESS;
			}
		}
		return CommandResponse.TEAM_IS_NOT_EXISTS;
	}

	private CommandResponse setTeamSpawn(String name, Location loc) {
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				if (game.getPos1() == null || game.getPos2() == null) {
					return CommandResponse.SET_POS1_POS2_FIRST;
				}
				if (game.getWorld() != loc.getWorld()) {
					return CommandResponse.MUST_BE_IN_SAME_WORLD;
				}
				if (!isInArea(loc, game.getPos1(), game.getPos2())) {
					return CommandResponse.SPAWN_MUST_BE_IN_MAIN_AREA;
				}
				t.spawn = loc;
				return CommandResponse.SUCCESS;
			}
		}
		return CommandResponse.TEAM_IS_NOT_EXISTS;
	}

	private CommandResponse setTeamMaxPlayers(String name, int maxPlayers) {
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				if (maxPlayers < 1) {
					return CommandResponse.MAX_PLAYERS_FAIL;
				}
				
				t.maxPlayers = maxPlayers;
				
				return CommandResponse.SUCCESS;
			}
		}
		return CommandResponse.TEAM_IS_NOT_EXISTS;
	}

	private CommandResponse setTeamColor(String name, String color) {
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				TeamColor c;
				try {
					c = TeamColor.valueOf(color);
				} catch (Exception e) {
					return CommandResponse.INVALID_COLOR;
				}
				
				t.color = c;
				
				return CommandResponse.SUCCESS;
			}
		}
		return CommandResponse.TEAM_IS_NOT_EXISTS;
	}

	private CommandResponse removeTeam(String name) {
		Team forRemove = null;
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				forRemove = t;
				break;
			}
		}
		if (forRemove != null) {
			game.getTeams().remove(forRemove);
			return CommandResponse.SUCCESS;
		}
		return CommandResponse.TEAM_IS_NOT_EXISTS;
	}

	private CommandResponse addTeam(String name, String color, int maxPlayers) {
		for (Team t : game.getTeams()) {
			if (t.name.equals(name)) {
				return CommandResponse.TEAM_IS_ALREADY_EXISTS;
			}
		}
		TeamColor c;
		try {
			c = TeamColor.valueOf(color);
		} catch (Exception e) {
			return CommandResponse.INVALID_COLOR;
		}
		
		if (maxPlayers < 1) {
			return CommandResponse.MAX_PLAYERS_FAIL;
		}
		
		Team team = new Team();
		team.name = name;
		team.color = c;
		team.maxPlayers = maxPlayers;
		game.getTeams().add(team);
		
		return CommandResponse.SUCCESS;
	}

	private CommandResponse resetAllSpawners() {
		game.getSpawners().clear();
		return CommandResponse.SUCCESS;
	}

	private CommandResponse addSpawner(String type, Location loc) {
		if (game.getPos1() == null || game.getPos2() == null) {
			return CommandResponse.SET_POS1_POS2_FIRST;
		}
		if (game.getWorld() != loc.getWorld()) {
			return CommandResponse.MUST_BE_IN_SAME_WORLD;
		}
		if (!isInArea(loc, game.getPos1(), game.getPos2())) {
			return CommandResponse.SPAWN_MUST_BE_IN_MAIN_AREA;
		}
		loc.setYaw(0);
		loc.setPitch(0);
		if (type.equalsIgnoreCase("bronze")) {
			game.getSpawners().add(new ItemSpawner(loc, ItemSpawnerType.BRONZE));
		} else if (type.equalsIgnoreCase("iron")) {
			game.getSpawners().add(new ItemSpawner(loc, ItemSpawnerType.IRON));
		} else if (type.equalsIgnoreCase("gold")) {
			game.getSpawners().add(new ItemSpawner(loc, ItemSpawnerType.GOLD));
		} else {
			return CommandResponse.INVALID_SPAWNER_TYPE;
		}
		return CommandResponse.SUCCESS;
	}

	public CommandResponse addStore(Location loc) {
		if (game.getPos1() == null || game.getPos2() == null) {
			return CommandResponse.SET_POS1_POS2_FIRST;
		}
		if (game.getWorld() != loc.getWorld()) {
			return CommandResponse.MUST_BE_IN_SAME_WORLD;
		}
		if (!isInArea(loc, game.getPos1(), game.getPos2())) {
			return CommandResponse.SPAWN_MUST_BE_IN_MAIN_AREA;
		}
		String location = loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
		if (villagerstores.containsKey(location)) {
			return CommandResponse.STORE_EXISTS;
		}
		villagerstores.put(location, loc);
		return CommandResponse.SUCCESS;
	}

	public CommandResponse removeStore(Location loc) {
		if (game.getWorld() != loc.getWorld()) {
			return CommandResponse.MUST_BE_IN_SAME_WORLD;
		}
		String location = loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
		if (!villagerstores.containsKey(location)) {
			return CommandResponse.STORE_NOT_EXISTS;
		}
		villagerstores.remove(location);
		return CommandResponse.SUCCESS;
	}

	public CommandResponse setLobbySpawn(Location loc) {
		game.setLobbySpawn(loc);
		return CommandResponse.SUCCESS;
	}

	public CommandResponse setSpecSpawn(Location loc) {
		if (game.getPos1() == null || game.getPos2() == null) {
			return CommandResponse.SET_POS1_POS2_FIRST;
		}
		if (game.getWorld() != loc.getWorld()) {
			return CommandResponse.MUST_BE_IN_SAME_WORLD;
		}
		if (!isInArea(loc, game.getPos1(), game.getPos2())) {
			return CommandResponse.SPAWN_MUST_BE_IN_MAIN_AREA;
		}
		game.setSpecSpawn(loc);
		return CommandResponse.SUCCESS;
	}

	public CommandResponse setPos1(Location loc) {
		if (game.getWorld() == null) {
			game.setWorld(loc.getWorld());
		}
		if (game.getWorld() != loc.getWorld()) {
			return CommandResponse.MUST_BE_IN_SAME_WORLD;
		}
		if (game.getPos2() != null) {
			if (Math.abs(game.getPos2().getBlockY() - loc.getBlockY()) <= 5) {
				return CommandResponse.POS1_POS2_DIFFERENCE_MUST_BE_HIGHER;
			}
		}
		game.setPos1(loc);
		return CommandResponse.SUCCESS;
	}

	public CommandResponse setPos2(Location loc) {
		if (game.getWorld() == null) {
			game.setWorld(loc.getWorld());
		}
		if (game.getWorld() != loc.getWorld()) {
			return CommandResponse.MUST_BE_IN_SAME_WORLD;
		}
		if (game.getPos1() != null) {
			if (Math.abs(game.getPos1().getBlockY() - loc.getBlockY()) <= 5) {
				return CommandResponse.POS1_POS2_DIFFERENCE_MUST_BE_HIGHER;
			}
		}
		game.setPos2(loc);
		return CommandResponse.SUCCESS;
	}

	public CommandResponse setPauseCountdown(int countdown) {
		if (countdown >= 10 && countdown <= 600) {
			game.setPauseCountdown(countdown);
			return CommandResponse.SUCCESS;
		}
		return CommandResponse.INVALID_COUNTDOWN;
	}

	public CommandResponse setGameTime(int countdown) {
		if (countdown >= 10 && countdown <= 3600) {
			game.setGameTime(countdown);
			return CommandResponse.SUCCESS;
		}
		return CommandResponse.INVALID_COUNTDOWN2;
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

	enum CommandResponse {
		SUCCESS("admin_command_success"), MUST_BE_IN_SAME_WORLD("admin_command_must_be_in_same_world"),
		INVALID_COUNTDOWN("admin_command_invalid_countdown"), INVALID_COUNTDOWN2("admin_command_invalid_countdown2"),
		SPAWN_MUST_BE_IN_MAIN_AREA("admin_command_spawn_must_be_in_area"),
		SET_POS1_POS2_FIRST("admin_command_set_pos1_pos2_first"),
		POS1_POS2_DIFFERENCE_MUST_BE_HIGHER("admin_command_pos1_pos2_difference_must_be_higher"),
		UNKNOWN_COMMAND("unknown_command"), SAVED_AND_STARTED("admin_command_game_saved_and_started"),
		STORE_EXISTS("admin_command_store_already_exists"), STORE_NOT_EXISTS("admin_command_store_not_exists"),
		INVALID_SPAWNER_TYPE("admin_command_invalid_spawner_type"), INVALID_COLOR("admin_command_invalid_color"),
		BLOCK_IS_NOT_BED("admin_command_block_is_not_bed"), MAX_PLAYERS_FAIL("admin_command_max_players_fail"),
		NEED_MIN_2_TEAMS("admin_command_need_min_2_teems"), SPAWNERS_NEEDED("admin_command_spawners_needed"),
		TEAM_IS_NOT_EXISTS("admin_command_team_is_not_exists"),
		TEAM_IS_ALREADY_EXISTS("admin_command_team_is_already_exists");

		private final String msg;

		private CommandResponse(String msg) {
			this.msg = msg;
		}

		public String i18n() {
			return I18n._(msg, false);
		}
	}
}
