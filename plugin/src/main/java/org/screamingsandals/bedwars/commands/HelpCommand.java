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
import org.screamingsandals.bedwars.VersionInfo;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.cloud.extras.MinecraftHelp;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class HelpCommand extends BaseCommand {
    public HelpCommand() {
        super("help", null, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        var minecraftHelp = MinecraftHelp.createNative("/bw help", manager)
                .commandFilter((command, sender) -> {
                    if ((command.getSenderType().isPresent() && !command.getSenderType().get().isInstance(sender)) || command.isHidden()) {
                        return false; // Cloud for some reason doesn't check sender type and hidden
                    }
                    return true; // Permissions will be resolved by Cloud itself
                })
                .setHeaderFooterLength(55)
                .messageProvider(MinecraftHelp::audienceMessageProvider)
                .setMessage(MinecraftHelp.MESSAGE_HELP_TITLE, Message.of(LangKeys.HELP_TITLE).placeholder("version", VersionInfo.VERSION))
                .setMessage(MinecraftHelp.MESSAGE_COMMAND, Message.of(LangKeys.HELP_MESSAGES_COMMAND))
                .setMessage(MinecraftHelp.MESSAGE_DESCRIPTION, Message.of(LangKeys.HELP_MESSAGES_DESCRIPTION))
                .setMessage(MinecraftHelp.MESSAGE_NO_DESCRIPTION, Message.of(LangKeys.HELP_MESSAGES_NO_DESCRIPTION))
                .setMessage(MinecraftHelp.MESSAGE_ARGUMENTS, Message.of(LangKeys.HELP_MESSAGES_ARGUMENTS))
                .setMessage(MinecraftHelp.MESSAGE_OPTIONAL, Message.of(LangKeys.HELP_MESSAGES_OPTIONAL))
                .setMessage(MinecraftHelp.MESSAGE_SHOWING_RESULTS_FOR_QUERY, Message.of(LangKeys.HELP_MESSAGES_SHOWING_RESULTS_FOR_QUERY))
                .setMessage(MinecraftHelp.MESSAGE_NO_RESULTS_FOR_QUERY, Message.of(LangKeys.HELP_MESSAGES_NO_RESULTS_FOR_QUERY))
                .setMessage(MinecraftHelp.MESSAGE_AVAILABLE_COMMANDS, Message.of(LangKeys.HELP_MESSAGES_AVAILABLE_COMMANDS))
                .setMessage(MinecraftHelp.MESSAGE_CLICK_TO_SHOW_HELP, Message.of(LangKeys.HELP_MESSAGES_CLICK_TO_SHOW_HELP))
                .setMessage(MinecraftHelp.MESSAGE_PAGE_OUT_OF_RANGE, Message.of(LangKeys.HELP_MESSAGES_PAGE_OUT_OF_RANGE))
                .setMessage(MinecraftHelp.MESSAGE_CLICK_FOR_NEXT_PAGE, Message.of(LangKeys.HELP_MESSAGES_CLICK_FOR_NEXT_PAGE))
                .setMessage(MinecraftHelp.MESSAGE_CLICK_FOR_PREVIOUS_PAGE, Message.of(LangKeys.HELP_MESSAGES_CLICK_FOR_PREVIOUS_PAGE));

        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument.optional("query", StringArgument.StringMode.GREEDY))
                        .hidden()
                        .handler(context -> minecraftHelp.queryCommands(context.getOrDefault("query", ""), context.getSender()))
        );

        // special case: help command
        manager.command(manager.commandBuilder("bw")
                .hidden()
                .handler(context -> minecraftHelp.queryCommands("", context.getSender()))
        );
    }
}
