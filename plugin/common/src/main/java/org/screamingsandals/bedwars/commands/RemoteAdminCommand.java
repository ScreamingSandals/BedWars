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
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.remote.RemoteGameImpl;
import org.screamingsandals.bedwars.game.remote.RemoteGameLoaderImpl;
import org.screamingsandals.bedwars.game.remote.RemoteGameStateManager;
import org.screamingsandals.bedwars.game.remote.protocol.ProtocolManagerImpl;
import org.screamingsandals.bedwars.game.remote.protocol.packets.GameListPacket;
import org.screamingsandals.bedwars.game.remote.protocol.packets.GameListRequestPacket;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.BungeeUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.utils.annotations.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RemoteAdminCommand extends BaseCommand {
    private final @NotNull RemoteGameStateManager stateStorage;
    private final @NotNull ProtocolManagerImpl protocolManager;

    public RemoteAdminCommand(@NotNull RemoteGameStateManager stateStorage, @NotNull ProtocolManagerImpl protocolManager) {
        super("remote-admin", BedWarsPermission.ADMIN_PERMISSION, true);
        this.stateStorage = stateStorage;
        this.protocolManager = protocolManager;
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        // add
        var baseAddCommand = commandSenderWrapperBuilder
                .literal("add")
                .argument(StringArgument.of("name"));

        var serverArgument = StringArgument.<CommandSender>newBuilder("server")
                .withSuggestionsProvider((context, s) -> {
                    var servers = BedWarsPlugin.getInstance().getBungeeServers();
                    if (servers != null) {
                        return servers;
                    }

                    if (context.getSender() instanceof Player) {
                        // ask for server list so we can autocomplete it next time
                        BungeeUtils.sendBungeeMessage((Player) context.getSender(), dataOutputStream -> {
                            dataOutputStream.writeUTF("GetServers");
                        });
                    }
                    return List.of();
                });

        var gameArgument = StringArgument.<CommandSender>newBuilder("game")
                .withSuggestionsProvider((context, s) -> {
                    @NotNull String server = context.get("server");

                    if (stateStorage.hasKnownGamesForServer(server)) {
                        return stateStorage.getKnownGames(server).stream().map(GameListPacket.GameEntry::getName).collect(Collectors.toList());
                    }

                    var servers = BedWarsPlugin.getInstance().getBungeeServers();
                    if (servers == null || !servers.contains(server)) {
                        return List.of();
                    }

                    try {
                        protocolManager.sendPacket(server, new GameListRequestPacket(BedWarsPlugin.getInstance().getServerName()));
                    } catch (IOException ignored) { // It is just autocomplete
                    }

                    return List.of();
                });

        manager.command(baseAddCommand
                .literal("server")
                .argument(serverArgument)
                .handler(commandContext -> {
                    var sender = commandContext.getSender();
                    var name = commandContext.<String>get("name");
                    var server = commandContext.<String>get("server");

                    if (GameManagerImpl.getInstance().hasGame(name)) {
                        sender.sendMessage(Message.of(LangKeys.ADMIN_REMOTE_NAME_ALREADY_EXISTS).defaultPrefix().placeholder("name", name));
                    }

                    var game = GameManagerImpl.getInstance().createNewRemoteGame(true, name, server, null);
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
                .argument(serverArgument)
                .argument(gameArgument)
                .handler(commandContext -> {
                    var sender = commandContext.getSender();
                    var name = commandContext.<String>get("name");
                    var server = commandContext.<String>get("server");
                    var game = commandContext.<String>get("game");

                    if (GameManagerImpl.getInstance().hasGame(name)) {
                        sender.sendMessage(Message.of(LangKeys.ADMIN_REMOTE_NAME_ALREADY_EXISTS).defaultPrefix().placeholder("name", name));
                    }

                    var gameOb = GameManagerImpl.getInstance().createNewRemoteGame(true, name, server, game);
                    RemoteGameStateManager.getInstance().subscribe(server, game);
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
                .argument(serverArgument)
                .handler(commandContext -> {
                    var sender = commandContext.getSender();
                    var name = commandContext.<String>get("name");
                    var server = commandContext.<String>get("server");

                    var game = GameManagerImpl.getInstance().getRemoteGame(name);
                    if (game.isEmpty()) {
                        sender.sendMessage(Message.of(LangKeys.ADMIN_REMOTE_DOES_NOT_EXIST).defaultPrefix().placeholder("name", name));
                        return;
                    }

                    var g = game.get();

                    var oldServer = g.getRemoteServer();
                    var oldIdentifier = g.getRemoteGameIdentifier();
                    if (oldIdentifier != null) {
                        RemoteGameStateManager.getInstance().unsubscribe(oldServer, oldIdentifier);
                    }
                    if (g instanceof RemoteGameImpl && ((RemoteGameImpl) g).getSaveFile() != null) {
                        RemoteGameLoaderImpl.getInstance().saveGame(g);
                    }

                    game.get().setRemoteServer(server);
                    sender.sendMessage(
                            Message.of(LangKeys.ADMIN_REMOTE_SET_SERVER)
                                    .defaultPrefix()
                                    .placeholder("name", name)
                                    .placeholder("uuid", g.getUuid().toString())
                                    .placeholder("server", server)
                    );
                })
        );

        manager.command(baseSetCommand
                .literal("server-game")
                .argument(serverArgument)
                .argument(gameArgument)
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

                    var g = gameOb.get();

                    var oldServer = g.getRemoteServer();
                    var oldIdentifier = g.getRemoteGameIdentifier();
                    if (oldIdentifier != null) {
                        RemoteGameStateManager.getInstance().unsubscribe(oldServer, oldIdentifier);
                    }

                    g.setRemoteServer(server);
                    g.setRemoteGameIdentifier(game);
                    RemoteGameStateManager.getInstance().subscribe(server, game);
                    if (g instanceof RemoteGameImpl && ((RemoteGameImpl) g).getSaveFile() != null) {
                        RemoteGameLoaderImpl.getInstance().saveGame(g);
                    }

                    sender.sendMessage(
                            Message.of(LangKeys.ADMIN_REMOTE_SET_SERVER_GAME)
                                    .defaultPrefix()
                                    .placeholder("name", name)
                                    .placeholder("uuid", g.getUuid().toString())
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
                    var oldServer = game.get().getRemoteServer();
                    var oldIdentifier = game.get().getRemoteGameIdentifier();
                    if (oldIdentifier != null) {
                        RemoteGameStateManager.getInstance().unsubscribe(oldServer, oldIdentifier);
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
