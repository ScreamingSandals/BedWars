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
import cloud.commandframework.arguments.standard.StringArgument;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class DisplayNameCommand extends BaseAdminSubCommand {
    public DisplayNameCommand() {
        super("displayName");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument.of("displayName", StringArgument.StringMode.GREEDY))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String displayName = commandContext.get("displayName");
                            if (displayName.trim().equalsIgnoreCase("off") || displayName.trim().isEmpty()) {
                                game.setDisplayName(null);
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_DISPLAY_NAME_DISABLED).defaultPrefix());
                            } else {
                                game.setDisplayName(AdventureHelper.translateAlternateColorCodes(LegacyComponentSerializer.AMPERSAND_CHAR, displayName));
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_DISPLAY_NAME_ENABLED).placeholder("display_name", displayName).defaultPrefix());
                            }
                        }))
        );
    }
}
