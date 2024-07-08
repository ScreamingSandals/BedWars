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
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class ListCommand extends BaseCommand {
    public ListCommand() {
        super("list", BedWarsPermission.LIST_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            var sender = commandContext.getSender();
                            sender.sendMessage(Message.of(LangKeys.LIST_HEADER).defaultPrefix());
                            GameManagerImpl.getInstance().getGames().forEach(game ->
                                    sendGameState(game, sender)
                            );
                        })
        );
    }

    public static void sendGameState(Game game, CommandSender sender) {
        sender.sendMessage(Component
                .text()
                .content(game.getName())
                .color(game.getStatus() != GameStatus.REMOTE_UNKNOWN ? (game.getStatus() == GameStatus.DISABLED ? Color.RED : Color.GREEN) : Color.BLUE)
                .append(Component.text(" " + game.countConnectedPlayers(), Color.WHITE))
        );
    }
}
