package org.screamingsandals.bedwars.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.ArenaTime;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.game.*;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

import static misat11.lib.lang.I.m;
import static misat11.lib.lang.I18n.i18n;
import static misat11.lib.lang.I18n.i18nonly;

public class AdminCommand extends BaseCommand {

    public HashMap<String, GameCreator> gc = new HashMap<>();

    public AdminCommand() {
        super("admin", ADMIN_PERMISSION, false, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (args.size() >= 2) {
            String arN = args.get(0);
            if (args.get(1).equalsIgnoreCase("info")) {
                if (Main.isGameExists(arN)) {
                    Game game = Main.getGame(arN);
                    if (args.size() >= 3) {
                        if (args.get(2).equalsIgnoreCase("base")) {
                            player.sendMessage(i18n("arena_info_header"));

                            player.sendMessage(i18n("arena_info_name", false).replace("%name%", game.getName()));
                            String status = i18n("arena_info_status", false);
                            switch (game.getStatus()) {
                                case DISABLED:
                                    if (gc.containsKey(arN)) {
                                        status = status.replace("%status%",
                                                i18n("arena_info_status_disabled_in_edit", false));
                                    } else {
                                        status = status.replace("%status%", i18n("arena_info_status_disabled", false));
                                    }
                                    break;
                                case REBUILDING:
                                    status = status.replace("%status%", i18n("arena_info_status_rebuilding", false));
                                    break;
                                case RUNNING:
                                case GAME_END_CELEBRATING:
                                    status = status.replace("%status%", i18n("arena_info_status_running", false));
                                    break;
                                case WAITING:
                                    status = status.replace("%status%", i18n("arena_info_status_waiting", false));
                                    break;
                            }
                            player.sendMessage(status);

                            player.sendMessage(
                                    i18n("arena_info_world", false).replace("%world%", game.getWorld().getName()));

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
                            player.sendMessage(i18n("arena_info_min_players", false).replace("%minplayers%",
                                    Integer.toString(game.getMinPlayers())));
                            player.sendMessage(i18n("arena_info_lobby_countdown", false).replace("%time%",
                                    Integer.toString(game.getPauseCountdown())));
                            player.sendMessage(i18n("arena_info_game_time", false).replace("%time%",
                                    Integer.toString(game.getGameTime())));
                            m("arena_info_postgamewaiting").replace("time", game.getPostGameWaiting()).send(player);

                        } else if (args.get(2).equalsIgnoreCase("teams")) {
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
                        } else if (args.get(2).equalsIgnoreCase("spawners")) {
                            player.sendMessage(i18n("arena_info_header"));

                            player.sendMessage(i18n("arena_info_spawners", false));
                            for (ItemSpawner spawner : game.getSpawners()) {
                                Location loc_spawner = spawner.loc;
                                org.screamingsandals.bedwars.api.Team team = spawner.getTeam();

                                String spawnerTeam;

                                if (team != null) {
                                    spawnerTeam = TeamColor.fromApiColor(team.getColor()).chatColor + team.getName();
                                } else {
                                    spawnerTeam = i18nonly("arena_info_spawner_no_team");
                                }

                                String spawnerM = i18n("arena_info_spawner", false)
                                        .replace("%resource%", spawner.type.getItemName())
                                        .replace("%x%", String.valueOf(loc_spawner.getBlockX()))
                                        .replace("%y%", String.valueOf(loc_spawner.getBlockY()))
                                        .replace("%z%", String.valueOf(loc_spawner.getBlockZ()))
                                        .replace("%yaw%", String.valueOf(loc_spawner.getYaw()))
                                        .replace("%pitch%", String.valueOf(loc_spawner.getPitch()))
                                        .replace("%world%", loc_spawner.getWorld().getName())
                                        .replace("%team%", spawnerTeam)
                                        .replace("%holo%", String.valueOf(spawner.getHologramEnabled()));
    
                                
                                player.sendMessage(spawnerM);
                            }
                        } else if (args.get(2).equalsIgnoreCase("stores")) {
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

                                String storeM2 = i18n("arena_info_villager_entity_type", false).replace("%type%",
                                        store.getEntityType().name());
                                player.sendMessage(storeM2);

                                String storeM3 = i18n("arena_info_villager_shop", false).replace("%bool%",
                                        store.getShopFile() != null ? i18n("arena_info_config_true", false)
                                                : i18n("arena_info_config_false", false));
                                player.sendMessage(storeM3);
                                if (store.getShopFile() != null) {
                                    String storeM4 = i18n("arena_info_villager_shop_name", false)
                                            .replace("%file%", store.getShopFile()).replace("%bool%",
                                                    store.getUseParent() ? i18n("arena_info_config_true", false)
                                                            : i18n("arena_info_config_false", false));
                                    player.sendMessage(storeM4);
                                }
                                String storeM5 = i18nonly("arena_info_villager_shop_dealer_name").replace("%name%",
                                        store.isShopCustomName() ? store.getShopCustomName()
                                                : i18nonly("arena_info_villager_shop_dealer_has_no_name"));
                                player.sendMessage(storeM5);
                            }
                        } else if (args.get(2).equalsIgnoreCase("config")) {
                            player.sendMessage(i18n("arena_info_header"));

                            player.sendMessage(i18n("arena_info_config", false));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "compassEnabled").replace("%value%",
                                            i18n("arena_info_config_" + game.getCompassEnabled().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(
                                    i18n("arena_info_config_constant", false)
                                            .replace("%constant%", "joinRandomTeamAfterLobby").replace("%value%",
                                            i18n("arena_info_config_"
                                                            + game.getJoinRandomTeamAfterLobby().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "joinRandomTeamOnJoin")
                                    .replace("%value%", i18n(
                                            "arena_info_config_" + game.getJoinRandomTeamOnJoin().name().toLowerCase(),
                                            false)));

                            player.sendMessage(
                                    i18n("arena_info_config_constant", false)
                                            .replace("%constant%", "addWoolToInventoryOnJoin").replace("%value%",
                                            i18n("arena_info_config_"
                                                            + game.getAddWoolToInventoryOnJoin().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(
                                    i18n("arena_info_config_constant", false)
                                            .replace("%constant%", "preventKillingVillagers").replace("%value%",
                                            i18n("arena_info_config_"
                                                            + game.getPreventKillingVillagers().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "playerDrops").replace("%value%", i18n(
                                            "arena_info_config_" + game.getPlayerDrops().name().toLowerCase(), false)));

                            player.sendMessage(
                                    i18n("arena_info_config_constant", false).replace("%constant%", "friendlyfire")
                                            .replace("%value%", i18n(
                                                    "arena_info_config_" + game.getFriendlyfire().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "coloredLeatherByTeamInLobby").replace("%value%",
                                            i18n("arena_info_config_"
                                                            + game.getColoredLeatherByTeamInLobby().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(
                                    i18n("arena_info_config_constant", false).replace("%constant%", "keepInventory")
                                            .replace("%value%", i18n(
                                                    "arena_info_config_" + game.getKeepInventory().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "crafting").replace("%value%", i18n(
                                            "arena_info_config_" + game.getCrafting().name().toLowerCase(), false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "gameScoreboard").replace("%value%", i18n(
                                            "arena_info_config_" + game.getScoreboard().name().toLowerCase(), false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "lobbyScoreboard").replace("%value%",
                                            i18n("arena_info_config_" + game.getLobbyScoreboard().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "gameBossbar").replace("%value%", i18n(
                                            "arena_info_config_" + game.getGameBossbar().name().toLowerCase(), false)));

                            player.sendMessage(
                                    i18n("arena_info_config_constant", false).replace("%constant%", "lobbyScoreboard")
                                            .replace("%value%", i18n(
                                                    "arena_info_config_" + game.getLobbyBossbar().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "preventSpawningMobs")
                                    .replace("%value%", i18n(
                                            "arena_info_config_" + game.getPreventSpawningMobs().name().toLowerCase(),
                                            false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "spawnerholograms").replace("%value%",
                                            i18n("arena_info_config_" + game.getSpawnerHolograms().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "spawnerDisableMerge")
                                    .replace("%value%", i18n(
                                            "arena_info_config_" + game.getSpawnerDisableMerge().name().toLowerCase(),
                                            false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "give gameStartItems").replace("%value%",
                                            i18n("arena_info_config_" + game.getGameStartItems().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "give playerRespawnItems")
                                    .replace("%value%", i18n(
                                            "arena_info_config_" + game.getPlayerRespawnItems().name().toLowerCase(),
                                            false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "spawnerHologramsCountdown").replace("%value%",
                                            i18n("arena_info_config_"
                                                            + game.getSpawnerHologramsCountdown().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "damageWhenPlayerIsNotInArena").replace("%value%",
                                            i18n("arena_info_config_"
                                                            + game.getDamageWhenPlayerIsNotInArena().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(
                                    i18n("arena_info_config_constant", false)
                                            .replace("%constant%", "removeunusedtargetblocks").replace("%value%",
                                            i18n("arena_info_config_"
                                                            + game.getRemoveUnusedTargetBlocks().name().toLowerCase(),
                                                    false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "allowblockfalling")
                                    .replace("%value%", i18n(
                                            "arena_info_config_" + game.getAllowBlockFalling().name().toLowerCase(),
                                            false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "holoAboveBed")
                                    .replace("%value%", i18n(
                                            "arena_info_config_" + game.getHoloAboveBed().name().toLowerCase(),
                                            false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "spectatorJoin")
                                    .replace("%value%", i18n(
                                            "arena_info_config_" + game.getSpectatorJoin().name().toLowerCase(),
                                            false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "stopTeamSpawnersOnDie")
                                    .replace("%value%", i18n(
                                            "arena_info_config_" + game.getStopTeamSpawnersOnDie().name().toLowerCase(),
                                            false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "anchorAutoFill")
                                    .replace("%value%", i18n(
                                            "arena_info_config_" + game.getAnchorAutoFill().name().toLowerCase(),
                                            false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "anchorDecreasing")
                                    .replace("%value%", i18n(
                                            "arena_info_config_" + game.getAnchorDecreasing().name().toLowerCase(),
                                            false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "cakeTargetBlockEating")
                                    .replace("%value%", i18n(
                                            "arena_info_config_" + game.getCakeTargetBlockEating().name().toLowerCase(),
                                            false)));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "targetBlockExplosions")
                                    .replace("%value%", i18n(
                                            "arena_info_config_" + game.getTargetBlockExplosions().name().toLowerCase(),
                                            false)));

                            // NON-BOOLEAN CONSTANTS

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "arenaTime").replace("%value%", game.getArenaTime().name()));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "arenaWeather")
                                    .replace("%value%", game.getArenaWeather() != null ? game.getArenaWeather().name()
                                            : "default"));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "lobbybossbarcolor").replace("%value%",
                                            game.getLobbyBossBarColor() != null ? game.getLobbyBossBarColor().name()
                                                    : "default"));

                            player.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "gamebossbarcolor").replace("%value%",
                                            game.getGameBossBarColor() != null ? game.getGameBossBarColor().name()
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
            } else if (args.get(1).equalsIgnoreCase("add")) {
                if (Main.isGameExists(arN)) {
                    player.sendMessage(i18n("allready_exists"));
                } else if (gc.containsKey(arN)) {
                    player.sendMessage(i18n("allready_working_on_it"));
                } else {
                    GameCreator creator = new GameCreator(Game.createGame(arN));
                    gc.put(arN, creator);
                    player.sendMessage(i18n("arena_added"));
                }
            } else if (args.get(1).equalsIgnoreCase("remove")) {
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
            } else if (args.get(1).equalsIgnoreCase("edit")) {
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
                    List<String> nargs = new ArrayList<>();
                    int lid = 0;
                    for (String arg : args) {
                        if (lid >= 2) {
                            nargs.add(arg);
                        }
                        lid++;
                    }
                    boolean isArenaSaved = gc.get(arN).cmd(player, args.get(1),
                            nargs.toArray(new String[nargs.size()]));
                    if (args.get(1).equalsIgnoreCase("save") && isArenaSaved) {
                        gc.remove(arN);
                    }
                } else {
                    player.sendMessage(i18n("arena_not_in_edit"));
                }
            }
        } else {
            player.sendMessage(i18n("usage_bw_admin"));
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            completion.addAll(Main.getGameNames());
            for (String arena : gc.keySet()) {
                if (!completion.contains(arena)) {
                    completion.add(arena);
                }
            }
        } else if (args.size() == 2) {
            completion.addAll(Arrays.asList("add", "lobby", "spec", "pos1", "pos2", "pausecountdown", "team", "spawner",
                    "time", "store", "save", "remove", "edit", "jointeam", "minplayers", "info", "config", "arenatime",
                    "arenaweather", "lobbybossbarcolor", "gamebossbarcolor", "postgamewaiting"));
        } else if (args.get(1).equalsIgnoreCase("pausecountdown") && args.size() == 3) {
            completion.addAll(Arrays.asList("30", "60"));
        } else if (args.get(1).equalsIgnoreCase("time") && args.size() == 3) {
            completion.addAll(Arrays.asList("180", "300", "600"));
        } else if (args.get(1).equalsIgnoreCase("minplayers") && args.size() == 3) {
            completion.addAll(Arrays.asList("2", "3", "4", "5"));
        } else if (args.get(1).equalsIgnoreCase("info") && args.size() == 3) {
            completion.addAll(Arrays.asList("base", "stores", "spawners", "teams", "config"));
        } else if (args.get(1).equalsIgnoreCase("arenatime") && args.size() == 3) {
            for (ArenaTime type : ArenaTime.values()) {
                completion.add(type.name());
            }
        } else if ((args.get(1).equalsIgnoreCase("lobbybossbarcolor")
                || args.get(1).equalsIgnoreCase("gamebossbarcolor")) && args.size() == 3) {
            try {
                completion.add("default");
                for (BarColor type : BarColor.values()) {
                    completion.add(type.name());
                }
            } catch (Throwable ignored) {
            }
        } else if (args.get(1).equalsIgnoreCase("arenaweather") && args.size() == 3) {
            completion.add("default");
            for (WeatherType type : WeatherType.values()) {
                completion.add(type.name());
            }
        } else if (args.get(1).equalsIgnoreCase("store")) {
            if (args.size() == 3) {
                completion.addAll(Arrays.asList("add", "remove", "type", "child", "adult"));
            }
            if (args.size() == 4 && args.get(2).equalsIgnoreCase("type")) {
                for (EntityType type : EntityType.values()) {
                    if (type.isAlive()) {
                        completion.add(type.name());
                    }
                }
            }
            if (args.size() == 4 && args.get(2).equalsIgnoreCase("add")) {
                completion.addAll(Arrays.asList("Villager_shop", "Dealer", "Seller", "&a&lVillager_shop", "&4Barter"));
            }
            if (args.size() == 5 && args.get(2).equalsIgnoreCase("add")) {
                // TODO scan files for this :D
            }
            if (args.size() == 6 && args.get(2).equalsIgnoreCase("add")) {
                completion.addAll(Arrays.asList("true", "false"));
            }
        } else if (args.get(1).equalsIgnoreCase("config")) {
            if (args.size() == 3) {
                completion.addAll(Arrays.asList("compassEnabled", "joinRandomTeamAfterLobby", "joinRandomTeamOnJoin",
                        "addWoolToInventoryOnJoin", "preventKillingVillagers", "playerDrops",
                        "friendlyfire", "coloredLeatherByTeamInLobby", "keepInventory", "crafting", "gamebossbar",
                        "lobbybossbar", "gamescoreboard", "lobbyscoreboard", "preventspawningmobs", "spawnerholograms",
                        "spawnerDisableMerge", "gamestartitems", "playerrespawnitems", "spawnerhologramscountdown",
                        "damagewhenplayerisnotinarena", "removeunusedtargetblocks", "holoabovebed", "allowblockfall",
                        "spectatorjoin", "stopTeamSpawnersOnDie", "anchorAutoFill", "anchorDecreasing", "cakeTargetBlockEating", "targetBlockExplosions"));
            }
            if (args.size() == 4) {
                completion.addAll(Arrays.asList("true", "false", "inherit"));
            }
        } else if (args.get(1).equalsIgnoreCase("spawner")) {
            if (args.size() == 3) {
                completion.addAll(Arrays.asList("add", "remove", "reset"));
            }
            if (args.get(2).equalsIgnoreCase("add")) {
                if (args.size() == 4) {
                    completion.addAll(Main.getAllSpawnerTypes());
                }
                if (args.size() == 5) {
                    completion.addAll(Arrays.asList("false", "true"));
                }
                if (args.size() == 6) {
                    completion.addAll(Collections.singletonList("1"));
                }
                if (args.size() == 8) {
                    if (gc.containsKey(args.get(0))) {
                        for (Team t : gc.get(args.get(0)).getGame().getTeams()) {
                            completion.add(t.name);
                        }
                    }
                }
                if (args.size() == 8 || args.size() == 9) {
                    completion.addAll(Arrays.asList("5", "10", "20"));
                }
            }
        } else if (args.get(1).equalsIgnoreCase("team")) {
            if (args.size() == 3) {
                completion.addAll(Arrays.asList("add", "color", "maxplayers", "spawn", "bed", "remove"));
            }
            if (args.size() == 4) {
                if (gc.containsKey(args.get(0))) {
                    for (Team t : gc.get(args.get(0)).getGame().getTeams()) {
                        completion.add(t.name);
                    }
                }
            }
            if (args.size() == 5) {
                if (args.get(2).equalsIgnoreCase("add") || args.get(2).equalsIgnoreCase("color")) {
                    for (TeamColor en : TeamColor.values()) {
                        completion.add(en.toString());
                    }
                } else if (args.get(2).equalsIgnoreCase("maxplayers")) {
                    completion.addAll(Arrays.asList("1", "2", "4", "8"));
                }
            }
            if (args.size() == 6 && args.get(2).equalsIgnoreCase("add")) {
                completion.addAll(Arrays.asList("1", "2", "4", "8"));
            }
        } else if (args.get(1).equalsIgnoreCase("jointeam")) {
            if (gc.containsKey(args.get(0))) {
                for (Team t : gc.get(args.get(0)).getGame().getTeams()) {
                    completion.add(t.name);
                }
            }
        }
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
            new BukkitRunnable() { // Fix loading plugin on CraftBukkit (non-Spigot) server
                public void run() {
                    TextComponent[] hoverComponent = new TextComponent[]{new TextComponent(click)};

                    TextComponent msg1 = new TextComponent(base);
                    msg1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bw admin " + game.getName() + " info base"));
                    msg1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
                    player.spigot().sendMessage(msg1);

                    TextComponent msg2 = new TextComponent(stores);
                    msg2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bw admin " + game.getName() + " info stores"));
                    msg2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
                    player.spigot().sendMessage(msg2);

                    TextComponent msg3 = new TextComponent(spawners);
                    msg3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bw admin " + game.getName() + " info spawners"));
                    msg3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
                    player.spigot().sendMessage(msg3);

                    TextComponent msg4 = new TextComponent(teams);
                    msg4.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bw admin " + game.getName() + " info teams"));
                    msg4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
                    player.spigot().sendMessage(msg4);

                    TextComponent msg5 = new TextComponent(config);
                    msg5.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bw admin " + game.getName() + " info config"));
                    msg5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
                    player.spigot().sendMessage(msg5);
                }
            }.runTask(Main.getInstance());
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
