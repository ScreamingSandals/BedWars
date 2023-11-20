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
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class DisplayNameCommand extends BaseAdminSubCommand {
    public DisplayNameCommand() {
        super("display-name");
    }

    @Override
    public void construct(CommandManager<CommandSender> manager, Command.Builder<CommandSender> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("set")
                        .argument(StringArgument.of("display-name", StringArgument.StringMode.GREEDY))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String displayName = commandContext.get("display-name");

                            game.setDisplayName(displayName);
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_DISPLAY_NAME_ENABLED).placeholder("display_name", displayName).defaultPrefix());
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("reset")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            game.setDisplayName(null);
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_DISPLAY_NAME_DISABLED).defaultPrefix());
                        }))
        );
    }
}
