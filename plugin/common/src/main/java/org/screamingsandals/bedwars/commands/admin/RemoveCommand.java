/*
 * Copyright (C) 2025 ScreamingSandals
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
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class RemoveCommand extends BaseAdminSubCommand {
    public RemoveCommand() {
        super("remove");
    }

    @Override
    public void construct(CommandManager<CommandSender> manager, Command.Builder<CommandSender> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                .handler(commandContext -> {
                    String gameName = commandContext.get("game");
                    var sender = commandContext.getSender();

                    GameManagerImpl.getInstance().getLocalGame(gameName).ifPresentOrElse(game -> {
                        if (!AdminCommand.gc.containsKey(gameName)) {
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_ERROR_ARENA_MUST_BE_IN_EDIT_MODE).defaultPrefix());
                        } else {
                            AdminCommand.gc.remove(gameName);
                            var file = game.getFile();
                            if (file != null) {
                                file.delete();
                            }
                            GameManagerImpl.getInstance().removeGame(game);
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_SUCCESS_REMOVED).defaultPrefix());
                        }
                    }, () -> {
                        if (AdminCommand.gc.containsKey(gameName)) {
                            AdminCommand.gc.remove(gameName);
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_SUCCESS_REMOVED).defaultPrefix());
                        } else {
                            sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                        }
                    });
                })
        );
    }
}
