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

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.commands.cheat.CheatAdminCommand;
import org.screamingsandals.bedwars.commands.cheat.CheatConsoleCommand;
import org.screamingsandals.bedwars.commands.migrate.MigrateBedWars1058Command;
import org.screamingsandals.bedwars.commands.migrate.MigrateBedWarsRelCommand;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.cloud.CloudConstructor;
import org.screamingsandals.lib.cloud.extras.MinecraftExceptionHandler;
import org.screamingsandals.lib.cloud.extras.MinecraftHelp;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;
import org.screamingsandals.lib.utils.annotations.methods.Provider;

@Service
@ServiceDependencies(dependsOn = {
        CloudConstructor.class
}, initAnother = {
        AddholoCommand.class,
        AdminCommand.class,
        AlljoinCommand.class,
        AutojoinCommand.class,
        CheatAdminCommand.class,
        CheatConsoleCommand.class,
        DumpCommand.class,
        HelpCommand.class,
        JoinCommand.class,
        LanguageCommand.class,
        LeaderboardCommand.class,
        LeaveCommand.class,
        ListCommand.class,
        MainlobbyCommand.class,
        PartyCommand.class,
        RejoinCommand.class,
        ReloadCommand.class,
        RemoveHoloCommand.class,
        StatsCommand.class,
        GamesInventoryCommand.class,
        NPCCommand.class,
        MigrateBedWarsRelCommand.class,
        MigrateBedWars1058Command.class,
        GroupCommand.class,
        JoinGroupCommand.class
})
@UtilityClass
public class CommandService {
    public static final MinecraftHelp.HelpColors DEFAULT_HELP_COLORS = MinecraftHelp.DEFAULT_HELP_COLORS;
    public static final int HEADER_FOOTER_LENGTH = 55;

    @Provider(level = Provider.Level.POST_ENABLE)
    public static CommandManager<CommandSender> provideCommandManager() {
        try {
            var manager = CloudConstructor.construct(CommandExecutionCoordinator.simpleCoordinator());

            new MinecraftExceptionHandler<CommandSender>()
                    .withDefaultHandlers()
                    .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION, (senderWrapper, e) ->
                            Message.of(LangKeys.NO_PERMISSIONS).defaultPrefix().getForJoined(senderWrapper)
                    )
                    .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, (senderWrapper, e) ->
                            Message.of(LangKeys.UNKNOWN_USAGE).defaultPrefix().getForJoined(senderWrapper)
                    )
                    .apply(manager, s -> s);

            return manager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
