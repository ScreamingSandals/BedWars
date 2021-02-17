package org.screamingsandals.bedwars.game;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.ArenaTime;
import org.screamingsandals.bedwars.region.FlatteningBedUtils;
import org.screamingsandals.bedwars.region.LegacyBedUtils;
import org.screamingsandals.bedwars.utils.TeamJoinMetaDataValue;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.screamingsandals.bedwars.lib.lang.I.i18nonly;
import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class GameCreator {
    public static final String BEDWARS_TEAM_JOIN_METADATA = "bw-addteamjoin";

    private Game game;
    private HashMap<String, GameStore> villagerstores = new HashMap<>();

    public GameCreator(Game game) {
        this.game = game;
        List<GameStore> gs = game.getGameStoreList();
        if (!gs.isEmpty()) {
            for (GameStore store : gs) {
                villagerstores.put(store.getStoreLocation().getBlockX() + ";" + store.getStoreLocation().getBlockY()
                        + ";" + store.getStoreLocation().getBlockZ(), store);
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
        } else if (action.equalsIgnoreCase("postgamewaiting")) {
            if (args.length >= 1) {
                response = setPostGameWaiting(Integer.parseInt(args[0]));
            }
        } else if (action.equalsIgnoreCase("arenatime")) {
            if (args.length >= 1) {
                response = setArenaTime(args[0]);
            }
        } else if (action.equalsIgnoreCase("arenaweather")) {
            if (args.length >= 1) {
                response = setArenaWeather(args[0]);
            }
        } else if (action.equalsIgnoreCase("minplayers")) {
            if (args.length >= 1) {
                response = setMinPlayers(Integer.parseInt(args[0]));
            }
        } else if (action.equalsIgnoreCase("config")) {
            if (args.length >= 2) {
                response = setLocalConfigVariable(args[0], args[1]);
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
                    response = setTeamBed(args[1], player.getTargetBlock(null, 5));
                }
            }
        } else if (action.equalsIgnoreCase("spawner")) {
            //TODO: add floatingEnabled param to command add
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (args.length >= 3) {
                        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                            if (args.length >= 4) {
                                double customLevel;
                                try {
                                    customLevel = Double.parseDouble(args[3]);
                                } catch (NumberFormatException e) {
                                    player.sendMessage(i18n("admin_command_invalid_spawner_level"));
                                    customLevel = 1.0;
                                }
                                if (args.length >= 5) {
                                    if (args.length >= 6) {
                                        org.screamingsandals.bedwars.api.Team newTeam = null;
                                        for (Team team : game.getTeams()) {
                                            if (team.name.equals(args[5])) {
                                                newTeam = team;
                                            }
                                        }
                                    	int maxSpawnedResources = -1;
                                        if (newTeam == null) {
                                        	boolean error = true;
                                        	if (args.length == 6) { // Check if it's not higher than 6
	                                        	try {
	                                        		maxSpawnedResources = Integer.parseInt(args[5]);
	                                        		error = false;
	                                        	} catch (NumberFormatException e) {
	                                        	}
                                        	}
                                        	if (error) {
	                                            player.sendMessage(i18n("admin_command_invalid_team").replace("%team%", args[5]));
	                                            return false;
                                        	}
                                        } else if (args.length >= 7) {
                                        	maxSpawnedResources = Integer.parseInt(args[6]);
                                        }
                                        response = addSpawner(args[1], player.getLocation(), args[4], Boolean.parseBoolean(args[2]), customLevel, newTeam, maxSpawnedResources);
                                    } else {
                                        response = addSpawner(args[1], player.getLocation(), args[4], Boolean.parseBoolean(args[2]), customLevel, null, -1);
                                    }
                                } else {
                                    response = addSpawner(args[1], player.getLocation(), null, Boolean.parseBoolean(args[2]), customLevel, null, -1);
                                }
                            } else {
                                response = addSpawner(args[1], player.getLocation(), null, Boolean.parseBoolean(args[2]), 1, null, -1);
                            }
                        } else {
                            response = null;
                        }
                    } else {
                        response = addSpawner(args[1], player.getLocation(), null, true, 1, null, -1);
                    }
                } else if (args[0].equalsIgnoreCase("remove")) {
                    response = removeSpawner(player.getLocation());
                } else if (args[0].equalsIgnoreCase("reset")) {
                    response = resetAllSpawners();
                }
            }
        } else if (action.equalsIgnoreCase("store")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (args.length >= 2) {
                        if (args.length >= 3) {
                            if (args.length >= 4) {
                                response = addStore(player.getLocation(), args[2], Boolean.parseBoolean(args[3]), args[1]);
                            } else {
                                response = addStore(player.getLocation(), args[2], true, args[1]);
                            }
                        } else {
                            response = addStore(player.getLocation(), null, true, args[1]);
                        }
                    } else {
                        response = addStore(player.getLocation(), null, true, null);
                    }
                } else if (args[0].equalsIgnoreCase("remove")) {
                    response = removeStore(player.getLocation());
                } else if (args[0].equalsIgnoreCase("child")) {
                    response = setStoreAge(player.getLocation(), true);
                } else if (args[0].equalsIgnoreCase("adult")) {
                    response = setStoreAge(player.getLocation(), false);
                } else if (args[0].equalsIgnoreCase("type")) {
                    if (args.length >= 2) {
                        response = changeStoreEntityType(player.getLocation(), args[1]);
                    }
                }
            }
        } else if (action.equalsIgnoreCase("jointeam")) {
            if (args.length >= 1) {
                response = addTeamJoinEntity(player, args[0]);
            }
        } else if (action.equalsIgnoreCase("lobbybossbarcolor")) {
            if (args.length >= 1) {
                response = setLobbyBossBarColor(args[0]);
            }
        } else if (action.equalsIgnoreCase("gamebossbarcolor")) {
            if (args.length >= 1) {
                response = setGameBossBarColor(args[0]);
            }
        } else if (action.equalsIgnoreCase("customprefix")) {
            if (args.length >= 1) {
                response = setCustomPrefix(String.join(" ", args));
            }
        } else if (action.equalsIgnoreCase("save")) {
            List<GameStore> gamestores = new ArrayList<>();
            for (Map.Entry<String, GameStore> vloc : villagerstores.entrySet()) {
                gamestores.add(vloc.getValue());
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
                } else if (game.getGameStoreList().isEmpty()) {
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
                    if (block.getState().getData() instanceof org.bukkit.material.Bed) {
                        org.bukkit.material.Bed bed = (org.bukkit.material.Bed) block.getState().getData();
                        if (!bed.isHeadOfBed()) {
                            t.bed = LegacyBedUtils.getBedNeighbor(block).getLocation();
                        } else {
                            t.bed = loc;
                        }
                    } else {
                        t.bed = loc;
                    }

                } else {
                    // 1.13+
                    if (block.getBlockData() instanceof Bed) {
                        Bed bed = (Bed) block.getBlockData();
                        if (bed.getPart() != Part.HEAD) {
                            t.bed = FlatteningBedUtils.getBedNeighbor(block).getLocation();
                        } else {
                            t.bed = loc;
                        }
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
        color = color.toUpperCase();
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
        color = color.toUpperCase();
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
        team.game = game;
        game.getTeams().add(team);

        return i18n("admin_command_team_created").replace("%team%", team.name)
                .replace("%teamcolor%", team.color.chatColor + team.color.name())
                .replace("%maxplayers%", Integer.toString(team.maxPlayers));
    }
}
