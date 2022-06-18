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
import org.screamingsandals.bedwars.game.GroupManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class JoinGroupCommand extends BaseCommand {
    private final GroupManagerImpl groupManager;

    public JoinGroupCommand(GroupManagerImpl groupManager) {
        super("join-group", BedWarsPermission.JOIN_GROUP_PERMISSION, false);
        this.groupManager = groupManager;
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("group")
                                .withSuggestionsProvider((c, s) ->
                                        groupManager.getExistingGroups()
                                )
                        )
                        .handler(commandContext -> {
                            String group = commandContext.get("group");

                            var sender = commandContext.getSender().as(PlayerWrapper.class);
                            var player = sender.as(PlayerWrapper.class);
                            if (PlayerManagerImpl.getInstance().isPlayerInGame(sender)) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_ALREADY_IN_GAME).defaultPrefix());
                                return;
                            }

                            var games = groupManager.getGamesInGroup(group);

                            if (!games.isEmpty()) {
                                MiscUtils.getGameWithHighestPlayers(games, false).ifPresentOrElse(
                                        game1 -> game1.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)),
                                        () -> sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix())
                                );
                            } else {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                            }
                        })
        );
    }
}
