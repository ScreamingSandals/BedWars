package misat11.bw.commands;

import static misat11.lib.lang.I18n.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.StringUtil;

import misat11.bw.Main;
import misat11.bw.api.ArenaTime;
import misat11.bw.api.GameStore;
import misat11.bw.game.Game;
import misat11.bw.game.GameCreator;
import misat11.bw.game.ItemSpawner;
import misat11.bw.game.Team;
import misat11.bw.game.TeamColor;
import misat11.bw.statistics.PlayerStatistic;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BwCommand implements CommandExecutor, TabCompleter {

	public static final String ADMIN_PERMISSION = "misat11.bw.admin";
	public static final String OTHER_STATS_PERMISSION = "misat11.bw.otherstats";

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
				} else if (args[0].equalsIgnoreCase("stats")) {
					if (!Main.isPlayerStatisticsEnabled()) {
						player.sendMessage(i18n("statistics_is_disabled"));
					} else {
						if (args.length >= 2) {
							if (!player.hasPermission(OTHER_STATS_PERMISSION)
									&& !player.hasPermission(ADMIN_PERMISSION)) {
								player.sendMessage(i18n("no_permissions"));
							} else {
								String name = args[1];
								OfflinePlayer off = Main.getInstance().getServer().getPlayerExact(name);

								if (off == null) {
									player.sendMessage(i18n("statistics_player_is_not_exists"));
								} else {
									PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(off);
									if (statistic == null) {
										player.sendMessage(i18n("statistics_not_found"));
									} else {
										this.sendStats(player, statistic);
									}
								}
							}
						} else {
							PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(player);
							if (statistic == null) {
								player.sendMessage(i18n("statistics_not_found"));
							} else {
								this.sendStats(player, statistic);
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("list")) {
					player.sendMessage(i18n("list_header"));
					Main.sendGameListInfo(player);
				} else if (args[0].equalsIgnoreCase("addholo")) {
					if (player.hasPermission(ADMIN_PERMISSION)) {
						if (!Main.isHologramsEnabled()) {
							player.sendMessage(i18n("holo_not_enabled"));
						} else {
							Main.getHologramInteraction().addHologramLocation(player.getEyeLocation());
							Main.getHologramInteraction().updateHolograms();
							player.sendMessage(i18n("holo_added"));
						}
					} else {
						player.sendMessage(i18n("no_permissions"));
					}
				} else if (args[0].equalsIgnoreCase("removeholo")) {
					if (player.hasPermission(ADMIN_PERMISSION)) {
						if (!Main.isHologramsEnabled()) {
							player.sendMessage(i18n("holo_not_enabled"));
						} else {
							player.setMetadata("bw-remove-holo", new FixedMetadataValue(Main.getInstance(), true));
							player.sendMessage(i18n("click_to_holo_for_remove"));
						}
					} else {
						player.sendMessage(i18n("no_permissions"));
					}
				} else if (args[0].equalsIgnoreCase("admin")) {
					if (player.hasPermission(ADMIN_PERMISSION)) {
						if (args.length >= 3) {
							String arN = args[1];
							if (args[2].equalsIgnoreCase("info")) {
								if (Main.isGameExists(arN)) {
									Game game = Main.getGame(arN);
									if (args.length >= 4) {
										if (args[3].equalsIgnoreCase("base")) {
											player.sendMessage(i18n("arena_info_header"));

											player.sendMessage(
													i18n("arena_info_name", false).replace("%name%", game.getName()));
											String status = i18n("arena_info_status", false);
											switch (game.getStatus()) {
											case DISABLED:
												if (gc.containsKey(arN)) {
													status = status.replace("%status%",
															i18n("arena_info_status_disabled_in_edit", false));
												} else {
													status = status.replace("%status%",
															i18n("arena_info_status_disabled", false));
												}
												break;
											case REBUILDING:
												status = status.replace("%status%",
														i18n("arena_info_status_rebuilding", false));
												break;
											case RUNNING:
												status = status.replace("%status%",
														i18n("arena_info_status_running", false));
												break;
											case WAITING:
												status = status.replace("%status%",
														i18n("arena_info_status_waiting", false));
												break;
											}
											player.sendMessage(status);

											player.sendMessage(i18n("arena_info_world", false).replace("%world%",
													game.getWorld().getName()));

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
											player.sendMessage(i18n("arena_info_min_players", false)
													.replace("%minplayers%", Integer.toString(game.getMinPlayers())));
											player.sendMessage(i18n("arena_info_lobby_countdown", false)
													.replace("%time%", Integer.toString(game.getPauseCountdown())));
											player.sendMessage(i18n("arena_info_game_time", false).replace("%time%",
													Integer.toString(game.getGameTime())));

										} else if (args[3].equalsIgnoreCase("teams")) {
											player.sendMessage(i18n("arena_info_header"));

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
										} else if (args[3].equalsIgnoreCase("spawners")) {
											player.sendMessage(i18n("arena_info_header"));

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
										} else if (args[3].equalsIgnoreCase("stores")) {
											player.sendMessage(i18n("arena_info_header"));

											player.sendMessage(i18n("arena_info_villagers", false));
											for (GameStore store : game.getGameStores()) {

												Location loc_store = store.getStoreLocation();
												String storeM = i18n("arena_info_villager_pos", false)
														.replace("%x%", Double.toString(loc_store.getX()))
														.replace("%y%", Double.toString(loc_store.getY()))
														.replace("%z%", Double.toString(loc_store.getZ()))
														.replace("%yaw%", Float.toString(loc_store.getYaw()))
														.replace("%pitch%", Float.toString(loc_store.getPitch()))
														.replace("%world%", loc_store.getWorld().getName());

												player.sendMessage(storeM);

												String storeM2 = i18n("arena_info_villager_entity_type", false)
														.replace("%type%", store.getEntityType().name());
												player.sendMessage(storeM2);

												String storeM3 = i18n("arena_info_villager_shop", false).replace(
														"%bool%",
														store.getShopFile() != null
																? i18n("arena_info_config_true", false)
																: i18n("arena_info_config_false", false));
												player.sendMessage(storeM3);
												if (store.getShopFile() != null) {
													String storeM4 = i18n("arena_info_villager_shop_name", false)
															.replace("%file%", store.getShopFile()).replace("%bool%",
																	store.getUseParent()
																			? i18n("arena_info_config_true", false)
																			: i18n("arena_info_config_false", false));
													player.sendMessage(storeM4);
												}
												String storeM5 = i18nonly("arena_info_villager_shop_dealer_name")
														.replace("%name%", store.isShopCustomName()
																? store.getShopCustomName()
																: i18nonly(
																		"arena_info_villager_shop_dealer_has_no_name"));
												player.sendMessage(storeM5);
											}
										} else if (args[3].equalsIgnoreCase("config")) {
											player.sendMessage(i18n("arena_info_header"));

											player.sendMessage(i18n("arena_info_config", false));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "compassEnabled").replace("%value%",
															i18n("arena_info_config_"
																	+ game.getCompassEnabled().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "joinRandomTeamAfterLobby")
													.replace("%value%",
															i18n("arena_info_config_" + game
																	.getJoinRandomTeamAfterLobby().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "joinRandomTeamOnJoin")
													.replace("%value%", i18n("arena_info_config_"
															+ game.getJoinRandomTeamOnJoin().name().toLowerCase(),
															false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "addWoolToInventoryOnJoin")
													.replace("%value%",
															i18n("arena_info_config_" + game
																	.getAddWoolToInventoryOnJoin().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "preventKillingVillagers").replace("%value%",
															i18n("arena_info_config_" + game
																	.getPreventKillingVillagers().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "spectatorGm3").replace("%value%",
															i18n("arena_info_config_"
																	+ game.getSpectatorGm3().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "playerDrops").replace("%value%",
															i18n("arena_info_config_"
																	+ game.getPlayerDrops().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "friendlyfire").replace("%value%",
															i18n("arena_info_config_"
																	+ game.getFriendlyfire().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "coloredLeatherByTeamInLobby")
													.replace("%value%",
															i18n("arena_info_config_"
																	+ game.getColoredLeatherByTeamInLobby().name()
																			.toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "keepInventory").replace("%value%",
															i18n("arena_info_config_"
																	+ game.getKeepInventory().name().toLowerCase(),
																	false)));

											player.sendMessage(
													i18n("arena_info_config_constant", false)
															.replace("%constant%", "crafting").replace("%value%",
																	i18n("arena_info_config_"
																			+ game.getCrafting().name().toLowerCase(),
																			false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "gameScoreboard").replace("%value%",
															i18n("arena_info_config_"
																	+ game.getScoreboard().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "lobbyScoreboard").replace("%value%",
															i18n("arena_info_config_"
																	+ game.getLobbyScoreboard().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "gameBossbar").replace("%value%",
															i18n("arena_info_config_"
																	+ game.getGameBossbar().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "lobbyScoreboard").replace("%value%",
															i18n("arena_info_config_"
																	+ game.getLobbyBossbar().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "preventSpawningMobs")
													.replace("%value%", i18n("arena_info_config_"
															+ game.getPreventSpawningMobs().name().toLowerCase(),
															false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "spawnerholograms").replace("%value%",
															i18n("arena_info_config_"
																	+ game.getSpawnerHolograms().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "spawnerDisableMerge")
													.replace("%value%", i18n("arena_info_config_"
															+ game.getSpawnerDisableMerge().name().toLowerCase(),
															false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "give gameStartItems").replace("%value%",
															i18n("arena_info_config_"
																	+ game.getGameStartItems().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "give playerRespawnItems").replace("%value%",
															i18n("arena_info_config_"
																	+ game.getPlayerRespawnItems().name().toLowerCase(),
																	false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "spawnerHologramsCountdown")
													.replace("%value%", i18n("arena_info_config_"
															+ game.getSpawnerHologramsCountdown().name().toLowerCase(),
															false)));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "upgrades (experimental)").replace("%value%",
															i18n("arena_info_config_"
																	+ String.valueOf(game.isUpgradesEnabled()),
																	false)));

											// NON-BOOLEAN CONSTANTS

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "arenaTime")
													.replace("%value%", game.getArenaTime().name()));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "arenaWeather").replace("%value%",
															game.getArenaWeather() != null
																	? game.getArenaWeather().name()
																	: "default"));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "lobbybossbarcolor").replace("%value%",
															game.getLobbyBossBarColor() != null
																	? game.getLobbyBossBarColor().name()
																	: "default"));

											player.sendMessage(i18n("arena_info_config_constant", false)
													.replace("%constant%", "gamebossbarcolor").replace("%value%",
															game.getGameBossBarColor() != null
																	? game.getGameBossBarColor().name()
																	: "default"));
										} else {
											sendInfoSelectType(player, game);
										}
									} else {
										sendInfoSelectType(player, game);
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
									boolean isArenaSaved = gc.get(arN).cmd(player, args[2],
											nargs.toArray(new String[nargs.size()]));
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
				List<String> cmds = Arrays.asList("join", "leave", "list", "stats");
				if (player.hasPermission(ADMIN_PERMISSION)) {
					cmds = Arrays.asList("join", "leave", "list", "admin", "reload", "stats", "addholo", "removeholo");
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
								"team", "spawner", "time", "store", "save", "remove", "edit", "jointeam", "minplayers",
								"info", "config", "arenatime", "arenaweather", "lobbybossbarcolor", "gamebossbarcolor",
								"upgrades");
						StringUtil.copyPartialMatches(args[2], cmds, completionList);
					} else if (args[2].equalsIgnoreCase("pausecountdown") && args.length == 4) {
						StringUtil.copyPartialMatches(args[3], Arrays.asList("30", "60"), completionList);
					} else if (args[2].equalsIgnoreCase("time") && args.length == 4) {
						StringUtil.copyPartialMatches(args[3], Arrays.asList("180", "300", "600"), completionList);
					} else if (args[2].equalsIgnoreCase("minplayers") && args.length == 4) {
						StringUtil.copyPartialMatches(args[3], Arrays.asList("2", "3", "4", "5"), completionList);
					} else if (args[2].equalsIgnoreCase("info")) {
						if (args.length == 4) {
							List<String> cmds = Arrays.asList("base", "stores", "spawners", "teams", "config");
							StringUtil.copyPartialMatches(args[3], cmds, completionList);
						}
					} else if (args[2].equalsIgnoreCase("arenatime")) {
						if (args.length == 4) {
							List<String> cmds = new ArrayList<>();
							for (ArenaTime type : ArenaTime.values()) {
								cmds.add(type.name());
							}
							StringUtil.copyPartialMatches(args[3], cmds, completionList);
						}
					} else if (args[2].equalsIgnoreCase("lobbybossbarcolor")
							|| args[2].equalsIgnoreCase("gamebossbarcolor")) {
						if (args.length == 4) {
							List<String> cmds = new ArrayList<>();
							cmds.add("default");
							for (BarColor type : BarColor.values()) {
								cmds.add(type.name());
							}
							StringUtil.copyPartialMatches(args[3], cmds, completionList);
						}
					} else if (args[2].equalsIgnoreCase("arenaweather")) {
						if (args.length == 4) {
							List<String> cmds = new ArrayList<String>();
							cmds.add("default");
							for (WeatherType type : WeatherType.values()) {
								cmds.add(type.name());
							}
							StringUtil.copyPartialMatches(args[3], cmds, completionList);
						}
					} else if (args[2].equalsIgnoreCase("store")) {
						if (args.length == 4) {
							List<String> cmds = Arrays.asList("add", "remove", "type");
							StringUtil.copyPartialMatches(args[3], cmds, completionList);
						}
						if (args.length == 5 && args[3].equalsIgnoreCase("type")) {
							List<String> cmds = new ArrayList<String>();
							for (EntityType type : EntityType.values()) {
								if (type.isAlive()) {
									cmds.add(type.name());
								}
							}
							StringUtil.copyPartialMatches(args[4], cmds, completionList);
						}
						if (args.length == 5 && args[3].equalsIgnoreCase("add")) {
							StringUtil.copyPartialMatches(args[4],
									Arrays.asList("Villager_shop", "Dealer", "Seller", "&a&lVillager_shop"),
									completionList);
						}
						if (args.length == 6 && args[3].equalsIgnoreCase("add")) {
							// TODO scan files for this :D
						}
						if (args.length == 7 && args[3].equalsIgnoreCase("add")) {
							StringUtil.copyPartialMatches(args[6], Arrays.asList("true", "false"), completionList);
						}
					} else if (args[2].equalsIgnoreCase("config")) {
						if (args.length == 4) {
							List<String> cmds = Arrays.asList("compassEnabled", "joinRandomTeamAfterLobby",
									"joinRandomTeamOnJoin", "addWoolToInventoryOnJoin", "preventKillingVillagers",
									"spectatorGm3", "playerDrops", "friendlyfire", "coloredLeatherByTeamInLobby",
									"keepInventory", "crafting", "gamebossbar", "lobbybossbar", "gamescoreboard",
									"lobbyscoreboard", "preventspawningmobs", "spawnerholograms", "spawnerDisableMerge",
									"gamestartitems", "playerrespawnitems", "spawnerhologramscountdown");
							StringUtil.copyPartialMatches(args[3], cmds, completionList);
						}
						if (args.length == 5) {
							List<String> cmds = Arrays.asList("true", "false", "inherit");
							StringUtil.copyPartialMatches(args[4], cmds, completionList);
						}
					} else if (args[2].equalsIgnoreCase("upgrades")) {
						if (args.length == 4) {
							StringUtil.copyPartialMatches(args[3], Arrays.asList("true", "false"), completionList);
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
							StringUtil.copyPartialMatches(args[6], Arrays.asList("1", "2", "4", "8"), completionList);
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

		if (player.hasPermission(ADMIN_PERMISSION) || player.hasPermission(OTHER_STATS_PERMISSION)) {
			player.sendMessage(i18n("help_bw_stats_other", false));
		} else {
			player.sendMessage(i18n("help_bw_stats", false));
		}

		if (player.hasPermission(ADMIN_PERMISSION)) {
			player.sendMessage(i18n("help_bw_addholo", false));
			player.sendMessage(i18n("help_bw_removeholo", false));

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
			player.sendMessage(i18n("help_bw_admin_config", false));
			player.sendMessage(i18n("help_bw_admin_arena_time", false));
			player.sendMessage(i18n("help_bw_admin_arena_weather", false));
			player.sendMessage(i18n("help_bw_admin_upgrades", false));
			player.sendMessage(i18n("help_bw_admin_remove", false));
			player.sendMessage(i18n("help_bw_admin_edit", false));
			player.sendMessage(i18n("help_bw_admin_save", false));
			player.sendMessage(i18n("help_bw_reload", false));
		}
	}

	private void sendStats(Player player, PlayerStatistic statistic) {
		player.sendMessage(i18n("statistics_header").replace("%player%", statistic.getName()));

		player.sendMessage(i18n("statistics_kills", false).replace("%kills%",
				Integer.toString(statistic.getKills() + statistic.getCurrentKills())));
		player.sendMessage(i18n("statistics_deaths", false).replace("%deaths%",
				Integer.toString(statistic.getDeaths() + statistic.getCurrentDeaths())));
		player.sendMessage(i18n("statistics_kd", false).replace("%kd%",
				Double.toString(statistic.getKD() + statistic.getCurrentKD())));
		player.sendMessage(i18n("statistics_wins", false).replace("%wins%",
				Integer.toString(statistic.getWins() + statistic.getCurrentWins())));
		player.sendMessage(i18n("statistics_loses", false).replace("%loses%",
				Integer.toString(statistic.getLoses() + statistic.getCurrentLoses())));
		player.sendMessage(i18n("statistics_games", false).replace("%games%",
				Integer.toString(statistic.getGames() + statistic.getCurrentGames())));
		player.sendMessage(i18n("statistics_beds", false).replace("%beds%",
				Integer.toString(statistic.getDestroyedBeds() + statistic.getCurrentDestroyedBeds())));
		player.sendMessage(i18n("statistics_score", false).replace("%score%",
				Integer.toString(statistic.getScore() + statistic.getCurrentScore())));
	}

	private void sendInfoSelectType(Player player, Game game) {
		String select = i18n("please_select_info_type").replace("%arena%", game.getName());
		String base = i18n("please_select_info_type_base", false).replace("%arena%", game.getName());
		String stores = i18n("please_select_info_type_stores", false).replace("%arena%", game.getName());
		String spawners = i18n("please_select_info_type_spawners", false).replace("%arena%", game.getName());
		String teams = i18n("please_select_info_type_teams", false).replace("%arena%", game.getName());
		String config = i18n("please_select_info_type_config", false).replace("%arena%", game.getName());
		String click = i18n("please_select_info_type_click", false);

		if (Main.isSpigot()) {
			player.sendMessage(select);
			TextComponent[] hoverComponent = new TextComponent[] { new TextComponent(click) };

			TextComponent msg1 = new TextComponent(base);
			msg1.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bw admin " + game.getName() + " info base"));
			msg1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
			player.spigot().sendMessage(msg1);

			TextComponent msg2 = new TextComponent(stores);
			msg2.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bw admin " + game.getName() + " info stores"));
			msg2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
			player.spigot().sendMessage(msg2);

			TextComponent msg3 = new TextComponent(spawners);
			msg3.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bw admin " + game.getName() + " info spawners"));
			msg3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
			player.spigot().sendMessage(msg3);

			TextComponent msg4 = new TextComponent(teams);
			msg4.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bw admin " + game.getName() + " info teams"));
			msg4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
			player.spigot().sendMessage(msg4);

			TextComponent msg5 = new TextComponent(config);
			msg5.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bw admin " + game.getName() + " info config"));
			msg5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
			player.spigot().sendMessage(msg5);
		} else {
			player.sendMessage(select);
			player.sendMessage(base);
			player.sendMessage(stores);
			player.sendMessage(spawners);
			player.sendMessage(teams);
			player.sendMessage(config);
		}
	}
}
