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
import org.screamingsandals.bedwars.commands.ConfigCommandFactory;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.parameters.DataFolder;

import java.nio.file.Path;

@Service
public class ConfigCommand extends BaseAdminSubCommand {
    private final Path shopFolder;

    public ConfigCommand(@DataFolder("shop") Path shopFolder) {
        super("config");
        this.shopFolder = shopFolder;
    }

    @Override
    public void construct(CommandManager<CommandSender> manager, Command.Builder<CommandSender> commandSenderWrapperBuilder) {
        ConfigCommandFactory.builder()
                .manager(manager)
                .commandBuilder(commandSenderWrapperBuilder)
                .resolver((context, viewOnly) -> {
                    var game = viewOnly ? viewMode(context) : editMode(context);
                    if (game != null) {
                        return ConfigCommandFactory.ResolverResult.builder()
                                .container(game.getConfigurationContainer())
                                .configuredComponentName(game.getName())
                                .build();
                    } else {
                        return ConfigCommandFactory.ResolverResult.builder()
                                .errorMessage(Message.of(LangKeys.ADMIN_ARENA_ERROR_ARENA_NOT_IN_EDIT).defaultPrefix())
                                .build();
                    }
                })
                .shopFolder(shopFolder)
                .build()
                .construct();
    }
}
