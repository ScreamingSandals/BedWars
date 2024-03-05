/*
 * Copyright (C) 2024 ScreamingSandals
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

package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.remote.RemoteGameImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class RemoteAdminCommand extends BaseCommand {
    public RemoteAdminCommand() {
        super("remote-admin", BedWarsPermission.ADMIN_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        // add
        var baseAddCommand = commandSenderWrapperBuilder
                .literal("add")
                .argument(StringArgument.of("name"));
        manager.command(baseAddCommand
                .literal("server")
                .argument(StringArgument.of("server"))
                .handler(commandContext -> {
                    var sender = commandContext.getSender();
                    var name = commandContext.<String>get("name");
                    var server = commandContext.<String>get("server");

                    if (GameManagerImpl.getInstance().hasGame(name)) {
                        sender.sendMessage(Message.of(LangKeys.ADMIN_REMOTE_NAME_ALREADY_EXISTS).defaultPrefix().placeholder("name", name));
                    }

                    var game = RemoteGameImpl.createGame(name, server);
                    GameManagerImpl.getInstance().addGame(game);
                    sender.sendMessage(
                            Message.of(LangKeys.ADMIN_REMOTE_ADDED_SERVER)
                                    .defaultPrefix()
                                    .placeholder("name", name)
                                    .placeholder("uuid", game.getUuid().toString())
                                    .placeholder("server", server)
                    );
                })
        );

        manager.command(baseAddCommand
                .literal("server-game")
                .argument(StringArgument.of("server"))
                .argument(StringArgument.of("game"))
                .handler(commandContext -> {
                    var sender = commandContext.getSender();
                    var name = commandContext.<String>get("name");
                    var server = commandContext.<String>get("server");
                    var game = commandContext.<String>get("game");

                    if (GameManagerImpl.getInstance().hasGame(name)) {
                        sender.sendMessage(Message.of(LangKeys.ADMIN_REMOTE_NAME_ALREADY_EXISTS).defaultPrefix().placeholder("name", name));
                    }

                    var gameOb = RemoteGameImpl.createGame(name, server);
                    gameOb.setRemoteGameIdentifier(game);
                    GameManagerImpl.getInstance().addGame(gameOb);
                    sender.sendMessage(
                            Message.of(LangKeys.ADMIN_REMOTE_ADDED_SERVER_GAME)
                                    .defaultPrefix()
                                    .placeholder("name", name)
                                    .placeholder("uuid", gameOb.getUuid().toString())
                                    .placeholder("server", server)
                                    .placeholder("game", game)
                    );
                })
        );

        // list
        manager.command(commandSenderWrapperBuilder
                .literal("list")
                .handler(commandContext -> {
                    var sender = commandContext.getSender();

                    var games = GameManagerImpl.getInstance().getRemoteGames();

                    if (games.isEmpty()) {
                        sender.sendMessage(Message.of(LangKeys.ADMIN_REMOTE_EMPTY).defaultPrefix());
                        return;
                    }

                    sender.sendMessage(Message.of(LangKeys.ADMIN_REMOTE_LIST).defaultPrefix());

                    for (var game : games) {
                        sender.sendMessage(
                                Message.of(LangKeys.ADMIN_REMOTE_LIST_FORMAT)
                                        .placeholder("name", game.getName())
                                        .placeholder("uuid", game.getUuid().toString())
                                        .placeholder("server", game.getRemoteServer())
                                        .placeholder("game",
                                                game.getRemoteGameIdentifier() != null ? Component.text(game.getRemoteGameIdentifier()) : Message.of(LangKeys.ADMIN_REMOTE_LIST_FORMAT_NO_GAME)
                                        )
                        );
                    }
                })
        );

        // set
        var baseSetCommand = commandSenderWrapperBuilder
                .literal("set")
                .argument(StringArgument.<CommandSender>newBuilder("name")
                        .withSuggestionsProvider((ctx, s) -> GameManagerImpl.getInstance().getRemoteGameNames())
                );
        manager.command(baseSetCommand
                .literal("server")
                .argument(StringArgument.of("server"))
                .handler(commandContext -> {
                    var sender = commandContext.getSender();
                    var name = commandContext.<String>get("name");
                    var server = commandContext.<String>get("server");

                    var game = GameManagerImpl.getInstance().getRemoteGame(name);
                    if (game.isEmpty()) {
                        sender.sendMessage(Message.of(LangKeys.ADMIN_REMOTE_DOES_NOT_EXIST).defaultPrefix().placeholder("name", name));
                        return;
                    }

                    game.get().setRemoteServer(server);
                    sender.sendMessage(
                            Message.of(LangKeys.ADMIN_REMOTE_SET_SERVER)
                                    .defaultPrefix()
                                    .placeholder("name", name)
                                    .placeholder("uuid", game.get().getUuid().toString())
                                    .placeholder("server", server)
                    );
                })
        );

        manager.command(baseSetCommand
                .literal("server-game")
                .argument(StringArgument.of("server"))
                .argument(StringArgument.of("game"))
                .handler(commandContext -> {
                    var sender = commandContext.getSender();
                    var name = commandContext.<String>get("name");
                    var server = commandContext.<String>get("server");
                    var game = commandContext.<String>get("game");

                    var gameOb = GameManagerImpl.getInstance().getRemoteGame(name);
                    if (gameOb.isEmpty()) {
                        sender.sendMessage(Message.of(LangKeys.ADMIN_REMOTE_DOES_NOT_EXIST).defaultPrefix().placeholder("name", name));
                        return;
                    }

                    gameOb.get().setRemoteServer(server);
                    gameOb.get().setRemoteGameIdentifier(game);;
                    sender.sendMessage(
                            Message.of(LangKeys.ADMIN_REMOTE_SET_SERVER_GAME)
                                    .defaultPrefix()
                                    .placeholder("name", name)
                                    .placeholder("uuid", gameOb.get().getUuid().toString())
                                    .placeholder("server", server)
                                    .placeholder("game", game)
                    );
                })
        );

        // delete
        manager.command(commandSenderWrapperBuilder
                .literal("delete")
                .argument(StringArgument.<CommandSender>newBuilder("name")
                        .withSuggestionsProvider((ctx, s) -> GameManagerImpl.getInstance().getRemoteGameNames())
                )
                .handler(commandContext -> {
                    var sender = commandContext.getSender();
                    var name = commandContext.<String>get("name");

                    var game = GameManagerImpl.getInstance().getRemoteGame(name);
                    if (game.isEmpty()) {
                        sender.sendMessage(Message.of(LangKeys.ADMIN_REMOTE_DOES_NOT_EXIST).defaultPrefix().placeholder("name", name));
                        return;
                    }

                    GameManagerImpl.getInstance().removeGame(game.get());
                    sender.sendMessage(
                            Message.of(LangKeys.ADMIN_REMOTE_DELETED)
                                    .defaultPrefix()
                                    .placeholder("name", name)
                                    .placeholder("uuid", game.get().getUuid().toString())
                    );
                })
        );
    }
}
