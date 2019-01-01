package misat11.bw.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import misat11.bw.Main;
import misat11.bw.game.Game;
import misat11.bw.game.GameCreator;
import misat11.bw.game.Team;
import misat11.bw.game.TeamColor;
import misat11.bw.utils.I18n;

public class BwCommand implements CommandExecutor, TabCompleter {

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
							player.sendMessage(I18n._("no_arena_found"));
						}
					} else {
						player.sendMessage(I18n._("usage_bw_join"));
					}
				} else if (args[0].equalsIgnoreCase("leave")) {
					if (Main.isPlayerInGame(player)) {
						Main.getPlayerGameProfile(player).changeGame(null);
					} else {
						player.sendMessage(I18n._("you_arent_in_game"));
					}
				} else if (args[0].equalsIgnoreCase("list")) {
					player.sendMessage(I18n._("list_header"));
					Main.sendGameListInfo(player);
				} else if (args[0].equalsIgnoreCase("admin")) {
					if (player.hasPermission("misat11.bw.admin")) {
						if (args.length >= 3) {
							String arN = args[1];
							if (args[2].equalsIgnoreCase("add")) {
								if (Main.isGameExists(arN)) {
									player.sendMessage(I18n._("allready_exists"));
								} else if (gc.containsKey(arN)) {
									player.sendMessage(I18n._("allready_working_on_it"));
								} else {
									GameCreator creator = new GameCreator(Game.createGame(arN));
									gc.put(arN, creator);
									player.sendMessage(I18n._("arena_added"));
								}
							} else if (args[2].equalsIgnoreCase("remove")) {
								if (Main.isGameExists(arN)) {
									if (!gc.containsKey(arN)) {
										player.sendMessage(I18n._("arena_must_be_in_edit_mode"));
									} else {
										gc.remove(arN);
										new File(Main.getInstance().getDataFolder(), "arenas/" + arN + ".yml").delete();
										Main.removeGame(Main.getGame(arN));
										player.sendMessage(I18n._("arena_removed"));
									}
								} else if (gc.containsKey(arN)) {
									gc.remove(arN);
									player.sendMessage(I18n._("arena_removed"));
								} else {
									player.sendMessage(I18n._("no_arena_found"));
								}
							} else if (args[2].equalsIgnoreCase("edit")) {
								if (Main.isGameExists(arN)) {
									Game game = Main.getGame(arN);
									game.stop();
									gc.put(arN, new GameCreator(game));
									player.sendMessage(I18n._("arena_switched_to_edit"));
								} else {
									player.sendMessage(I18n._("no_arena_found"));
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
									gc.get(arN).cmd(player, args[2], nargs.toArray(new String[nargs.size()]));
									if (args[2].equalsIgnoreCase("save")) {
										gc.remove(arN);
									}
								} else {
									player.sendMessage(I18n._("arena_not_in_edit"));
								}
							}
						} else {
							player.sendMessage(I18n._("usage_bw_admin"));
						}
					} else {
						player.sendMessage(I18n._("no_permissions"));
					}
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (player.hasPermission("misat11.bw.admin")) {
						Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
						Bukkit.getServer().getPluginManager().enablePlugin(Main.getInstance());
						player.sendMessage("Plugin reloaded!");
					} else {
						player.sendMessage(I18n._("no_permissions"));
					}
				} else {
					player.sendMessage(I18n._("unknown_command"));
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
				if (player.hasPermission("misat11.bw.admin")) {
					cmds = Arrays.asList("join", "leave", "list", "admin", "reload");
				}
				StringUtil.copyPartialMatches(args[0], cmds, completionList);
			}
			if (args.length > 1) {
				if (args[0].equalsIgnoreCase("join")) {
					List<String> arenas = Main.getGameNames();
					StringUtil.copyPartialMatches(args[1], arenas, completionList);
				} else if (args[0].equalsIgnoreCase("admin") && player.hasPermission("misat11.bw.admin")) {
					if (args.length == 2) {
						List<String> arenas = Main.getGameNames();
						for (String arena : gc.keySet()) {
							arenas.add(arena);
						}
						StringUtil.copyPartialMatches(args[1], arenas, completionList);
					} else if (args.length == 3) {
						List<String> cmds = Arrays.asList("add", "lobby", "spec", "pos1", "pos2", "pausecountdown",
								"team", "spawner", "time", "store", "save", "remove", "edit");
						StringUtil.copyPartialMatches(args[2], cmds, completionList);
					} else if (args[2].equalsIgnoreCase("pausecountdown") && args.length == 4) {
						StringUtil.copyPartialMatches(args[3], Arrays.asList("30", "60"), completionList);
					} else if (args[2].equalsIgnoreCase("time") && args.length == 4) {
						StringUtil.copyPartialMatches(args[3], Arrays.asList("180", "300", "600"), completionList);
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
							List<String> cmds = Arrays.asList("bronze", "iron", "gold");
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
								StringUtil.copyPartialMatches(args[3], Arrays.asList("1", "2", "4", "8"),
										completionList);
							}
						}
						if (args.length == 7 && args[3].equalsIgnoreCase("add")) {
							StringUtil.copyPartialMatches(args[3], Arrays.asList("1", "2", "4", "8"),
									completionList);
						}
					}
				}
			}
		}
		return completionList;
	}

	public void sendHelp(Player player) {
		player.sendMessage(I18n._("help_title", false).replace("%version%", Main.getVersion()));
		player.sendMessage(I18n._("help_bw_join", false));
		player.sendMessage(I18n._("help_bw_leave", false));
		player.sendMessage(I18n._("help_bw_list", false));
		if (player.hasPermission("misat11.bw.admin")) {
			player.sendMessage(I18n._("help_bw_admin_add", false));
			player.sendMessage(I18n._("help_bw_admin_lobby", false));
			player.sendMessage(I18n._("help_bw_admin_spec", false));
			player.sendMessage(I18n._("help_bw_admin_pos1", false));
			player.sendMessage(I18n._("help_bw_admin_pos2", false));
			player.sendMessage(I18n._("help_bw_admin_pausecountdown", false));
			player.sendMessage(I18n._("help_bw_admin_time", false));
			player.sendMessage(I18n._("help_bw_admin_team_add", false));
			player.sendMessage(I18n._("help_bw_admin_team_color", false));
			player.sendMessage(I18n._("help_bw_admin_team_maxplayers", false));
			player.sendMessage(I18n._("help_bw_admin_team_spawn", false));
			player.sendMessage(I18n._("help_bw_admin_team_bed", false));
			player.sendMessage(I18n._("help_bw_admin_spawner_add", false));
			player.sendMessage(I18n._("help_bw_admin_spawner_reset", false));
			player.sendMessage(I18n._("help_bw_admin_store_add", false));
			player.sendMessage(I18n._("help_bw_admin_store_remove", false));
			player.sendMessage(I18n._("help_bw_admin_remove", false));
			player.sendMessage(I18n._("help_bw_admin_edit", false));
			player.sendMessage(I18n._("help_bw_admin_save", false));
			player.sendMessage(I18n._("help_bw_reload", false));
		}
	}

}
