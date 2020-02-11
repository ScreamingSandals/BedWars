package org.screamingsandals.bedwars.game;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.ArenaTime;
import org.screamingsandals.bedwars.api.game.ConfigVariables;
import org.screamingsandals.bedwars.api.game.GameStore;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static misat11.lib.lang.I.mpr;
import static misat11.lib.lang.I18n.i18n;

public class GameCreator {
    public static final String BEDWARS_TEAM_JOIN_METADATA = "bw-addteamjoin";

    private Game game;
    private HashMap<String, GameStore> villagerStores = new HashMap<>();

    public GameCreator(Game game) {
        this.game = game;
        List<GameStore> gs = game.getGameStores();
        if (!gs.isEmpty()) {
            for (GameStore store : gs) {
                villagerStores.put(store.getStoreLocation().getBlockX() + ";" + store.getStoreLocation().getBlockY()
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
        } else if (action.equalsIgnoreCase("lobbytime")) {
            if (args.length >= 1) {
                response = setLobbyTime(Integer.parseInt(args[0]));
            }
        } else if (action.equalsIgnoreCase("time")) {
            if (args.length >= 1) {
                response = setGameTime(Integer.parseInt(args[0]));
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
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (args.length >= 3) {
                        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                            if (args.length >= 4) {
                                double customLevel;
                                try {
                                    customLevel = Double.parseDouble(args[3]);
                                } catch (NumberFormatException e) {
                                    mpr("commands.admin.messages.invalid_spawner_level").send(player);
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
	                                        	} catch (NumberFormatException ignored) {
	                                        	}
                                        	}
                                        	if (error) {
                                                mpr("commands.admin.messages.team_is_not_exists")
                                                        .replace("%team%", args[5])
                                                        .send(player);
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
        } else if (action.equalsIgnoreCase("save")) {
            List<GameStore> gamestores = new ArrayList<>();
            for (Map.Entry<String, GameStore> vloc : villagerStores.entrySet()) {
                gamestores.add(vloc.getValue());
            }
            boolean isTeamsSetCorrectly = true;
            for (Team team : game.getTeams()) {
                if (team.bed == null) {
                    response =  mpr("commands.admin.messages.set_bed_for_team_before_save").replace("%team%", team.name).get();
                    isTeamsSetCorrectly = false;
                    break;
                } else if (team.spawn == null) {
                    response = mpr("commands.admin.messages.set_bed_for_team_before_save").replace("%team%", team.name).get();
                    isTeamsSetCorrectly = false;
                    break;
                }
            }
            if (isTeamsSetCorrectly) {
                game.setGameStores(gamestores);
                if (game.getTeams().size() < 2) {
                    response = mpr("commands.admin.messages.need_min_2_teams").get();
                } else if (game.getPos1() == null || game.getPos2() == null) {
                    response = mpr("commands.admin.messages.set_pos1_pos2_before_save").get();
                } else if (game.getLobbySpawn() == null) {
                    response = mpr("commands.admin.messages.set_lobby_before_save").get();
                } else if (game.getSpecSpawn() == null) {
                    response = mpr("commands.admin.messages.set_spec_before_save").get();
                } else if (game.getGameStores().isEmpty()) {
                    response = mpr("commands.admin.messages.set_stores_before_save").get();
                } else if (game.getSpawners().isEmpty()) {
                    response = mpr("commands.admin.messages.set_spawners_before_save").get();
                } else {
                    game.saveToConfig();
                    game.start();
                    Main.addGame(game);
                    response = mpr("commands.admin.messages.game_saved_and_started").get();
                    isArenaSaved = true;
                }
            }
        }

        if (response == null) {
            response = mpr("commands.errors.unknown_command").get();
        }
        player.sendMessage(response);
        return isArenaSaved;
    }

    private String setGameBossBarColor(String color) {
        color = color.toUpperCase();
        BarColor c = null;
        if (!color.equalsIgnoreCase("default")) {
            try {
                c = BarColor.valueOf(color);
            } catch (Exception e) {
                return mpr("admin_command_invalid_bar_color").get();
            }
        }

        game.setGameBossBarColor(c);

        return i18n("admin_command_bar_color_set").replace("%color%", c == null ? "default" : c.name())
                .replace("%type%", "GAME");
    }

    private String setLobbyBossBarColor(String color) {
        color = color.toUpperCase();
        BarColor c = null;
        if (!color.equalsIgnoreCase("default")) {
            try {
                c = BarColor.valueOf(color);
            } catch (Exception e) {
                return i18n("admin_command_invalid_bar_color");
            }
        }

        game.setLobbyBossBarColor(c);

        return i18n("admin_command_bar_color_set").replace("%color%", c == null ? "default" : c.name())
                .replace("%type%", "LOBBY");
    }

    private String setArenaWeather(String arenaWeather) {
        arenaWeather = arenaWeather.toUpperCase();
        WeatherType c = null;
        if (!arenaWeather.equalsIgnoreCase("default")) {
            try {
                c = WeatherType.valueOf(arenaWeather);
            } catch (Exception e) {
                return i18n("admin_command_invalid_arena_weather");
            }
        }

        game.setArenaWeather(c);

        return i18n("admin_command_arena_weather_set").replace("%weather%", c == null ? "default" : c.name());
    }

    private String setArenaTime(String arenaTime) {
        arenaTime = arenaTime.toUpperCase();
        ArenaTime c;
        try {
            c = ArenaTime.valueOf(arenaTime);
        } catch (Exception e) {
            return i18n("admin_command_invalid_arena_time");
        }

        game.setArenaTime(c);

        return i18n("admin_command_arena_time_set").replace("%time%", c.name());
    }

    private String setLocalConfigVariable(String config, String value) {
        value = value.toLowerCase();
        boolean val = false;
        boolean inherited = false;
        switch (value) {
            case "t":
            case "tr":
            case "tru":
            case "true":
            case "y":
            case "ye":
            case "yes":
            case "1":
            	val = true;
                break;
            case "f":
            case "fa":
            case "fal":
            case "fals":
            case "false":
            case "n":
            case "no":
            case "0":
            	val = false;
                break;
            case "i":
            case "in":
            case "inh":
            case "inhe":
            case "inher":
            case "inheri":
            case "inherit":
            case "d":
            case "de":
            case "def":
            case "defa":
            case "defau":
            case "defaul":
            case "default":
            	inherited = true;
                break;
            default:
                return i18n("admin_command_invalid_config_value");
        }

        config = config.toLowerCase().replaceAll("-", "").replaceAll("_", "");
        for (ConfigVariables var : ConfigVariables.values()) {
        	if (GameConfigManager.transform(var).equalsIgnoreCase(config)) {
        		if (inherited) {
        			game.getConfigManager().reset(var);
        		} else {
        			game.getConfigManager().set(var, val);
        		}
                return i18n("admin_command_config_variable_set_to").replace("%config%", config).replace("%value%", value);
        	}
        }
        
        return i18n("admin_command_invalid_config_variable_name");
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

    private String resetAllSpawners() {
        game.getSpawners().clear();
        return i18n("admin_command_spawners_reseted").replace("%arena%", game.getName());
    }

    private String addSpawner(String type, Location loc, String customName, boolean hologramEnabled, double startLevel, org.screamingsandals.bedwars.api.Team team, int maxSpawnedResources) {
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
            game.getSpawners().add(new ItemSpawner(loc, spawnerType, customName, hologramEnabled, startLevel, team, maxSpawnedResources));
            return i18n("admin_command_spawner_added").replace("%resource%", spawnerType.getItemName())
                    .replace("%x%", Integer.toString(loc.getBlockX())).replace("%y%", Integer.toString(loc.getBlockY()))
                    .replace("%z%", Integer.toString(loc.getBlockZ()));
        } else {
            return i18n("admin_command_invalid_spawner_type");
        }
    }

    private String addStore(Location loc, String shop, boolean useParent, String name) {
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
        if (villagerStores.containsKey(location)) {
            return i18n("admin_command_store_already_exists");
        }
        if (name != null) {
            name = ChatColor.translateAlternateColorCodes('&', name);
        }
        villagerStores.put(location, new GameStore(loc, shop, useParent, name, name != null));
        return i18n("admin_command_store_added").replace("%x%", Double.toString(loc.getX()))
                .replace("%y%", Double.toString(loc.getY())).replace("%z%", Double.toString(loc.getZ()))
                .replace("%yaw%", Float.toString(loc.getYaw())).replace("%pitch%", Float.toString(loc.getPitch()));
    }

    private String changeStoreEntityType(Location loc, String type) {
        type = type.toUpperCase();

        String location = loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
        if (villagerStores.containsKey(location)) {
            EntityType t = null;
            try {
                t = EntityType.valueOf(type);
                if (!t.isAlive()) {
                    t = null;
                }
            } catch (Exception ignored) {
            }

            if (t == null) {
                return i18n("admin_command_wrong_living_entity_type");
            }

            villagerStores.get(location).setEntityType(t);

            return i18n("admin_command_store_living_entity_type_set").replace("%type%", t.toString());
        }

        return i18n("admin_command_store_not_exists");
    }

    private String removeStore(Location loc) {
        if (game.getWorld() != loc.getWorld()) {
            return i18n("admin_command_must_be_in_same_world");
        }
        String location = loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
        if (!villagerStores.containsKey(location)) {
            return i18n("admin_command_store_not_exists");
        }
        villagerStores.remove(location);
        return i18n("admin_command_store_removed").replace("%x%", Double.toString(loc.getX()))
                .replace("%y%", Double.toString(loc.getY())).replace("%z%", Double.toString(loc.getZ()))
                .replace("%yaw%", Float.toString(loc.getYaw())).replace("%pitch%", Float.toString(loc.getPitch()));
    }

    private String setLobbySpawn(Location loc) {
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

    public String setLobbyTime(int countdown) {
        if (countdown >= 10 && countdown <= 600) {
            game.setLobbyTime(countdown);
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
        if (!p1.getWorld().equals(l.getWorld())) {
            return false;
        }

        Location min = new Location(p1.getWorld(), Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()),
                Math.min(p1.getZ(), p2.getZ()));
        Location max = new Location(p1.getWorld(), Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()),
                Math.max(p1.getZ(), p2.getZ()));
        return (min.getX() <= l.getX() && min.getY() <= l.getY() && min.getZ() <= l.getZ() && max.getX() >= l.getX()
                && max.getY() >= l.getY() && max.getZ() >= l.getZ());
    }

    public static boolean isChunkInArea(Chunk l, Location p1, Location p2) {
    	if (!p1.getWorld().equals(l.getWorld())) {
    		return false;
    	}
    	
        Chunk min = new Location(p1.getWorld(), Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()),
                Math.min(p1.getZ(), p2.getZ())).getChunk();
        Chunk max = new Location(p1.getWorld(), Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()),
                Math.max(p1.getZ(), p2.getZ())).getChunk();
        return (min.getX() <= l.getX() && min.getZ() <= l.getZ() && max.getX() >= l.getX() && max.getZ() >= l.getZ());
    }
}
