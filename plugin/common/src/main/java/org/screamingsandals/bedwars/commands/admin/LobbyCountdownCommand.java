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

package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class LobbyCountdownCommand extends BaseAdminSubCommand {
    public LobbyCountdownCommand() {
        super("lobby-countdown");
    }

    @Override
    public void construct(CommandManager<CommandSender> manager, Command.Builder<CommandSender> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(IntegerArgument
                                .<CommandSender>newBuilder("countdown")
                                .withMin(10)
                                .withMax(600)
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            int countdown = commandContext.get("countdown");

                            if (countdown >= 10 && countdown <= 600) {
                                game.setPauseCountdown(countdown);
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_LOBBY_COUNTDOWN_SET).placeholder("countdown", countdown).defaultPrefix());
                                return;
                            }
                            sender.sendMessage(Message
                                    .of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_COUNTDOWN)
                                    .defaultPrefix()
                                    .placeholder("lowest", 10)
                                    .placeholder("highest", 600)
                            );
                        }))
        );
    }
}
