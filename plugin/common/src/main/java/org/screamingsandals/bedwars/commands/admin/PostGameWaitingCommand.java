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
public class PostGameWaitingCommand extends BaseAdminSubCommand {
    public PostGameWaitingCommand() {
        super("postgamewaiting");
    }

    @Override
    public void construct(CommandManager<CommandSender> manager, Command.Builder<CommandSender> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(IntegerArgument
                                .<CommandSender>newBuilder("time")
                                .withMin(0)
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            int time = commandContext.get("time");

                            if (time >= 0) {
                                game.setPostGameWaiting(time);
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_POST_GAME_WAITING).defaultPrefix().placeholder("number", time));
                                return;
                            }
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_TIME).defaultPrefix().placeholder("number", time));
                        }))
        );
    }
}
