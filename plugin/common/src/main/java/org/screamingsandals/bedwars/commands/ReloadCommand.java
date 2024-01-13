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

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.PlatformService;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class ReloadCommand extends BaseCommand {
    public ReloadCommand() {
        super("reload", BedWarsPermission.ADMIN_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        manager.command(
                commandSenderWrapperBuilder
                    .handler(commandContext -> {
                        var sender = commandContext.getSender();

                        PlatformService.getInstance().reloadPlugin(sender);
                    })
        );
    }
}
