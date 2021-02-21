package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.ChatColor;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;

import static org.screamingsandals.bedwars.lib.lang.I.*;
import static org.screamingsandals.bedwars.lib.lang.I.i18nonly;

public class InfoCommand extends BaseAdminSubCommand {
    public InfoCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "info");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder.
                        literal("base").
                        handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var gameOpt = GameManager.getInstance().getGame(gameName);
                            if (gameOpt.isEmpty()) {
                                sender.sendMessage(i18n("no_arena_found"));
                                return;
                            }

                            var game = gameOpt.get();

                            sender.sendMessage(i18n("arena_info_header"));

                            sender.sendMessage(i18n("arena_info_name", false).replace("%name%", game.getName()));
                            sender.sendMessage(i18n("arena_info_file", false).replace("%file%", game.getFile().getName()));
                            var status = i18n("arena_info_status", false);
                            switch (game.getStatus()) {
                                case DISABLED:
                                    if (AdminCommand.gc.containsKey(gameName)) {
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
                            sender.sendMessage(status);

                            sender.sendMessage(
                                    i18n("arena_info_world", false).replace("%world%", game.getWorld().getName()));

                            var loc_pos1 = game.getPos1();
                            var pos1 = i18n("arena_info_pos1", false)
                                    .replace("%x%", Double.toString(loc_pos1.getX()))
                                    .replace("%y%", Double.toString(loc_pos1.getY()))
                                    .replace("%z%", Double.toString(loc_pos1.getZ()))
                                    .replace("%yaw%", Float.toString(loc_pos1.getYaw()))
                                    .replace("%pitch%", Float.toString(loc_pos1.getPitch()))
                                    .replace("%world%", loc_pos1.getWorld().getName());

                            sender.sendMessage(pos1);

                            var loc_pos2 = game.getPos2();
                            var pos2 = i18n("arena_info_pos2", false)
                                    .replace("%x%", Double.toString(loc_pos2.getX()))
                                    .replace("%y%", Double.toString(loc_pos2.getY()))
                                    .replace("%z%", Double.toString(loc_pos2.getZ()))
                                    .replace("%yaw%", Float.toString(loc_pos2.getYaw()))
                                    .replace("%pitch%", Float.toString(loc_pos2.getPitch()))
                                    .replace("%world%", loc_pos2.getWorld().getName());

                            sender.sendMessage(pos2);

                            var loc_spec = game.getSpecSpawn();
                            var spec = i18n("arena_info_spec", false)
                                    .replace("%x%", Double.toString(loc_spec.getX()))
                                    .replace("%y%", Double.toString(loc_spec.getY()))
                                    .replace("%z%", Double.toString(loc_spec.getZ()))
                                    .replace("%yaw%", Float.toString(loc_spec.getYaw()))
                                    .replace("%pitch%", Float.toString(loc_spec.getPitch()))
                                    .replace("%world%", loc_spec.getWorld().getName());

                            sender.sendMessage(spec);

                            var loc_lobby = game.getLobbySpawn();
                            var lobby = i18n("arena_info_lobby", false)
                                    .replace("%x%", Double.toString(loc_lobby.getX()))
                                    .replace("%y%", Double.toString(loc_lobby.getY()))
                                    .replace("%z%", Double.toString(loc_lobby.getZ()))
                                    .replace("%yaw%", Float.toString(loc_lobby.getYaw()))
                                    .replace("%pitch%", Float.toString(loc_lobby.getPitch()))
                                    .replace("%world%", loc_lobby.getWorld().getName());

                            sender.sendMessage(lobby);
                            sender.sendMessage(i18n("arena_info_min_players", false).replace("%minplayers%",
                                    Integer.toString(game.getMinPlayers())));
                            sender.sendMessage(i18n("arena_info_lobby_countdown", false).replace("%time%",
                                    Integer.toString(game.getPauseCountdown())));
                            sender.sendMessage(i18n("arena_info_game_time", false).replace("%time%",
                                    Integer.toString(game.getGameTime())));
                            m("arena_info_postgamewaiting").replace("time", game.getPostGameWaiting()).send(sender);
                        })
        );

        manager.command(
                commandSenderWrapperBuilder.
                        literal("teams").
                        handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var gameOpt = GameManager.getInstance().getGame(gameName);
                            if (gameOpt.isEmpty()) {
                                sender.sendMessage(i18n("no_arena_found"));
                                return;
                            }

                            var game = gameOpt.get();

                            sender.sendMessage(i18n("arena_info_header"));

                            sender.sendMessage(i18n("arena_info_teams", false));
                            game.getTeams().forEach(team -> {
                                sender.sendMessage(i18n("arena_info_team", false)
                                        .replace("%team%", team.color.chatColor.toString() + team.name)
                                        .replace("%maxplayers%", Integer.toString(team.maxPlayers)));

                                var loc_spawn = team.spawn;
                                var spawn = i18n("arena_info_team_spawn", false)
                                        .replace("%x%", Double.toString(loc_spawn.getX()))
                                        .replace("%y%", Double.toString(loc_spawn.getY()))
                                        .replace("%z%", Double.toString(loc_spawn.getZ()))
                                        .replace("%yaw%", Float.toString(loc_spawn.getYaw()))
                                        .replace("%pitch%", Float.toString(loc_spawn.getPitch()))
                                        .replace("%world%", loc_spawn.getWorld().getName());

                                sender.sendMessage(spawn);

                                var loc_target = team.bed;
                                var target = i18n("arena_info_team_target", false)
                                        .replace("%x%", Double.toString(loc_target.getX()))
                                        .replace("%y%", Double.toString(loc_target.getY()))
                                        .replace("%z%", Double.toString(loc_target.getZ()))
                                        .replace("%yaw%", Float.toString(loc_target.getYaw()))
                                        .replace("%pitch%", Float.toString(loc_target.getPitch()))
                                        .replace("%world%", loc_target.getWorld().getName());

                                sender.sendMessage(target);
                            });
                        })
        );

        manager.command(
                commandSenderWrapperBuilder.
                        literal("spawners").
                        handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var gameOpt = GameManager.getInstance().getGame(gameName);
                            if (gameOpt.isEmpty()) {
                                sender.sendMessage(i18n("no_arena_found"));
                                return;
                            }

                            var game = gameOpt.get();

                            sender.sendMessage(i18n("arena_info_header"));

                            sender.sendMessage(i18n("arena_info_spawners", false));
                            game.getSpawners().forEach(spawner -> {
                                var loc_spawner = spawner.loc;
                                var team = spawner.getTeam().orElse(null);

                                String spawnerTeam;

                                if (team != null) {
                                    spawnerTeam = TeamColor.fromApiColor(team.getColor()).chatColor + team.getName();
                                } else {
                                    spawnerTeam = i18nonly("arena_info_spawner_no_team");
                                }

                                var spawnerM = i18n("arena_info_spawner", false)
                                        .replace("%resource%", spawner.type.getItemName())
                                        .replace("%x%", String.valueOf(loc_spawner.getBlockX()))
                                        .replace("%y%", String.valueOf(loc_spawner.getBlockY()))
                                        .replace("%z%", String.valueOf(loc_spawner.getBlockZ()))
                                        .replace("%yaw%", String.valueOf(loc_spawner.getYaw()))
                                        .replace("%pitch%", String.valueOf(loc_spawner.getPitch()))
                                        .replace("%world%", loc_spawner.getWorld().getName())
                                        .replace("%team%", spawnerTeam)
                                        .replace("%holo%", String.valueOf(spawner.getHologramEnabled()));


                                sender.sendMessage(spawnerM);
                            });
                        })
        );

        manager.command(
                commandSenderWrapperBuilder.
                        literal("stores").
                        handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var gameOpt = GameManager.getInstance().getGame(gameName);
                            if (gameOpt.isEmpty()) {
                                sender.sendMessage(i18n("no_arena_found"));
                                return;
                            }

                            var game = gameOpt.get();

                            sender.sendMessage(i18n("arena_info_header"));

                            sender.sendMessage(i18n("arena_info_villagers", false));
                            game.getGameStoreList().forEach(store -> {
                                var loc_store = store.getStoreLocation();
                                var storeM = i18n("arena_info_villager_pos", false)
                                        .replace("%x%", Double.toString(loc_store.getX()))
                                        .replace("%y%", Double.toString(loc_store.getY()))
                                        .replace("%z%", Double.toString(loc_store.getZ()))
                                        .replace("%yaw%", Float.toString(loc_store.getYaw()))
                                        .replace("%pitch%", Float.toString(loc_store.getPitch()))
                                        .replace("%world%", loc_store.getWorld().getName());

                                sender.sendMessage(storeM);

                                var storeM2 = i18n("arena_info_villager_entity_type", false).replace("%type%",
                                        store.getEntityType().name());
                                sender.sendMessage(storeM2);

                                var storeM3 = i18n("arena_info_villager_shop", false).replace("%bool%",
                                        store.getShopFile() != null ? i18n("arena_info_config_true", false)
                                                : i18n("arena_info_config_false", false));
                                sender.sendMessage(storeM3);
                                if (store.getShopFile() != null) {
                                    var storeM4 = i18n("arena_info_villager_shop_name", false)
                                            .replace("%file%", store.getShopFile()).replace("%bool%",
                                                    store.getUseParent() ? i18n("arena_info_config_true", false)
                                                            : i18n("arena_info_config_false", false));
                                    sender.sendMessage(storeM4);
                                }
                                var storeM5 = i18nonly("arena_info_villager_shop_dealer_name").replace("%name%",
                                        store.isShopCustomName() ? store.getShopCustomName()
                                                : i18nonly("arena_info_villager_shop_dealer_has_no_name"));
                                sender.sendMessage(storeM5);
                            });
                        })
        );

        manager.command(
                commandSenderWrapperBuilder.
                        literal("config").
                        handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var gameOpt = GameManager.getInstance().getGame(gameName);
                            if (gameOpt.isEmpty()) {
                                sender.sendMessage(i18n("no_arena_found"));
                                return;
                            }

                            var game = gameOpt.get();

                            sender.sendMessage(i18n("arena_info_header"));

                            sender.sendMessage(i18n("arena_info_config", false));

                            var msg = m("arena_info_config_constant");

                            game.getConfigurationContainer().getRegisteredKeys().forEach(s -> {
                                var opt = game.getConfigurationContainer().get(s, Object.class).get();
                                msg.replace("constant", s);
                                var val = String.valueOf(opt.get());
                                if (val.equalsIgnoreCase("true")) {
                                    val = i18nonly("arena_info_config_true");
                                } else if (val.equalsIgnoreCase("false")) {
                                    val = i18nonly("arena_info_config_false");
                                }
                                if (!opt.isSet()) {
                                    msg.replace("value", i18nonly("arena_info_config_inherit") + ChatColor.RESET + val);
                                } else {
                                    msg.replace("value", ChatColor.RESET + val);
                                }
                                msg.send(sender);
                            });

                            // NON-BOOLEAN CONSTANTS

                            sender.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "arenaTime").replace("%value%", game.getArenaTime().name()));

                            sender.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "arenaWeather")
                                    .replace("%value%", game.getArenaWeather() != null ? game.getArenaWeather().name()
                                            : "default"));

                            sender.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "lobbybossbarcolor").replace("%value%",
                                            game.getLobbyBossBarColor() != null ? game.getLobbyBossBarColor().name()
                                                    : "default"));

                            sender.sendMessage(i18n("arena_info_config_constant", false)
                                    .replace("%constant%", "gamebossbarcolor").replace("%value%",
                                            game.getGameBossBarColor() != null ? game.getGameBossBarColor().name()
                                                    : "default"));
                        })
        );

        manager.command(
                commandSenderWrapperBuilder.
                        handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var gameOpt = GameManager.getInstance().getGame(gameName);
                            if (gameOpt.isEmpty()) {
                                sender.sendMessage(i18n("no_arena_found"));
                                return;
                            }

                            sender.sendMessage(i18n("please_select_info_type").replace("%arena%", gameName));

                            sender.sendMessage(AdventureHelper
                                    .toComponent(i18n("please_select_info_type_base", false).replace("%arena%", gameName))
                                    .clickEvent(ClickEvent.runCommand("/bw admin " + gameName + " info base"))
                            );

                            sender.sendMessage(AdventureHelper
                                    .toComponent(i18n("please_select_info_type_stores", false).replace("%arena%", gameName))
                                    .clickEvent(ClickEvent.runCommand("/bw admin " + gameName + " info stores"))
                            );

                            sender.sendMessage(AdventureHelper
                                    .toComponent(i18n("please_select_info_type_spawners", false).replace("%arena%", gameName))
                                    .clickEvent(ClickEvent.runCommand("/bw admin " + gameName + " info spawners"))
                            );

                            sender.sendMessage(AdventureHelper
                                    .toComponent(i18n("please_select_info_type_teams", false).replace("%arena%", gameName))
                                    .clickEvent(ClickEvent.runCommand("/bw admin " + gameName + " info teams"))
                            );

                            sender.sendMessage(AdventureHelper.
                                    toComponent(i18n("please_select_info_type_config", false).replace("%arena%", gameName))
                                    .clickEvent(ClickEvent.runCommand("/bw admin " + gameName + " info config"))
                            );
                        })
        );
    }
}
