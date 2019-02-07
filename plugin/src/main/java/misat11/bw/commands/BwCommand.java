package misat11.bw.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import misat11.bw.Main;
import misat11.bw.api.GameStore;
import misat11.bw.game.Game;
import misat11.bw.game.GameCreator;
import misat11.bw.game.ItemSpawner;
import misat11.bw.game.Team;
import misat11.bw.game.TeamColor;

import static misat11.bw.utils.I18n.i18n;

public class BwCommand implements CommandExecutor, TabCompleter {
	
	public static final String ADMIN_PERMISSION = "misat11.bw.admin";

	public HashMap<String, GameCreator> gc = new HashMap<String, GameCreator>();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 0) {
				sendHelp(player);
			} else if (args.length > 0) {
				if (args[0].equalsIgnoreCase("help")) {
					sendHelp(player);
				} else if (args[0].equalsIgnoreCase("join")) {
					if (args.length > 1) {
						String arenaN = args[1];
						if (Main.isGameExists(arenaN)) {
							Main.getGame(arenaN).joinToGame(player);
						} else {
							player.sendMessage(i18n("no_arena_found"));
						}
					} else {
						player.sendMessage(i18n("usage_bw_join"));
					}
				} else if (args[0].equalsIgnoreCase("leave")) {
					if (Main.isPlayerInGame(player)) {
						Main.getPlayerGameProfile(player).changeGame(null);
					} else {
						player.sendMessage(i18n("you_arent_in_game"));
					}
				} else if (args[0].equalsIgnoreCase("list")) {
					player.sendMessage(i18n("list_header"));
					Main.sendGameListInfo(player);
				} else if (args[0].equalsIgnoreCase("admin")) {
					if (player.hasPermission(ADMIN_PERMISSION)) {
						if (args.length >= 3) {
							String arN = args[1];
							if (args[2].equalsIgnoreCase("info")) {
								if (Main.isGameExists(arN)) {
									Game game = Main.getGame(arN);
									player.sendMessage(i18n("arena_info_header"));
									
									player.sendMessage(i18n("arena_info_name", false).replace("%name%", game.getName()));
									String status = i18n("arena_info_status", false);
									switch (game.getStatus()) {
									case DISABLED:
										if (gc.containsKey(arN)) {
											status = status.replace("%status%", i18n("arena_info_status_disabled_in_edit", false));
										} else {
											status = status.replace("%status%", i18n("arena_info_status_disabled", false));
										}
										break;
									case REBUILDING:
										status = status.replace("%status%", i18n("arena_info_status_rebuilding", false));
										break;
									case RUNNING:
										status = status.replace("%status%", i18n("arena_info_status_running", false));
										break;
									case WAITING:
										status = status.replace("%status%", i18n("arena_info_status_waiting", false));
										break;
									}
									player.sendMessage(status);
									
									player.sendMessage(i18n("arena_info_world", false).replace("%world%", game.getWorld().getName()));
									
									Location loc_pos1 = game.getPos1();
									String pos1 = i18n("arena_info_pos1", false)
											.replace("%x%", Double.toString(loc_pos1.getX()))
											.replace("%y%", Double.toString(loc_pos1.getY()))
											.replace("%z%", Double.toString(loc_pos1.getZ()))
											.replace("%yaw%", Float.toString(loc_pos1.getYaw()))
											.replace("%pitch%", Float.toString(loc_pos1.getPitch()))
											.replace("%world%", loc_pos1.getWorld().getName());
									
									player.sendMessage(pos1);
									
									Location loc_pos2 = game.getPos2();
									String pos2 = i18n("arena_info_pos2", false)
											.replace("%x%", Double.toString(loc_pos2.getX()))
											.replace("%y%", Double.toString(loc_pos2.getY()))
											.replace("%z%", Double.toString(loc_pos2.getZ()))
											.replace("%yaw%", Float.toString(loc_pos2.getYaw()))
											.replace("%pitch%", Float.toString(loc_pos2.getPitch()))
											.replace("%world%", loc_pos2.getWorld().getName());
									
									player.sendMessage(pos2);
									
									Location loc_spec = game.getSpecSpawn();
									String spec = i18n("arena_info_spec", false)
											.replace("%x%", Double.toString(loc_spec.getX()))
											.replace("%y%", Double.toString(loc_spec.getY()))
											.replace("%z%", Double.toString(loc_spec.getZ()))
											.replace("%yaw%", Float.toString(loc_spec.getYaw()))
											.replace("%pitch%", Float.toString(loc_spec.getPitch()))
											.replace("%world%", loc_spec.getWorld().getName());
									
									player.sendMessage(spec);
									
									Location loc_lobby = game.getLobbySpawn();
									String lobby = i18n("arena_info_lobby", false)
											.replace("%x%", Double.toString(loc_lobby.getX()))
											.replace("%y%", Double.toString(loc_lobby.getY()))
											.replace("%z%", Double.toString(loc_lobby.getZ()))
											.replace("%yaw%", Float.toString(loc_lobby.getYaw()))
											.replace("%pitch%", Float.toString(loc_lobby.getPitch()))
											.replace("%world%", loc_lobby.getWorld().getName());
									
									player.sendMessage(lobby);
									player.sendMessage(i18n("arena_info_min_players", false).replace("%minplayers%", Integer.toString(game.getMinPlayers())));
									player.sendMessage(i18n("arena_info_lobby_countdown", false).replace("%time%", Integer.toString(game.getPauseCountdown())));
									player.sendMessage(i18n("arena_info_game_time", false).replace("%time%", Integer.toString(game.getGameTime())));
									
									player.sendMessage(i18n("arena_info_teams", false));
									for (Team team : game.getTeams()) {
										player.sendMessage(i18n("arena_info_team", false)
												.replace("%team%", team.color.chatColor.toString() + team.name)
												.replace("%maxplayers%", Integer.toString(team.maxPlayers)));
										
										Location loc_spawn = team.spawn;
										String spawn = i18n("arena_info_team_spawn", false)
												.replace("%x%", Double.toString(loc_spawn.getX()))
												.replace("%y%", Double.toString(loc_spawn.getY()))
												.replace("%z%", Double.toString(loc_spawn.getZ()))
												.replace("%yaw%", Float.toString(loc_spawn.getYaw()))
												.replace("%pitch%", Float.toString(loc_spawn.getPitch()))
												.replace("%world%", loc_spawn.getWorld().getName());
										
										player.sendMessage(spawn);

										Location loc_target = team.bed;
										String target = i18n("arena_info_team_target", false)
												.replace("%x%", Double.toString(loc_target.getX()))
												.replace("%y%", Double.toString(loc_target.getY()))
												.replace("%z%", Double.toString(loc_target.getZ()))
												.replace("%yaw%", Float.toString(loc_target.getYaw()))
												.replace("%pitch%", Float.toString(loc_target.getPitch()))
												.replace("%world%", loc_target.getWorld().getName());
										
										player.sendMessage(target);
									}
									
									player.sendMessage(i18n("arena_info_spawners", false));
									for (ItemSpawner spawner : game.getSpawners()) {

										Location loc_spawner = spawner.loc;
										String spawnerM = i18n("arena_info_spawner", false)
												.replace("%resource%", spawner.type.getItemName())
												.replace("%x%", Double.toString(loc_spawner.getX()))
												.replace("%y%", Double.toString(loc_spawner.getY()))
												.replace("%z%", Double.toString(loc_spawner.getZ()))
												.replace("%yaw%", Float.toString(loc_spawner.getYaw()))
												.replace("%pitch%", Float.toString(loc_spawner.getPitch()))
												.replace("%world%", loc_spawner.getWorld().getName());
										
										player.sendMessage(spawnerM);
									}
									
									player.sendMessage(i18n("arena_info_villagers", false));
									for (GameStore store : game.getGameStores()) {

										Location loc_store = store.getStoreLocation();
										String storeM = i18n("arena_info_villager", false)
												.replace("%x%", Double.toString(loc_store.getX()))
												.replace("%y%", Double.toString(loc_store.getY()))
												.replace("%z%", Double.toString(loc_store.getZ()))
												.replace("%yaw%", Float.toString(loc_store.getYaw()))
												.replace("%pitch%", Float.toString(loc_store.getPitch()))
												.replace("%world%", loc_store.getWorld().getName());
										
										player.sendMessage(storeM);
									}
								} else {
									player.sendMessage(i18n("no_arena_found"));
								}
							} else if (args[2].equalsIgnoreCase("add")) {
								if (Main.isGameExists(arN)) {
									player.sendMessage(i18n("allready_exists"));
								} else if (gc.containsKey(arN)) {
									player.sendMessage(i18n("allready_working_on_it"));
								} else {
									GameCreator creator = new GameCreator(Game.createGame(arN));
									gc.put(arN, creator);
									player.sendMessage(i18n("arena_added"));
								}
							} else if (args[2].equalsIgnoreCase("remove")) {
								if (Main.isGameExists(arN)) {
									if (!gc.containsKey(arN)) {
										player.sendMessage(i18n("arena_must_be_in_edit_mode"));
									} else {
										gc.remove(arN);
										new File(Main.getInstance().getDataFolder(), "arenas/" + arN + ".yml").delete();
										Main.removeGame(Main.getGame(arN));
										player.sendMessage(i18n("arena_removed"));
									}
								} else if (gc.containsKey(arN)) {
									gc.remove(arN);
									player.sendMessage(i18n("arena_removed"));
								} else {
									player.sendMessage(i18n("no_arena_found"));
								}
							} else if (args[2].equalsIgnoreCase("edit")) {
								if (Main.isGameExists(arN)) {
									Game game = Main.getGame(arN);
									game.stop();
									gc.put(arN, new GameCreator(game));
									player.sendMessage(i18n("arena_switched_to_edit"));
								} else {
									player.sendMessage(i18n("no_arena_found"));
								}
							} else {
								if (gc.containsKey(arN)) {
									List<String> nargs = new ArrayList<String>();
									int lid = 0;
									for (String arg : args) {
										if (lid >= 3) {
											nargs.add(arg);
										}
										lid++;
									}
									boolean isArenaSaved = gc.get(arN).cmd(player, args[2], nargs.toArray(new String[nargs.size()]));
									if (args[2].equalsIgnoreCase("save") && isArenaSaved) {
										gc.remove(arN);
									}
								} else {
									player.sendMessage(i18n("arena_not_in_edit"));
								}
							}
						} else {
							player.sendMessage(i18n("usage_bw_admin"));
						}
					} else {
						player.sendMessage(i18n("no_permissions"));
					}
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (player.hasPermission(ADMIN_PERMISSION)) {
						Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
						Bukkit.getServer().getPluginManager().enablePlugin(Main.getInstance());
						player.sendMessage("Plugin reloaded!");
					} else {
						player.sendMessage(i18n("no_permissions"));
					}
				} else {
					player.sendMessage(i18n("unknown_command"));
				}
			}
		} else {
			sender.sendMessage("BW commands cannot be executed from console!");
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> completionList = new ArrayList<>();
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				List<String> cmds = Arrays.asList("join", "leave", "list");
				if (player.hasPermission(ADMIN_PERMISSION)) {
					cmds = Arrays.asList("join", "leave", "list", "admin", "reload");
				}
				StringUtil.copyPartialMatches(args[0], cmds, completionList);
			}
			if (args.length > 1) {
				if (args[0].equalsIgnoreCase("join")) {
					List<String> arenas = Main.getGameNames();
					StringUtil.copyPartialMatches(args[1], arenas, completionList);
				} else if (args[0].equalsIgnoreCase("admin") && player.hasPermission(ADMIN_PERMISSION)) {
					if (args.length == 2) {
						List<String> arenas = Main.getGameNames();
						for (String arena : gc.keySet()) {
							arenas.add(arena);
						}
						StringUtil.copyPartialMatches(args[1], arenas, completionList);
					} else if (args.length == 3) {
						List<String> cmds = Arrays.asList("add", "lobby", "spec", "pos1", "pos2", "pausecountdown",
								"team", "spawner", "time", "store", "save", "remove", "edit", "jointeam", "minplayers", "info");
						StringUtil.copyPartialMatches(args[2], cmds, completionList);
					} else if (args[2].equalsIgnoreCase("pausecountdown") && args.length == 4) {
						StringUtil.copyPartialMatches(args[3], Arrays.asList("30", "60"), completionList);
					} else if (args[2].equalsIgnoreCase("time") && args.length == 4) {
						StringUtil.copyPartialMatches(args[3], Arrays.asList("180", "300", "600"), completionList);
					} else if (args[2].equalsIgnoreCase("minplayers") && args.length == 4) {
						StringUtil.copyPartialMatches(args[3], Arrays.asList("2", "3", "4", "5"), completionList);
					} else if (args[2].equalsIgnoreCase("store")) {
						if (args.length == 4) {
							List<String> cmds = Arrays.asList("add", "remove");
							StringUtil.copyPartialMatches(args[3], cmds, completionList);
						}
					} else if (args[2].equalsIgnoreCase("spawner")) {
						if (args.length == 4) {
							List<String> cmds = Arrays.asList("add", "reset");
							StringUtil.copyPartialMatches(args[3], cmds, completionList);
						}
						if (args.length == 5) {
							List<String> cmds = Main.getAllSpawnerTypes();
							StringUtil.copyPartialMatches(args[4], cmds, completionList);
						}
					} else if (args[2].equalsIgnoreCase("team")) {
						if (args.length == 4) {
							List<String> cmds = Arrays.asList("add", "color", "maxplayers", "spawn", "bed", "remove");
							StringUtil.copyPartialMatches(args[3], cmds, completionList);
						}
						if (args.length == 5) {
							if (gc.containsKey(args[1])) {
								List<String> cmds = new ArrayList<String>();
								for (Team t : gc.get(args[1]).getGame().getTeams()) {
									cmds.add(t.name);
								}
								StringUtil.copyPartialMatches(args[4], cmds, completionList);
							}
						}
						if (args.length == 6) {
							if (args[3].equalsIgnoreCase("add") || args[3].equalsIgnoreCase("color")) {
								List<String> colors = new ArrayList<String>();
								for (TeamColor en : TeamColor.values()) {
									colors.add(en.toString());
								}
								StringUtil.copyPartialMatches(args[5], colors, completionList);
							} else if (args[3].equalsIgnoreCase("maxplayers")) {
								StringUtil.copyPartialMatches(args[5], Arrays.asList("1", "2", "4", "8"),
										completionList);
							}
						}
						if (args.length == 7 && args[3].equalsIgnoreCase("add")) {
							StringUtil.copyPartialMatches(args[6], Arrays.asList("1", "2", "4", "8"),
									completionList);
						}
					} else if (args[2].equalsIgnoreCase("jointeam")) {
						if (gc.containsKey(args[1])) {
							List<String> cmds = new ArrayList<String>();
							for (Team t : gc.get(args[1]).getGame().getTeams()) {
								cmds.add(t.name);
							}
							StringUtil.copyPartialMatches(args[3], cmds, completionList);
						}
					}
				}
			}
		}
		return completionList;
	}

	public void sendHelp(Player player) {
		player.sendMessage(i18n("help_title", false).replace("%version%", Main.getVersion()));
		player.sendMessage(i18n("help_bw_join", false));
		player.sendMessage(i18n("help_bw_leave", false));
		player.sendMessage(i18n("help_bw_list", false));
		if (player.hasPermission(ADMIN_PERMISSION)) {
			player.sendMessage(i18n("help_bw_admin_info", false));
			player.sendMessage(i18n("help_bw_admin_add", false));
			player.sendMessage(i18n("help_bw_admin_lobby", false));
			player.sendMessage(i18n("help_bw_admin_spec", false));
			player.sendMessage(i18n("help_bw_admin_pos1", false));
			player.sendMessage(i18n("help_bw_admin_pos2", false));
			player.sendMessage(i18n("help_bw_admin_pausecountdown", false));
			player.sendMessage(i18n("help_bw_admin_minplayers", false));
			player.sendMessage(i18n("help_bw_admin_time", false));
			player.sendMessage(i18n("help_bw_admin_team_add", false));
			player.sendMessage(i18n("help_bw_admin_team_color", false));
			player.sendMessage(i18n("help_bw_admin_team_maxplayers", false));
			player.sendMessage(i18n("help_bw_admin_team_spawn", false));
			player.sendMessage(i18n("help_bw_admin_team_bed", false));
			player.sendMessage(i18n("help_bw_admin_jointeam", false));
			player.sendMessage(i18n("help_bw_admin_spawner_add", false));
			player.sendMessage(i18n("help_bw_admin_spawner_reset", false));
			player.sendMessage(i18n("help_bw_admin_store_add", false));
			player.sendMessage(i18n("help_bw_admin_store_remove", false));
			player.sendMessage(i18n("help_bw_admin_remove", false));
			player.sendMessage(i18n("help_bw_admin_edit", false));
			player.sendMessage(i18n("help_bw_admin_save", false));
			player.sendMessage(i18n("help_bw_reload", false));
		}
	}

}
