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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class ListCommand extends BaseCommand {
    public ListCommand() {
        super("list", BedWarsPermission.LIST_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            var sender = commandContext.getSender();
                            sender.sendMessage(Message.of(LangKeys.LIST_HEADER).defaultPrefix());
                            GameManagerImpl.getInstance().getGames().forEach(game ->
                                    sender.sendMessage(Component
                                            .text(game.getName())
                                            .color(game.getStatus() == GameStatus.DISABLED ? NamedTextColor.RED : NamedTextColor.GREEN)
                                            .append(Component
                                                    .text(" " + game.countPlayers())
                                                    .color(NamedTextColor.WHITE)
                                            )
                                    )
                            );
                        })
        );
    }
}
