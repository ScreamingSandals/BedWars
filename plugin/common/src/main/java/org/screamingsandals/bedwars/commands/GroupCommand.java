/*
 * Copyright (C) 2022 ScreamingSandals
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
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.GroupManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.stream.Collectors;

@Service
public class GroupCommand extends BaseCommand {
    private final GroupManagerImpl groupManager;
    private final GameManagerImpl gameManager;

    public GroupCommand(GroupManagerImpl groupManager, GameManagerImpl gameManager) {
        super("group", BedWarsPermission.ADMIN_PERMISSION, false);
        this.groupManager = groupManager;
        this.gameManager = gameManager;
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("add")
                        .argument(StringArgument.<CommandSender>newBuilder("group")
                                .withSuggestionsProvider((c, s) ->
                                        groupManager.getExistingGroups()
                                )
                        )
                        .argument(StringArgument.<CommandSender>newBuilder("game")
                                .withSuggestionsProvider((c, s) ->
                                        gameManager.getGameNames()
                                )
                        )
                        .handler(commandContext -> {
                            String group = commandContext.get("group");
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            if (!GroupManagerImpl.GROUP_PATTERN.matcher(group).matches()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_GROUP_INVALID_GROUP_NAME).defaultPrefix().placeholderRaw("group", group));
                                return;
                            }

                            var game = gameManager.getGame(gameName);
                            if (game.isPresent()) {
                                //noinspection PatternValidation
                                groupManager.addToGroup(group, game.get());
                                sender.sendMessage(Message.of(LangKeys.ADMIN_GROUP_ADDED).defaultPrefix().placeholderRaw("group", group).placeholderRaw("game", game.get().getName()));
                            } else {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                            }
                        })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("remove")
                        .argument(StringArgument.<CommandSender>newBuilder("group")
                                .withSuggestionsProvider((c, s) ->
                                        groupManager.getExistingGroups()
                                )
                        )
                        .argument(StringArgument.<CommandSender>newBuilder("game")
                                .withSuggestionsProvider((c, s) ->
                                    groupManager.getGamesInGroup(c.get("group")).stream().map(GameImpl::getName).collect(Collectors.toList())
                                )
                        )
                        .handler(commandContext -> {
                            String group = commandContext.get("group");
                            String gameName = commandContext.get("game");
                            var sender = commandContext.getSender();

                            var game = gameManager.getGame(gameName);
                            if (game.isPresent()) {
                                //noinspection PatternValidation
                                groupManager.removeFromGroup(group, game.get());
                                sender.sendMessage(Message.of(LangKeys.ADMIN_GROUP_REMOVED).defaultPrefix().placeholderRaw("group", group).placeholderRaw("game", game.get().getName()));
                            } else {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                            }
                        })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("list")
                        .argument(StringArgument.<CommandSender>newBuilder("group")
                                .withSuggestionsProvider((c, s) ->
                                        groupManager.getExistingGroups()
                                )
                        )
                        .handler(commandContext -> {
                            String group = commandContext.get("group");
                            var sender = commandContext.getSender();

                            //noinspection PatternValidation
                            var games = groupManager.getGamesInGroup(group);
                            if (games.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_GROUP_EMPTY).defaultPrefix().placeholderRaw("group", group));
                            } else {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_GROUP_LIST).defaultPrefix().placeholderRaw("group", group));
                                games.forEach(game -> ListCommand.sendGameState(game, sender));
                            }
                        })
        );
    }
}
