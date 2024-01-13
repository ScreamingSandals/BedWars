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

package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class MinPlayersCommand extends BaseAdminSubCommand {
    public MinPlayersCommand() {
        super("minplayers");
    }

    @Override
    public void construct(CommandManager<CommandSender> manager, Command.Builder<CommandSender> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(IntegerArgument
                                    .<CommandSender>newBuilder("minPlayers")
                                    .withMin(2)
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            int minPlayers = commandContext.get("minPlayers");

                            if (minPlayers < 2) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_MIN_PLAYERS).defaultPrefix());
                                return;
                            }
                            game.setMinPlayers(minPlayers);
                            sender.sendMessage(
                                    Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_MIN_PLAYERS_SET)
                                            .placeholder("min", minPlayers)
                                            .defaultPrefix()
                            );
                        }))
        );
    }
}
