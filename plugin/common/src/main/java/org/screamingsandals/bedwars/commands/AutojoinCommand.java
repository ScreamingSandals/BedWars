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
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class AutojoinCommand extends BaseCommand {
    public AutojoinCommand() {
        super("autojoin", BedWarsPermission.AUTOJOIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(commandContext -> {
                            var player = commandContext.getSender().as(Player.class);
                            if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                                player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_ALREADY_IN_GAME).defaultPrefix());
                                return;
                            }

                            GameManagerImpl.getInstance().getGameWithHighestPlayers(false).ifPresentOrElse(
                                    game -> game.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)),
                                    () -> player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_THERE_IS_NO_EMPTY_GAME).defaultPrefix())
                            );
                        })
        );
    }
}
