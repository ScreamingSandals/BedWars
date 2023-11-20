/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.target.TargetBlockImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.event.ClickEvent;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class InfoCommand extends BaseAdminSubCommand {
    public InfoCommand() {
        super("info");
    }

    @Override
    public void construct(CommandManager<CommandSender> manager, Command.Builder<CommandSender> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder.
                        literal("base").
                        handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var gameOpt = GameManagerImpl.getInstance().getGame(gameName);
                            if (gameOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                                return;
                            }

                            var game = gameOpt.get();

                            Message
                                    .of(LangKeys.ADMIN_INFO_HEADER)
                                    .defaultPrefix()
                                    .prefixPolicy(Message.PrefixPolicy.FIRST_MESSAGE)
                                    .join(LangKeys.ADMIN_INFO_NAME)
                                    .join(LangKeys.ADMIN_INFO_FILE)
                                    .join(LangKeys.ADMIN_INFO_STATUS)
                                    .join(LangKeys.ADMIN_INFO_WORLD)
                                    .placeholder("name", game.getName())
                                    .placeholder("file", game.getFile().getName())
                                    .placeholder("status", senderWrapper -> {
                                        switch (game.getStatus()) {
                                            case DISABLED:
                                                if (AdminCommand.gc.containsKey(gameName)) {
                                                    return Message.of(LangKeys.ADMIN_INFO_STATUS_DISABLED_IN_EDIT).getForJoined(senderWrapper);
                                                } else {
                                                    return Message.of(LangKeys.ADMIN_INFO_STATUS_DISABLED).getForJoined(senderWrapper);
                                                }
                                            case REBUILDING:
                                                return Message.of(LangKeys.ADMIN_INFO_STATUS_REBUILDING).getForJoined(senderWrapper);
                                            case RUNNING:
                                            case GAME_END_CELEBRATING:
                                                return Message.of(LangKeys.ADMIN_INFO_STATUS_RUNNING).getForJoined(senderWrapper);
                                            case WAITING:
                                                return Message.of(LangKeys.ADMIN_INFO_STATUS_WAITING).getForJoined(senderWrapper);
                                        }
                                        return Component.empty(); //what??
                                    })
                                    .placeholder("world", game.getWorld().getName())
                                    .send(sender);

                            var loc_pos1 = game.getPos1();
                            Message
                                    .of(LangKeys.ADMIN_INFO_POS1)
                                    .placeholder("x", loc_pos1.getX(), 2)
                                    .placeholder("y", loc_pos1.getY(), 2)
                                    .placeholder("z", loc_pos1.getZ(), 2)
                                    .placeholder("yaw", loc_pos1.getYaw(), 5)
                                    .placeholder("pitch", loc_pos1.getPitch(), 5)
                                    .placeholder("world", loc_pos1.getWorld().getName())
                                    .send(sender);

                            var loc_pos2 = game.getPos2();
                            Message
                                    .of(LangKeys.ADMIN_INFO_POS2)
                                    .placeholder("x", loc_pos2.getX(), 2)
                                    .placeholder("y", loc_pos2.getY(), 2)
                                    .placeholder("z", loc_pos2.getZ(), 2)
                                    .placeholder("yaw", loc_pos2.getYaw(), 5)
                                    .placeholder("pitch", loc_pos2.getPitch(), 5)
                                    .placeholder("world", loc_pos2.getWorld().getName())
                                    .send(sender);

                            var loc_spec = game.getSpecSpawn();
                            Message
                                    .of(LangKeys.ADMIN_INFO_SPEC)
                                    .placeholder("x", loc_spec.getX(), 2)
                                    .placeholder("y", loc_spec.getY(), 2)
                                    .placeholder("z", loc_spec.getZ(), 2)
                                    .placeholder("yaw", loc_spec.getYaw(), 5)
                                    .placeholder("pitch", loc_spec.getPitch(), 5)
                                    .placeholder("world", loc_spec.getWorld().getName())
                                    .send(sender);

                            var loc_lobby = game.getLobbySpawn();
                            Message
                                    .of(LangKeys.ADMIN_INFO_LOBBY)
                                    .placeholder("x", loc_lobby.getX(), 2)
                                    .placeholder("y", loc_lobby.getY(), 2)
                                    .placeholder("z", loc_lobby.getZ(), 2)
                                    .placeholder("yaw", loc_lobby.getYaw(), 5)
                                    .placeholder("pitch", loc_lobby.getPitch(), 5)
                                    .placeholder("world", loc_lobby.getWorld().getName())
                                    .send(sender);

                            Message
                                    .of(LangKeys.ADMIN_INFO_MIN_PLAYERS)
                                    .join(LangKeys.ADMIN_INFO_LOBBY_COUNTDOWN)
                                    .placeholder("minplayers", game.getMinPlayers())
                                    .placeholder("time", game.getPauseCountdown())
                                    .send(sender);

                            Message
                                    .of(LangKeys.ADMIN_INFO_GAME_TIME)
                                    .placeholder("time", game.getGameTime())
                                    .send(sender);

                            Message
                                    .of(LangKeys.ADMIN_INFO_POST_GAME_WAITING)
                                    .placeholder("time", game.getPostGameWaiting())
                                    .send(sender);
                        })
        );

        manager.command(
                commandSenderWrapperBuilder.
                        literal("teams").
                        handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var gameOpt = GameManagerImpl.getInstance().getGame(gameName);
                            if (gameOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                                return;
                            }

                            var game = gameOpt.get();

                            Message
                                    .of(LangKeys.ADMIN_INFO_HEADER)
                                    .defaultPrefix()
                                    .prefixPolicy(Message.PrefixPolicy.FIRST_MESSAGE)
                                    .join(LangKeys.ADMIN_INFO_TEAMS)
                                    .send(sender);

                            game.getTeams().forEach(team -> {
                                Message
                                        .of(LangKeys.ADMIN_INFO_TEAM)
                                        .placeholder("team", Component.text(team.getName(), team.getColor().getTextColor()))
                                        .placeholder("maxplayers", team.getMaxPlayers())
                                        .send(sender);

                                var loc_spawns = team.getTeamSpawns();
                                for (var loc_spawn : loc_spawns) {
                                    Message
                                            .of(LangKeys.ADMIN_INFO_TEAM_SPAWN)
                                            .placeholder("x", loc_spawn.getX(), 2)
                                            .placeholder("y", loc_spawn.getY(), 2)
                                            .placeholder("z", loc_spawn.getZ(), 2)
                                            .placeholder("yaw", loc_spawn.getYaw(), 5)
                                            .placeholder("pitch", loc_spawn.getPitch(), 5)
                                            .placeholder("world", loc_spawn.getWorld().getName())
                                            .send(sender);
                                }

                                if (team.getTarget() instanceof TargetBlockImpl) {
                                    var loc_target = ((TargetBlockImpl) team.getTarget()).getTargetBlock();
                                    Message
                                            .of(LangKeys.ADMIN_INFO_TEAM_TARGET)
                                            .placeholder("x", loc_target.getX(), 2)
                                            .placeholder("y", loc_target.getY(), 2)
                                            .placeholder("z", loc_target.getZ(), 2)
                                            .placeholder("yaw", loc_target.getYaw(), 5)
                                            .placeholder("pitch", loc_target.getPitch(), 5)
                                            .placeholder("world", loc_target.getWorld().getName())
                                            .send(sender);
                                }
                            });
                        })
        );

        manager.command(
                commandSenderWrapperBuilder.
                        literal("spawners").
                        handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var gameOpt = GameManagerImpl.getInstance().getGame(gameName);
                            if (gameOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                                return;
                            }

                            var game = gameOpt.get();

                            Message
                                    .of(LangKeys.ADMIN_INFO_HEADER)
                                    .join(LangKeys.ADMIN_INFO_SPAWNERS)
                                    .defaultPrefix()
                                    .prefixPolicy(Message.PrefixPolicy.FIRST_MESSAGE)
                                    .send(sender);

                            game.getSpawners().forEach(spawner -> {
                                var loc_spawner = spawner.getLocation();
                                var team = spawner.getTeam();

                                Component spawnerTeam;

                                if (team != null) {
                                    spawnerTeam = Component.text(team.getName(), team.getColor().getTextColor());
                                } else {
                                    spawnerTeam = Message.of(LangKeys.ADMIN_INFO_SPAWNER_NO_TEAM).asComponent(sender);
                                }

                                Message
                                        .of(LangKeys.ADMIN_INFO_SPAWNER)
                                        .placeholder("resource", spawner.getItemSpawnerType().getItemName())
                                        .placeholder("x", loc_spawner.getBlockX())
                                        .placeholder("y", loc_spawner.getBlockY())
                                        .placeholder("z", loc_spawner.getBlockZ())
                                        .placeholder("yaw", loc_spawner.getYaw())
                                        .placeholder("pitch", loc_spawner.getPitch())
                                        .placeholder("world", loc_spawner.getWorld().getName())
                                        .placeholder("team", spawnerTeam)
                                        .placeholder("holo", spawner.isHologramEnabled())
                                        .send(sender);
                            });
                        })
        );

        manager.command(
                commandSenderWrapperBuilder.
                        literal("stores").
                        handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var gameOpt = GameManagerImpl.getInstance().getGame(gameName);
                            if (gameOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                                return;
                            }

                            var game = gameOpt.get();

                            Message
                                    .of(LangKeys.ADMIN_INFO_HEADER)
                                    .join(LangKeys.ADMIN_INFO_VILLAGERS)
                                    .defaultPrefix()
                                    .prefixPolicy(Message.PrefixPolicy.FIRST_MESSAGE)
                                    .send(sender);

                            game.getGameStoreList().forEach(store -> {
                                var loc_store = store.getStoreLocation();
                                Message
                                        .of(LangKeys.ADMIN_INFO_VILLAGER_POS)
                                        .join(LangKeys.ADMIN_INFO_VILLAGER_ENTITY_TYPE)
                                        .placeholder("x", loc_store.getX(), 2)
                                        .placeholder("y", loc_store.getY(), 2)
                                        .placeholder("z", loc_store.getZ(), 2)
                                        .placeholder("yaw", loc_store.getYaw(), 5)
                                        .placeholder("pitch", loc_store.getPitch(), 5)
                                        .placeholder("world", loc_store.getWorld().getName())
                                        .placeholder("type", store.getEntityType().location().asString())
                                        .send(sender);

                                Message
                                        .of(LangKeys.ADMIN_INFO_VILLAGER_SHOP)
                                        .placeholder("bool", Message
                                                .of(store.getShopFile() != null
                                                        ? LangKeys.ADMIN_INFO_CONSTANT_TRUE
                                                        : LangKeys.ADMIN_INFO_CONSTANT_FALSE
                                                )
                                        )
                                        .send(sender);

                                if (store.getShopFile() != null) {
                                    Message
                                            .of(LangKeys.ADMIN_INFO_VILLAGER_SHOP_NAME)
                                            .placeholder("file", store.getShopFile())
                                            .placeholder("bool", Message
                                                    .of(
                                                            LangKeys.ADMIN_INFO_CONSTANT_FALSE
                                                    )
                                            )
                                            .send(sender);
                                }
                                Message
                                        .of(LangKeys.ADMIN_INFO_VILLAGER_SHOP_DEALER_NAME)
                                        .placeholder("name",
                                                store.getShopCustomName() != null
                                                        ? Component.fromMiniMessage(store.getShopCustomName())
                                                        : Message.of(LangKeys.ADMIN_INFO_VILLAGER_SHOP_DEALER_HAS_NO_NAME).asComponent(sender)

                                        )
                                        .send(sender);
                            });
                        })
        );

        manager.command(
                commandSenderWrapperBuilder.
                        literal("config").
                        handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var gameOpt = GameManagerImpl.getInstance().getGame(gameName);
                            if (gameOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                                return;
                            }

                            var game = gameOpt.get();

                            Message
                                    .of(LangKeys.ADMIN_INFO_HEADER)
                                    .join(LangKeys.ADMIN_INFO_CONSTANTS)
                                    .defaultPrefix()
                                    .prefixPolicy(Message.PrefixPolicy.FIRST_MESSAGE)
                                    .send(sender);

                            game.getConfigurationContainer().getRegisteredKeys().forEach(s -> {
                                var opt = game.getConfigurationContainer().get(s).orElse(null);
                                if (opt == null) {
                                    return;
                                }
                                var val = String.valueOf(opt.get());
                                Component valC = Component.text(val);
                                if (val.equalsIgnoreCase("true")) {
                                    valC = Message.of(LangKeys.ADMIN_INFO_CONSTANT_TRUE).asComponent(sender);
                                } else if (val.equalsIgnoreCase("false")) {
                                    valC = Message.of(LangKeys.ADMIN_INFO_CONSTANT_FALSE).asComponent(sender);
                                }
                                final var finalValC = valC;
                                Message
                                        .of(LangKeys.ADMIN_INFO_CONSTANT)
                                        .placeholder("constant", String.join(".", s.getKey()))
                                        .placeholder("value", senderWrapper -> {
                                            if (!opt.isSet()) {
                                                return Component.text()
                                                        .append(Message.of(LangKeys.ADMIN_INFO_CONSTANT_INHERIT).asComponent(sender))
                                                        .append(": ")
                                                        .append(finalValC)
                                                        .build();
                                            } else {
                                                return finalValC;
                                            }
                                        })
                                        .send(sender);
                            });

                            // NON-BOOLEAN CONSTANTS

                            var weather = game.getArenaWeather();

                            Message
                                    .of(LangKeys.ADMIN_INFO_CONSTANT)
                                    .placeholder("constant", "arenaWeather")
                                    .placeholder("value", weather != null ? weather.location().asString() : "default")
                                    .send(sender);
                        })
        );

        manager.command(
                commandSenderWrapperBuilder.
                        handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var gameOpt = GameManagerImpl.getInstance().getGame(gameName);
                            if (gameOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                                return;
                            }

                            Message
                                    .of(LangKeys.ADMIN_INFO_SELECT_HEADER)
                                    .defaultPrefix()
                                    .placeholder("arena", gameName)
                                    .send(sender);

                            Message
                                    .of(LangKeys.ADMIN_INFO_SELECT_BASE)
                                    .placeholder("command", Component
                                            .text()
                                            .content("/bw admin " + gameName + " info base")
                                            .clickEvent(ClickEvent.runCommand("/bw admin " + gameName + " info base"))
                                            .hoverEvent(Message
                                                    .of(LangKeys.ADMIN_INFO_SELECT_CLICK)
                                                    .placeholder("command", "/bw admin " + gameName + " info base")
                                                    .asComponent(sender)
                                            )
                                    )
                                    .send(sender);

                            Message
                                    .of(LangKeys.ADMIN_INFO_SELECT_STORES)
                                    .placeholder("command", Component
                                            .text()
                                            .content("/bw admin " + gameName + " info stores")
                                            .clickEvent(ClickEvent.runCommand("/bw admin " + gameName + " info stores"))
                                            .hoverEvent(Message
                                                    .of(LangKeys.ADMIN_INFO_SELECT_CLICK)
                                                    .placeholder("command", "/bw admin " + gameName + " info stores")
                                                    .asComponent(sender)
                                            )
                                    )
                                    .send(sender);

                            Message
                                    .of(LangKeys.ADMIN_INFO_SELECT_SPAWNERS)
                                    .placeholder("command", Component
                                            .text()
                                            .content("/bw admin " + gameName + " info spawners")
                                            .clickEvent(ClickEvent.runCommand("/bw admin " + gameName + " info spawners"))
                                            .hoverEvent(Message
                                                    .of(LangKeys.ADMIN_INFO_SELECT_CLICK)
                                                    .placeholder("command", "/bw admin " + gameName + " info spawners")
                                                    .asComponent(sender)
                                            )
                                    )
                                    .send(sender);

                            Message
                                    .of(LangKeys.ADMIN_INFO_SELECT_TEAMS)
                                    .placeholder("command", Component
                                            .text()
                                            .content("/bw admin " + gameName + " info teams")
                                            .clickEvent(ClickEvent.runCommand("/bw admin " + gameName + " info teams"))
                                            .hoverEvent(Message
                                                    .of(LangKeys.ADMIN_INFO_SELECT_CLICK)
                                                    .placeholder("command", "/bw admin " + gameName + " info teams")
                                                    .asComponent(sender)
                                            )
                                    )
                                    .send(sender);

                            Message
                                    .of(LangKeys.ADMIN_INFO_SELECT_CONFIG)
                                    .placeholder("command", Component
                                            .text()
                                            .content("/bw admin " + gameName + " info config")
                                            .clickEvent(ClickEvent.runCommand("/bw admin " + gameName + " info config"))
                                            .hoverEvent(Message
                                                    .of(LangKeys.ADMIN_INFO_SELECT_CLICK)
                                                    .placeholder("command", "/bw admin " + gameName + " info config")
                                                    .asComponent(sender)
                                            )
                                    )
                                    .send(sender);
                        })
        );
    }
}
