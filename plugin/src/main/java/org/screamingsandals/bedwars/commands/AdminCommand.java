package org.screamingsandals.bedwars.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.ArenaTime;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.game.*;
import org.screamingsandals.bedwars.utils.Permissions;
import org.screamingsandals.lib.debug.Debug;
import org.screamingsandals.lib.screamingcommands.base.annotations.RegisterCommand;
import org.screamingsandals.lib.screamingcommands.base.interfaces.IBasicCommand;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

import static misat11.lib.lang.I.m;
import static misat11.lib.lang.I.mpr;

/**
 * @author ScreamingSandals team
 */
@RegisterCommand(commandName = "bw", subCommandName = "admin")
public class AdminCommand implements IBasicCommand {
    private HashMap<String, GameCreator> gameCreator = new HashMap<>();

    public AdminCommand(Main main) {
    }

    @Override
    public String getPermission() {
        return Permissions.ADMIN_PERMISSIONS.permission;
    }

    @Override
    public String getDescription() {
        return "BedWars admin command";
    }

    @Override
    public String getUsage() {
        return "/bw admin";
    }

    @Override
    public String getInvalidUsageMessage() {
        return mpr("commands.errors.unknown_usage").get();
    }

    @Override
    public boolean onPlayerCommand(Player player, List<String> args) {
        Debug.info(args.size() + " is args size in AdminCommand");

        if (args.size() >= 2) {
            String arenaName = args.get(0);
            if (args.get(1).equalsIgnoreCase("info")) {
                if (Main.isGameExists(arenaName)) {
                    Game game = Main.getGame(arenaName);
                    if (args.size() >= 3) {
                        if (args.get(2).equalsIgnoreCase("base")) {
                            m("commands.admin.arena.info.header").send(player);
                            m("commands.admin.arena.info.name").send(player).replace("%name%", game.getName());
                            m("commands.admin.arena.info.status", false).replace("%status%", game.getGameStatusString()).send(player);
                            m("commands.admin.arena.info.world", false).replace("%world%", game.getWorld().getName()).send(player);

                            Location loc_pos1 = game.getPos1();
                            m("commands.admin.arena.info.pos1", false)
                                    .replace("%x%", Double.toString(loc_pos1.getX()))
                                    .replace("%y%", Double.toString(loc_pos1.getY()))
                                    .replace("%z%", Double.toString(loc_pos1.getZ()))
                                    .replace("%yaw%", Float.toString(loc_pos1.getYaw()))
                                    .replace("%pitch%", Float.toString(loc_pos1.getPitch()))
                                    .replace("%world%", loc_pos1.getWorld().getName())
                                    .send(player);

                            Location loc_pos2 = game.getPos2();
                            m("commands.admin.arena.info.pos2", false)
                                    .replace("%x%", Double.toString(loc_pos2.getX()))
                                    .replace("%y%", Double.toString(loc_pos2.getY()))
                                    .replace("%z%", Double.toString(loc_pos2.getZ()))
                                    .replace("%yaw%", Float.toString(loc_pos2.getYaw()))
                                    .replace("%pitch%", Float.toString(loc_pos2.getPitch()))
                                    .replace("%world%", loc_pos2.getWorld().getName())
                                    .send(player);

                            Location loc_spec = game.getSpecSpawn();
                            m("commands.admin.arena.info.spectator", false)
                                    .replace("%x%", Double.toString(loc_spec.getX()))
                                    .replace("%y%", Double.toString(loc_spec.getY()))
                                    .replace("%z%", Double.toString(loc_spec.getZ()))
                                    .replace("%yaw%", Float.toString(loc_spec.getYaw()))
                                    .replace("%pitch%", Float.toString(loc_spec.getPitch()))
                                    .replace("%world%", loc_spec.getWorld().getName())
                                    .send(player);

                            Location loc_lobby = game.getLobbySpawn();
                            m("commands.admin.arena.info.lobby", false)
                                    .replace("%x%", Double.toString(loc_lobby.getX()))
                                    .replace("%y%", Double.toString(loc_lobby.getY()))
                                    .replace("%z%", Double.toString(loc_lobby.getZ()))
                                    .replace("%yaw%", Float.toString(loc_lobby.getYaw()))
                                    .replace("%pitch%", Float.toString(loc_lobby.getPitch()))
                                    .replace("%world%", loc_lobby.getWorld().getName())
                                    .send(player);

                            m("commands.admin.arena.info.min_players", false).replace("%minplayers%",
                                    Integer.toString(game.getMinPlayers())).send(player);
                            m("commands.admin.arena.info.countdown_time", false).replace("%minplayers%",
                                    Integer.toString(game.getPauseCountdown())).send(player);
                            m("commands.admin.arena.info.game_length", false).replace("%minplayers%",
                                    Integer.toString(game.getGameTime())).send(player);

                        } else if (args.get(2).equalsIgnoreCase("teams")) {
                            m("commands.admin.arena.info.header").send(player);
                            m("commands.admin.arena.info.teams.header").send(player);

                            for (Team team : game.getTeams()) {
                                m("commands.admin.arena.info.teams.info")
                                        .replace("%team%", team.color.chatColor.toString() + team.name)
                                        .replace("%maxplayers%", Integer.toString(team.maxPlayers))
                                        .send(player);

                                Location loc_spawn = team.spawn;
                                m("commands.admin.arena.info.teams.spawn", false)
                                        .replace("%x%", Double.toString(loc_spawn.getX()))
                                        .replace("%y%", Double.toString(loc_spawn.getY()))
                                        .replace("%z%", Double.toString(loc_spawn.getZ()))
                                        .replace("%yaw%", Float.toString(loc_spawn.getYaw()))
                                        .replace("%pitch%", Float.toString(loc_spawn.getPitch()))
                                        .replace("%world%", loc_spawn.getWorld().getName())
                                        .send(player);

                                Location loc_target = team.bed;
                                m("commands.admin.arena.info.teams.target", false)
                                        .replace("%x%", Double.toString(loc_target.getX()))
                                        .replace("%y%", Double.toString(loc_target.getY()))
                                        .replace("%z%", Double.toString(loc_target.getZ()))
                                        .replace("%yaw%", Float.toString(loc_target.getYaw()))
                                        .replace("%pitch%", Float.toString(loc_target.getPitch()))
                                        .replace("%world%", loc_target.getWorld().getName())
                                        .send(player);
                            }
                        } else if (args.get(2).equalsIgnoreCase("spawners")) {
                            m("commands.admin.arena.info.header").send(player);
                            m("commands.admin.arena.info.spawners.header").send(player);

                            for (ItemSpawner spawner : game.getSpawners()) {
                                Location loc_spawner = spawner.loc;
                                org.screamingsandals.bedwars.api.Team team = spawner.getTeam();

                                DecimalFormat numFormat = new DecimalFormat("##");
                                String spawnerTeam;

                                if (team != null) {
                                    spawnerTeam = TeamColor.fromApiColor(team.getColor()).chatColor + team.getName();
                                } else {
                                    spawnerTeam = m("commands.admin.arena.info.spawners.no_team").get();
                                }

                                m("commands.admin.arena.info.spawners.info", false)
                                        .replace("%resource%", spawner.type.getItemName())
                                        .replace("%x%", numFormat.format(loc_spawner.getX()))
                                        .replace("%y%", numFormat.format(loc_spawner.getY()))
                                        .replace("%z%", numFormat.format(loc_spawner.getZ()))
                                        .replace("%yaw%", numFormat.format(loc_spawner.getYaw()))
                                        .replace("%pitch%", numFormat.format(loc_spawner.getPitch()))
                                        .replace("%world%", loc_spawner.getWorld().getName())
                                        .replace("%team%", spawnerTeam)
                                        .replace("%holo%", String.valueOf(spawner.getHologramEnabled()))
                                        .send(player);
                            }

                        } else if (args.get(2).equalsIgnoreCase("stores")) {
                            m("commands.admin.arena.info.header").send(player);
                            m("commands.admin.arena.info.stores.header").send(player);

                            for (GameStore store : game.getGameStores()) {
                                Location loc_store = store.getStoreLocation();
                                m("commands.admin.arena.info.store.position", false)
                                        .replace("%x%", Double.toString(loc_store.getX()))
                                        .replace("%y%", Double.toString(loc_store.getY()))
                                        .replace("%z%", Double.toString(loc_store.getZ()))
                                        .replace("%yaw%", Float.toString(loc_store.getYaw()))
                                        .replace("%pitch%", Float.toString(loc_store.getPitch()))
                                        .replace("%world%", loc_store.getWorld().getName())
                                        .send(player);

                                m("commands.admin.arena.info.store.entity_type", false).replace("%type%",
                                        store.getEntityType().name()).send(player);

                                m("commands.admin.arena.info.store.custom_shop", false).replace("%type%",
                                        store.getEntityType().name()).send(player);

                                m("commands.admin.arena.info.store.custom_shop", false).replace("%bool%",
                                        store.getShopFile() != null ? m("commands.admin.arena.info.true_value", false).get()
                                                : m("commands.admin.arena.info.false_value", false).get()).send(player);

                                if (store.getShopFile() != null) {
                                    m("commands.admin.arena.info.store.custom_shop", false).replace("%file%",
                                            store.getUseParent() ? m("commands.admin.arena.info.true_value", false).get()
                                                    : m("commands.admin.arena.info.false_value", false).get()).send(player);
                                }

                                m("commands.admin.arena.info.store.name_above", false).replace("%name%",
                                        store.getUseParent() ? store.getShopCustomName()
                                                : m("commands.admin.arena.info.store.no_name_above", false).get()).send(player);
                            }
                        } else if (args.get(2).equalsIgnoreCase("config")) {
                            m("commands.admin.arena.info.header").send(player);
                            m("commands.admin.arena.info.config.header").send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "compassEnabled")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getCompassEnabled().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "joinRandomTeamAfterLobby")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getJoinRandomTeamAfterLobby().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "joinRandomTeamOnJoin")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getJoinRandomTeamOnJoin().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "addWoolToInventoryOnJoin")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getAddWoolToInventoryOnJoin().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "preventKillingVillagers")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getPreventKillingVillagers().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "playerDrops")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getPlayerDrops().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "friendlyfire")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant" + game.getFriendlyfire().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "coloredLeatherByTeamInLobby")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getColoredLeatherByTeamInLobby().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "keepInventory")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getKeepInventory().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "crafting")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getCrafting().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "gameScoreboard")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getScoreboard().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "lobbyScoreboard")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getLobbyScoreboard().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "gameBossbar")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getGameBossbar().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "lobbyScoreboard")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getLobbyBossbar().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "preventSpawningMobs")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getPreventSpawningMobs().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "spawnerholograms")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getSpawnerHolograms().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "spawnerDisableMerge")
                                    .replace("%value%", m("commands.admin.arena.info.config.constant"
                                            + game.getSpawnerDisableMerge().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "give gameStartItems")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getGameStartItems().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "give playerRespawnItems")
                                    .replace("%value%", m("commands.admin.arena.info.config.constant"
                                            + game.getPlayerRespawnItems().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "spawnerHologramsCountdown")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getSpawnerHologramsCountdown().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "damageWhenPlayerIsNotInArena")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getDamageWhenPlayerIsNotInArena().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "removeunusedtargetblocks")
                                    .replace("%value%",
                                            m("commands.admin.arena.info.config.constant"
                                                    + game.getRemoveUnusedTargetBlocks().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "allowblockfalling")
                                    .replace("%value%", m("commands.admin.arena.info.config.constant"
                                            + game.getAllowBlockFalling().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "holoAboveBed")
                                    .replace("%value%", m("commands.admin.arena.info.config.constant"
                                            + game.getHoloAboveBed().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "spectatorJoin")
                                    .replace("%value%", m("commands.admin.arena.info.config.constant"
                                            + game.getSpectatorJoin().name().toLowerCase(), false).get())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "stopTeamSpawnersOnDie")
                                    .replace("%value%", m("commands.admin.arena.info.config.constant"
                                            + game.getStopTeamSpawnersOnDie().name().toLowerCase(), false).get())
                                    .send(player);

                            // NON-BOOLEAN CONSTANTS

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "arenaTime")
                                    .replace("%value%", game.getArenaTime().name())
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "arenaWeather")
                                    .replace("%value%",
                                            game.getArenaWeather() != null ? game.getArenaWeather().name() : "default")
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "lobbybossbarcolor")
                                    .replace("%value%",
                                            game.getLobbyBossBarColor() != null ? game.getLobbyBossBarColor().name() : "default")
                                    .send(player);

                            m("commands.admin.arena.info.config.constant", false)
                                    .replace("%constant%", "gamebossbarcolor")
                                    .replace("%value%",
                                            game.getGameBossBarColor() != null ? game.getGameBossBarColor().name() : "default")
                                    .send(player);
                        } else {
                            sendInfoSelectType(player, game);
                        }
                    } else {
                        sendInfoSelectType(player, game);
                    }
                } else {
                    mpr("commands.admin.arena.errors.not_found").send(player);
                }
            } else if (args.get(1).equalsIgnoreCase("add")) {
                if (Main.isGameExists(arenaName)) {
                    mpr("commands.admin.add.already_exists").send(player);
                } else if (gameCreator.containsKey(arenaName)) {
                    mpr("commands.admin.add.not_saved").send(player);
                } else {
                    GameCreator creator = new GameCreator(Game.createGame(arenaName));
                    gameCreator.put(arenaName, creator);
                    mpr("commands.admin.add.added").send(player);
                }
            } else if (args.get(1).equalsIgnoreCase("remove")) {
                if (Main.isGameExists(arenaName)) {
                    if (!gameCreator.containsKey(arenaName)) {
                        mpr("commands.admin.arena.errors.not_in_edit_mode").send(player);
                    } else {
                        gameCreator.remove(arenaName);
                        new File(Main.getInstance().getDataFolder(), "arenas/" + arenaName + ".yml").delete();
                        Main.removeGame(Main.getGame(arenaName));
                        mpr("commands.admin.remove.removed").send(player);
                    }
                } else if (gameCreator.containsKey(arenaName)) {
                    gameCreator.remove(arenaName);
                    mpr("commands.admin.remove.removed").send(player);
                } else {
                    mpr("commands.admin.arena.errors.not_found").send(player);
                }
            } else if (args.get(1).equalsIgnoreCase("edit")) {
                if (Main.isGameExists(arenaName)) {
                    Game game = Main.getGame(arenaName);
                    game.stop();
                    gameCreator.put(arenaName, new GameCreator(game));
                    mpr("commands.admin.edit.switched").send(player);
                } else {
                    mpr("commands.admin.arena.errors.not_found").send(player);
                }
            } else {
                if (gameCreator.containsKey(arenaName)) {
                    List<String> gameCreatorArgs = new ArrayList<>(args);
                    int argsCounter = 0;
                    for (String arg : args) {
                        if (argsCounter >= 2) {
                            gameCreatorArgs.add(arg);
                        }
                        argsCounter++;
                    }

                    boolean isArenaSaved = gameCreator.get(arenaName).cmd(player, args.get(1),
                            gameCreatorArgs.toArray(new String[0]));
                    if (args.get(1).equalsIgnoreCase("save") && isArenaSaved) {
                        gameCreator.remove(arenaName);
                    }
                } else {
                    mpr("commands.admin.arena.errors.not_in_edit_mode").send(player);
                }
            }
        } else {
            mpr("commands.admin.usage").send(player);
        }
        return true;
    }


    @Override
    public boolean onConsoleCommand(ConsoleCommandSender consoleCommandSender, List<String> args) {
        mpr("commands.errors.not_for_console").send(consoleCommandSender);
        return true;
    }

    @Override
    public void onPlayerTabComplete(Player player, Command command, List<String> completion, List<String> args) {
        if (args.size() == 1) {
            completion.addAll(Main.getGameNames());
            for (String arena : gameCreator.keySet()) {
                if (!completion.contains(arena)) {
                    completion.add(arena);
                }
            }
        } else if (args.size() == 2) {
            completion.addAll(Arrays.asList("add", "lobby", "spec", "pos1", "pos2", "pausecountdown", "team", "spawner",
                    "time", "store", "save", "remove", "edit", "jointeam", "minplayers", "info", "config", "arenatime",
                    "arenaweather", "lobbybossbarcolor", "gamebossbarcolor"));
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
                completion.addAll(Arrays.asList("add", "remove", "type"));
            }
            if (args.size() == 4 && args.get(2).equalsIgnoreCase("type")) {
                for (EntityType type : EntityType.values()) {
                    if (type.isAlive()) {
                        completion.add(type.name());
                    }
                }
            }
            if (args.size() == 4 && args.get(2).equalsIgnoreCase("add")) {
                completion.addAll(Arrays.asList("Villager_shop", "Dealer", "Seller", "&a&lVillager_shop"));
            }
            if (args.size() == 5 && args.get(2).equalsIgnoreCase("add")) {
                completion.addAll(Collections.singletonList("<Shop File>"));
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
                        "spectatorjoin", "stopTeamSpawnersOnDie"));
            }
            if (args.size() == 4) {
                completion.addAll(Arrays.asList("true", "false", "inherit"));
            }
        } else if (args.get(1).equalsIgnoreCase("spawner")) {
            if (args.size() == 3) {
                completion.addAll(Arrays.asList("add", "reset"));
            }
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
                if (gameCreator.containsKey(args.get(0))) {
                    for (Team t : gameCreator.get(args.get(0)).getGame().getTeams()) {
                        completion.add(t.name);
                    }
                }
            }
            if (args.size() == 8 || args.size() == 9) {
                completion.addAll(Arrays.asList("5", "10", "20"));
            }
        } else if (args.get(1).equalsIgnoreCase("team")) {
            if (args.size() == 3) {
                completion.addAll(Arrays.asList("add", "color", "maxplayers", "spawn", "bed", "remove"));
            }
            if (args.size() == 4) {
                if (gameCreator.containsKey(args.get(0))) {
                    for (Team t : gameCreator.get(args.get(0)).getGame().getTeams()) {
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
            if (gameCreator.containsKey(args.get(0))) {
                for (Team t : gameCreator.get(args.get(0)).getGame().getTeams()) {
                    completion.add(t.name);
                }
            }
        }
    }

    @Override
    public void onConsoleTabComplete(ConsoleCommandSender consoleCommandSender, Command command, List<String> completion, List<String> args) {
    }


    private void sendInfoSelectType(Player player, Game game) {
        String select = m("commands.admin.arena.info_sender.header")
                .replace("%arena%", game.getName()).get();
        String base = m("commands.admin.arena.info_sender.base", false)
                .replace("%arena%", game.getName()).get();
        String stores = m("commands.admin.arena.info_sender.stores", false)
                .replace("%arena%", game.getName()).get();
        String spawners = m("commands.admin.arena.info_sender.spawners", false)
                .replace("%arena%", game.getName()).get();
        String teams = m("commands.admin.arena.info_sender.teams", false)
                .replace("%arena%", game.getName()).get();
        String config = m("commands.admin.arena.info_sender.config", false)
                .replace("%arena%", game.getName()).get();
        String click = m("commands.admin.arena.info_sender.click", false).get();

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
